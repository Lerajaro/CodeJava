package kiggs;
/* This class is supposed to have all the funcitonalities to take a dataset 
and perform risk analysis to it
and save the summarized results in the csv-file @ Constants.ANALYSIS_PATH
Perspecitvely it will output detailed results also each in a single file, of which the name will be saved in the analysis-csv
*/

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.sql.Timestamp;
import java.util.Arrays;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased.Order;
import org.deidentifier.arx.common.Groupify;
import org.deidentifier.arx.common.TupleWrapper;
import org.deidentifier.arx.criteria.AverageReidentificationRisk;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.risk.RiskEstimateBuilder;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.ARXPopulationModel.Region;
import org.deidentifier.arx.risk.RiskModelSampleRisks;
import org.deidentifier.arx.risk.RiskModelSampleUniqueness;
import org.deidentifier.arx.risk.RiskModelHistogram;
import org.deidentifier.arx.risk.RiskModelPopulationUniqueness;
import org.deidentifier.arx.risk.RiskModelPopulationUniqueness.PopulationUniquenessModel;
import org.deidentifier.arx.metric.Metric;

public class RiskEstimator extends Example {
    private static int[] QI_RESOLUTION = {0, 0, 0, 0, 0, 0};
    
    private enum AttributeCategory {
        QUASI_IDENTIFYING, IDENTIFYING, SENSITIVE, INSENSITIVE
    }

    public static void main(String[] args) {
    }
 
    public static void RiskEstimation() throws IOException {
        try{
            QI_RESOLUTION = Constants.getQIResolution();

            Constants.DATA.getHandle().release();
            
            
            setGeneralizationLevel(Constants.DATA); // has to be set anew in each iteration with different values.
             // Analyze Data
            ARXConfiguration config = setConfiguration();

            ARXAnonymizer anonymizer = new ARXAnonymizer(); // Create an instance of the anonymizer

            ARXResult result = anonymizer.anonymize(Constants.DATA, config);
            
            if (result.isResultAvailable()) { // making sure, that the code doesn't break because of bad settings for privacy model and parameters 
                csvCreator3(result);
                // System.out.println("\nOutput Statistics after Anonymization");
                // printResult(result, Constants.DATA);
            }
            else {
                System.out.println("No Result available. Anonymizing goal not reached.");
            }
            

            System.out.println("\nAll Done! Back to Controller and next Iteration\n\n------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[][] defineAttributes(Data data) {
        System.out.println("\nSetting categories to attributes...");
        String[] identifyers = {};
        String[] quasiIdentifiers = Constants.QUASI_IDENTIFIER_CHOICE;
        String[] sensitives = Constants.SENSITIVES_CHOICE;
        String[] insensitives = {};
        String[][] variableTypes = {quasiIdentifiers, identifyers, sensitives, insensitives};
        for (AttributeCategory category : AttributeCategory.values()) {
            String[] variables = variableTypes[category.ordinal()];
            for (int i = 0; i < variables.length; i++) {
                String variable = variables[i];
                if (QI_RESOLUTION[i] != -1) { // All QI's that are not chosen are set to -1.
                    setAttributeType(data, variable, category);
                }
            }
        }
        return variableTypes; // this method could possibly return void....
    }

    private static void setAttributeType(Data data, String variableName, AttributeCategory category) {
        AttributeType attributeType;
        switch (category) {
            case QUASI_IDENTIFYING:
                attributeType = AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
                break;
            case IDENTIFYING:
                attributeType = AttributeType.IDENTIFYING_ATTRIBUTE;
                break;
            case SENSITIVE:
                attributeType = AttributeType.SENSITIVE_ATTRIBUTE;
                break;
            case INSENSITIVE:
                attributeType = AttributeType.INSENSITIVE_ATTRIBUTE;
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute category");
        }
        data.getDefinition().setAttributeType(variableName, attributeType);
    }

    public static void setHierarchy(Data data){
        try {
            System.out.println("Setting Hierarchies to according Quasi-Identifying attributes...");
            for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
                switch (attribute) {
                    case "age2":
                        data.getDefinition().setAttributeType(attribute, Hierarchy.create(Constants.HIERARCHY_PATH + "age.csv", StandardCharsets.UTF_8, ';'));
                        break;
                    case "sex":
                        data.getDefinition().setAttributeType(attribute, Hierarchy.create(Constants.HIERARCHY_PATH + "geschlecht.csv", StandardCharsets.UTF_8, ';'));
                        break;
                    case "ppoint":
                        // data.getDefinition().setAttributeType("Inzidenzort", Hierarchy.create(Constants.HIERARCHY_PATH + "inzidenzort.csv", StandardCharsets.UTF_8, ';'));
                        data.getDefinition().setAttributeType(attribute, redationBasedHierarchy(attribute));
                        break;
                    case "sexa":
                        data.getDefinition().setAttributeType(attribute, Hierarchy.create(Constants.HIERARCHY_PATH + "geschlecht.csv", StandardCharsets.UTF_8, ';'));
                        break;
                    case "agegrpK":
                        // date(data, "Geburtsdatum");                        
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid attribute for Hierarchy Settings");
                }
            }
        } catch (IOException e) {
            // Handle the IOException, e.g., print an error message or log the details
            System.err.println("Error creating hierarchies:");
            e.printStackTrace();
            // You can also throw a custom exception or handle it as needed
        }  
    }

    public static Hierarchy redationBasedHierarchy(String attribute) {
        System.out.println("Establishing a redaction Based Hierarchy for attribute: " + attribute + " ...");
        // Create the builder
        HierarchyBuilderRedactionBased<?> builder = HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
                                                                                    Order.RIGHT_TO_LEFT,
                                                                                    ' ', '*');
        int colIndex = Constants.DATA.getHandle().getColumnIndexOf(attribute);
        String[] values = Constants.DATA.getHandle().getDistinctValues(colIndex);
        builder.prepare(values);  
        AttributeType.Hierarchy hierarchy = builder.build();
        return hierarchy;
    }

    private static void setGeneralizationLevel(Data data){
        System.out.println("Setting Generalization Levels to choice of Quasi_Identifiers...");
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            int index = Arrays.asList(Constants.QUASI_IDENTIFIER_FULL_SET).indexOf(attribute);
            if (index > 0 && index < QI_RESOLUTION.length){
                data.getDefinition().setMinimumGeneralization(attribute, QI_RESOLUTION[index]);
                data.getDefinition().setMaximumGeneralization(attribute, QI_RESOLUTION[index]);
            }
        }
    }

    private static ARXConfiguration setConfiguration() {
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new AverageReidentificationRisk(1d));
        config.setSuppressionLimit(1d);
        config.addPrivacyModel(new KAnonymity(3));
        // config.addPrivacyModel(new HierarchicalDistanceTCloseness(Constants.SENSITIVES_CHOICE[0], 0.6d, Constants.DATA.getDefinition().getHierarchyObject(Constants.SENSITIVES_CHOICE[0])));
        // config.addPrivacyModel(new RecursiveCLDiversity(Constants.SENSITIVES_CHOICE[0], 3d, 2));
        config.setQualityModel(Metric.createEntropyMetric());
        config.setSuppressionLimit(1d); // Recommended default: 1d
        config.setAttributeWeight(Constants.QUASI_IDENTIFIER_CHOICE[0], 0.5d); // attribute weight
        config.setAttributeWeight(Constants.QUASI_IDENTIFIER_CHOICE[1], 0.3d); // attribute weight
        config.setAttributeWeight(Constants.QUASI_IDENTIFIER_CHOICE[2], 1d); // attribute weight
        config.setQualityModel(Metric.createLossMetric(0.5d)); // suppression/generalization-factor
        return config;
    }

    private static Double[] getRisksFromHandle(DataHandle dataHandle) {
        // Perform risk analysis and other operations
        System.out.println("\nAnalyzing Risks from current Data Handle...");
        ARXPopulationModel populationmodel = ARXPopulationModel.create(Region.GERMANY);
        RiskEstimateBuilder builder = dataHandle.getRiskEstimator(populationmodel);
        RiskModelSampleRisks sampleReidentifiationRisk = builder.getSampleBasedReidentificationRisk();
        Double averageRisk = sampleReidentifiationRisk.getAverageRisk();
        Double lowestRisk = sampleReidentifiationRisk.getLowestRisk();
        Double highestRisk = sampleReidentifiationRisk.getHighestRisk();
        Double[] risks = {averageRisk, lowestRisk, highestRisk};
        return risks;
    }
        
    private static void csvCreator3(ARXResult result) {
        String outputFile = Constants.ANALYSIS_FOLDER + Constants.ANALYSIS_PATH_2;

        try (FileWriter writer = new FileWriter(outputFile, true)) { // Append mode
            StringBuilder line = new StringBuilder();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            line.append(timestamp).append(", ")
                .append(Constants.FILE_NAME_PREFIX).append(", ")
                .append(Constants.DATA.getHandle().getNumRows()).append(", ")
                .append(result.getGlobalOptimum().getLowestScore()).append( ", ");
            
            for (int i = 0; i < Constants.getQIResolution().length; i++) {
                
                line.append(Constants.getQIResolution()[i]);
                
                line.append(", ");
            }
            
            Double[] risks = getRisksFromHandle(result.getOutput());
            int[] equivalenceClasses = getEquivalenceClassStatistics2(result.getOutput());

            for (Double risk : risks) {
                line.append(risk).append(", ");
            }

            for (int equivalenceClass : equivalenceClasses) {
                line.append(equivalenceClass).append(", ");
            }

            if (line.length() > 0) {
                line.delete(line.length() - 2, line.length());
            }


            writer.write(line.toString() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Perform risk analysis
     * @param handle
     */
    public static void analyzeData(DataHandle handle) {
        
        ARXPopulationModel populationmodel = ARXPopulationModel.create(Region.USA);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelHistogram classes = builder.getEquivalenceClassModel();
        RiskModelSampleRisks sampleReidentifiationRisk = builder.getSampleBasedReidentificationRisk();
        RiskModelSampleUniqueness sampleUniqueness = builder.getSampleBasedUniquenessRisk();
        RiskModelPopulationUniqueness populationUniqueness = builder.getPopulationBasedUniquenessRisk();
        
        int[] histogram = classes.getHistogram();
        
        System.out.println("   * Equivalence classes:");
        System.out.println("     - Average size: " + classes.getAvgClassSize());
        System.out.println("     - Num classes : " + classes.getNumClasses());
        System.out.println("     - Histogram   :");
        for (int i = 0; i < histogram.length; i += 2) {
            System.out.println("        [Size: " + histogram[i] + ", count: " + histogram[i + 1] + "]");
        }
        System.out.println("   * Risk estimates:");
        System.out.println("     - Sample-based measures");
        System.out.println("       + Average risk     : " + sampleReidentifiationRisk.getAverageRisk());
        System.out.println("       + Lowest risk      : " + sampleReidentifiationRisk.getLowestRisk());
        System.out.println("       + Tuples affected  : " + sampleReidentifiationRisk.getFractionOfRecordsAffectedByLowestRisk());
        System.out.println("       + Highest risk     : " + sampleReidentifiationRisk.getHighestRisk());
        System.out.println("       + Tuples affected  : " + sampleReidentifiationRisk.getFractionOfRecordsAffectedByHighestRisk());
        System.out.println("       + Sample uniqueness: " + sampleUniqueness.getFractionOfUniqueRecords());
        System.out.println("     - Population-based measures");
        System.out.println("       + Population unqiueness (Zayatz): " + populationUniqueness.getFractionOfUniqueTuples(PopulationUniquenessModel.ZAYATZ));
    }

    public static int[] getEquivalenceClassStatistics2(DataHandle handle) {
        
        
        // Prepare
        Set<String> attributes = handle.getDefinition().getQuasiIdentifyingAttributes();
        final int[] indices = new int[attributes.size()];
        int index = 0;
        for (int column = 0; column < handle.getNumColumns(); column++) {
            if (attributes.contains(handle.getAttributeName(column))) {
                indices[index++] = column;
            }
        }

        // Calculate equivalence classes
        int capacity = handle.getNumRows() / 10;
        capacity = capacity > 10 ? capacity : 10;
        Groupify<TupleWrapper> map = new Groupify<TupleWrapper>(capacity);
        int numberOfSuppressedRecords = 0;
        int numberOfRecordsSuppressedRecords = 0;
        for (int row = 0; row < handle.getNumRows(); row++) {

            numberOfRecordsSuppressedRecords++;
            if (handle.isOutlier(row)) {
                numberOfSuppressedRecords++;
            } else {
                TupleWrapper tuple = new TupleWrapper(handle, indices, row);
                map.add(tuple);
            }
            ;
        }
        
        // Now compute the following values
        int averageEquivalenceClassSize = 0;
        int maximalEquivalenceClassSize = Integer.MIN_VALUE;
        int minimalEquivalenceClassSize = Integer.MAX_VALUE;
        int numberOfEquivalenceClasses = map.size();
        
        // Let's do it
        Groupify.Group<TupleWrapper> element = map.first();
        while (element != null) {
            
            maximalEquivalenceClassSize = Math.max(element.getCount(), maximalEquivalenceClassSize);
            minimalEquivalenceClassSize = Math.min(element.getCount(), minimalEquivalenceClassSize);
            averageEquivalenceClassSize += element.getCount();
            element = element.next();
        }
        
        // Calculate average
        averageEquivalenceClassSize /= (double)numberOfEquivalenceClasses;
        
        // Fix corner cases
        if (numberOfEquivalenceClasses == 0) {
            averageEquivalenceClassSize = 0;
            maximalEquivalenceClassSize = 0;
            minimalEquivalenceClassSize = 0;
        }

        int[] result = new int[] {averageEquivalenceClassSize, maximalEquivalenceClassSize, numberOfEquivalenceClasses,
        numberOfRecordsSuppressedRecords,
        numberOfSuppressedRecords};
        // And return
        return  result;
    }

}

