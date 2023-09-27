/* This class is supposed to have all the funcitonalities to take a dataset of zfkd data
and perform risk analysis to it
and save the results in the analysis.csv (later maybe the analysis2.csv)
plus store the detailed results in a single file, of which the name is saved in analysis.csv
*/

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Arrays;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.aggregates.HierarchyBuilderDate;
import org.deidentifier.arx.aggregates.HierarchyBuilderDate.Granularity;
import org.deidentifier.arx.criteria.AverageReidentificationRisk;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelAttributes;
import org.deidentifier.arx.risk.RiskModelAttributes.QuasiIdentifierRisk;

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

public class RiskEstimator extends Example {
    private static int[] QI_RESOLUTION = {0, 0, 0, 0, 0, 0};
    
    private enum AttributeCategory {
        QUASI_IDENTIFYING, IDENTIFYING, SENSITIVE, INSENSITIVE
    }

    public static void main(String[] args) {
    }

    public static void RiskEstimation(int[] qiResolution) throws IOException {
        try{
            QI_RESOLUTION = qiResolution;
            Data data = Data.create(Constants.FOLDER_PATH + Constants.FILE_PATH, StandardCharsets.UTF_8, ',');
            
            defineAttributes(data); 
            setHierarchy(data); // 
            setGeneralizationLevel(data); 

            System.out.println("\nNow testing data Definitions: ");
            DataDefinition dataDefinition = data.getDefinition();
            TesterMethods.testDefineAttributes(dataDefinition);
            TesterMethods.testHierarchyBuildingSuccess(dataDefinition);
            TesterMethods.testGeneralizationSuccess(dataDefinition); // Print out what the generalization minimum for each attribute ist. 
            TesterMethods.testQIGeneralization(dataDefinition);

            System.out.println("\n - Quasi-identifiers sorted by risk:");
            analyzeAttributes(data.getHandle());
            System.out.println("\n - Risk analysis:");
            analyzeData(data.getHandle());

            // Create an instance of the anonymizer
            ARXAnonymizer anonymizer = new ARXAnonymizer();
            ARXConfiguration config = ARXConfiguration.create();
            
            config.addPrivacyModel(new AverageReidentificationRisk(0.5d));
            config.setSuppressionLimit(0d);
            
            ARXResult result = anonymizer.anonymize(data, config);
            System.out.println("\nNow testing result Definitions:");
            DataDefinition resultDefinition = result.getDataDefinition();
            TesterMethods.testDefineAttributes(resultDefinition);
            TesterMethods.testHierarchyBuildingSuccess(resultDefinition);
            TesterMethods.testGeneralizationSuccess(resultDefinition); // Print out what the generalization minimum for each attribute ist. 
            TesterMethods.testQIGeneralization(resultDefinition);
            
            System.out.println("\nNow testing result Output Definitions:");
            System.out.println("\nOutput Data:\n" + result.getOutput());
            System.out.println("\nInput Data:\n" + result.getInput());
            System.out.println("\nConfiguration:\n" + result.getConfiguration());

            DataDefinition resultOutputDefinition = result.getOutput().getDefinition();
            TesterMethods.testDefineAttributes(resultOutputDefinition);
            TesterMethods.testHierarchyBuildingSuccess(resultOutputDefinition);
            TesterMethods.testGeneralizationSuccess(resultOutputDefinition); // Print out what the generalization minimum for each attribute ist. 
            TesterMethods.testQIGeneralization(resultOutputDefinition);

            System.out.println("resultDefinition == resultOUtputDefinition: " + (resultOutputDefinition == resultDefinition));

            result.getOutput().getRiskEstimator().getAttributeRisks();
            
            // Perform risk analysis
            Double[] risks = getRisksFromHandle(result.getOutput());

            // Create a new Output FileName
            String outputFile = RiskAnalysisManager.getNewFileName();
            System.out.println("Output Filename is: " + outputFile); // not needed, unless outputStream is being acitvated, but is buggy at the moment
            // Creating a new file with detailed risk analysis
            // outputStream(data, outputFile);
            // Writing a new line to the analysis.csv
            csvCreator2(Constants.FILE_NAME_PREFIX, Constants.DATA_SIZE, outputFile, QI_RESOLUTION, risks);
            System.out.println("\nDone! Back to Controller and Iteration over QI_Resolutions\n------------------\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static String[][] defineAttributes(Data data) {
        String[] identifyers = {};
        String[] quasiIdentifiers = Constants.QUASI_IDENTIFIER_CHOICE;
        String[] sensitives = Constants.SENSITIVES_CHOICE;
        String[] insensitives = {};
        String[][] variableTypes = {quasiIdentifiers, identifyers, sensitives, insensitives};

        for (AttributeCategory category : AttributeCategory.values()) {
            String[] variables = variableTypes[category.ordinal()];
            for (int i = 0; i < variables.length; i++) {
                String variable = variables[i];
                if (QI_RESOLUTION[i] != -1) {
                    setAttributeType(data, variable, category);
                    System.out.println("Variable " + variable + " is now set to " + category);    
                }
            }
        }
        System.out.println("Done");
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

    private static void setHierarchy(Data data){
        try {
            // Define hierarchies
            // date(data, "Geburtsdatum");
            // date(data, "Diagnosedatum");
            data.getDefinition().setAttributeType("Age", Hierarchy.create("hierarchies2/age.csv", StandardCharsets.UTF_8, ';'));
            data.getDefinition().setAttributeType("Geschlecht", Hierarchy.create("hierarchies2/geschlecht.csv", StandardCharsets.UTF_8, ';'));
            //ICD10CodeHierarchy builderInzidenzort =  ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort"));
            data.getDefinition().setAttributeType("Inzidenzort", Hierarchy.create("hierarchies2/inzidenzort.csv", StandardCharsets.UTF_8, ';'));
            // data.getDefinition().setAttributeType("Inzidenzort", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort")));
            data.getDefinition().setAttributeType("Diagnose_ICD10_Code", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Diagnose_ICD10_Code")));
        } catch (IOException e) {
            // Handle the IOException, e.g., print an error message or log the details
            System.err.println("Error creating hierarchies:");
            e.printStackTrace();
            // You can also throw a custom exception or handle it as needed
        }  
        System.out.println("Hierarchies set.");
    }

    private static void setGeneralizationLevel(Data data){
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            int index = Arrays.asList(Constants.QUASI_IDENTIFIER_FULL_SET).indexOf(attribute);

            if (index > 0 && index < QI_RESOLUTION.length){
                //config.setMinimumGeneralizationLevel(attribute, QI_RESOLUTION[index]);
                data.getDefinition().setMinimumGeneralization(attribute, QI_RESOLUTION[index]);
                data.getDefinition().setMaximumGeneralization(attribute, QI_RESOLUTION[index]);
            }
        }
    }

    private static Double[] getRisksFromHandle(DataHandle dataHandle) {
        // Perform risk analysis and other operations

        ARXPopulationModel populationmodel = ARXPopulationModel.create(Region.GERMANY);
        RiskEstimateBuilder builder = dataHandle.getRiskEstimator(populationmodel);
        RiskModelSampleRisks sampleReidentifiationRisk = builder.getSampleBasedReidentificationRisk();
        Double averageRisk = sampleReidentifiationRisk.getAverageRisk();
        Double lowestRisk = sampleReidentifiationRisk.getLowestRisk();
        Double highestRisk = sampleReidentifiationRisk.getHighestRisk();
        Double[] risks = {averageRisk, lowestRisk, highestRisk};
        return risks;
    }

    private static void outputStream(Data data, String outputFile) {
  
        try {
            // Redirect standard output
            PrintStream originalOut = System.out;  // Store original standard output
            
            // Redirect standard output to a ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            // Perform risk analysis and other operations
            String[][] variableTypes = defineAttributes(data);
            System.out.println("\n - Quasi-identifyers:");
            for (String quasiIdentifyer : variableTypes[0]){
                System.out.println(quasiIdentifyer);
            }
            System.out.println("\n - Risk analysis:");
            RiskAnalysis.analyzeData2(data.getHandle());

            // Restore standard output
            System.setOut(originalOut);

            // Write captured output to the file
            try (FileWriter writer = new FileWriter(Constants.ANALYSIS_FOLDER + outputFile)) {
                // Write output
                writer.write(outputStream.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    private static void csvCreator2(String dataPrefix, String dataSize, String outputStream, int[] quasiIdentifiers,
                                      Double[] risks) {
        String outputFile = Constants.ANALYSIS_FOLDER + "analysis3.csv"; // Modify this to the actual output file path

        try (FileWriter writer = new FileWriter(outputFile, true)) { // Append mode
            StringBuilder line = new StringBuilder();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            line.append(timestamp).append(", ")
                .append(dataPrefix).append(", ")
                .append(dataSize).append(", ")
                .append(outputStream).append(", ");
            
            for (int i = 0; i < 6; i++) {
                if (i < quasiIdentifiers.length) {
                    line.append(quasiIdentifiers[i]);
                }
                line.append(", ");
            }
            
            for (Double risk : risks) {
                line.append(risk).append(", ");
            }
            if (line.length() > 0) {
                line.delete(line.length() - 2, line.length());
            }

            writer.write(line.toString() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  


    private static void date(Data data, String variableNameString) {
        
    	String stringDateFormat = "yyyy-MM";
    	
    	DataType<Date> dateType = DataType.createDate(stringDateFormat);
    	
        // Create the builder
        HierarchyBuilderDate builder = HierarchyBuilderDate.create(dateType);
        
        // Define grouping
        builder.setGranularities(new Granularity[] {Granularity.QUARTER_YEAR, 
                                                    Granularity.YEAR, 
                                                    Granularity.DECADE});
        

        Integer columnIndexBirthDate = data.getHandle().getColumnIndexOf("Geburtsdatum");
        String[] columnBirthDateStrings = data.getHandle().getDistinctValues(columnIndexBirthDate);

        // Print info about resulting groups
        builder.prepare(columnBirthDateStrings);
        
        // Print resulting hierarchy
        builder.build().getHierarchy();
    }
    private static String[] getStringListFromData(Data inputData, String variableName) {
        // Load the CSV file using ARX's Data class
        Data data = inputData;
        
        // Get the index of the "Diagnose_ICD10_Code" column
        int icd10CodeColumnIndex = data.getHandle().getColumnIndexOf(variableName);

        // Get the number of rows in the column
        int numRows = data.getHandle().getNumRows();

        // Extract the values from the "Diagnose_ICD10_Code" column into a String[]
        String[] icd10Codes = new String[numRows - 1]; // Exclude the header row
        for (int i = 1; i < numRows; i++) { // Start from index 1 to skip the header
            icd10Codes[i - 1] = data.getHandle().getValue(i, icd10CodeColumnIndex);
        }
        return icd10Codes;
        // Now you have the "Diagnose_ICD10_Code" values in the icd10Codes array
        // You can pass this array to your redactionBasedHierarchy method
    }  
    private static void analyzeAttributes(DataHandle handle) {
        ARXPopulationModel populationmodel = ARXPopulationModel.create(Region.USA);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelAttributes riskmodel = builder.getAttributeRisks();
        for (QuasiIdentifierRisk risk : riskmodel.getAttributeRisks()) {
            System.out.println("   * Distinction: " + risk.getDistinction() + ", Separation: " + risk.getSeparation() + ", Identifier: " + risk.getIdentifier());
        }
    }

    /**
     * Perform risk analysis
     * @param handle
     */
    private static void analyzeData(DataHandle handle) {
        
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

}
