/* This class is supposed to have all the funcitonalities to take a dataset of zfkd data
and perform risk analysis to it
and save the results in the analysis.csv (later maybe the analysis2.csv)
plus store the detailed results in a single file, of which the name is saved in analysis.csv
*/

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;

import org.deidentifier.arx.risk.RiskModelSampleRisks;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.ARXPopulationModel.Region;

public class ZfKDRiskEstimate extends Example {
    private static final String FILE_PATH = "test-data/zfkd_QI_adapted_60000.csv";
    private static final String FILE_NAME_PREFIX = "zfkd_";
    private static final String ANALYSIS_FOLDER = "risk-analysis/";
    private static final String DATA_SIZE = "60000_";
    private static final String[] QUASI_IDENTIFYERS = {"Age", "Geschlecht", "Inzidenzort", "Geburtsdatum", "Diagnose_ICD10_Code", "Diagnosedatum"};
    private static final String[] QI_RESOLUTION = {"1","1","1","1","0","0"};

    private enum AttributeCategory {
        QUASI_IDENTIFYING, IDENTIFYING, SENSITIVE, INSENSITIVE
    }

    public static void main(String[] args) throws IOException {
        Data data = Data.create(FILE_PATH, StandardCharsets.UTF_8, ',');
        defineAttributes(data);
        DataHandle handle = data.getHandle();
        // Perform risk analysis and other operations
        ARXPopulationModel populationmodel = ARXPopulationModel.create(Region.GERMANY);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelSampleRisks sampleReidentifiationRisk = builder.getSampleBasedReidentificationRisk();
        Double averageRisk = sampleReidentifiationRisk.getAverageRisk();
        Double lowestRisk = sampleReidentifiationRisk.getLowestRisk();
        Double highestRisk = sampleReidentifiationRisk.getHighestRisk();

        // Write results
        String outputFile = riskAnalysisManager.getNewFileName(FILE_NAME_PREFIX, DATA_SIZE);
        // Creating a new file with detailed risk analysis
        outputStream(data, outputFile);
        // Writing a new line to the analysis.csv
        csvCreator(FILE_NAME_PREFIX, DATA_SIZE, outputFile, QUASI_IDENTIFYERS, averageRisk, lowestRisk, highestRisk);
        csvCreator2(FILE_NAME_PREFIX, DATA_SIZE, outputFile, QI_RESOLUTION, averageRisk, lowestRisk, highestRisk);
        System.out.println("Done!");
    }

    private static String[][] defineAttributes(Data data) {
        String[] identifyers = {};
        String[] sensitives = {};
        String[] insensitives = {};
        String[][] variableTypes = {QUASI_IDENTIFYERS, identifyers, sensitives, insensitives};

        for (AttributeCategory category : AttributeCategory.values()) {
            String[] variables = variableTypes[category.ordinal()];
            for (String variableName : variables) {
                setAttributeType(data, variableName, category);
            }
        }
        // Define hierarchies


        return variableTypes;
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
            try (FileWriter writer = new FileWriter(ANALYSIS_FOLDER + outputFile)) {
                // Write output
                writer.write(outputStream.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other methods and constants...
    private static void csvCreator(String dataPrefix, String dataSize, String outputStream, String[] quasiIdentifiers,
                                      double averageRisk, double lowestRisk, double highestRisk) {
        String outputFile = ANALYSIS_FOLDER + "analysis.csv"; // Modify this to the actual output file path

        try (FileWriter writer = new FileWriter(outputFile, true)) { // Append mode
            StringBuilder line = new StringBuilder();
            line.append(dataPrefix).append(", ")
                .append(dataSize).append(", ")
                .append(outputStream).append(", ");
            
            for (int i = 0; i < 6; i++) {
                if (i < quasiIdentifiers.length) {
                    line.append(quasiIdentifiers[i]);
                }
                line.append(", ");
            }
            
            line.append(averageRisk).append(", ")
                .append(lowestRisk).append(", ")
                .append(highestRisk);

            writer.write(line.toString() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
        
    private static void csvCreator2(String dataPrefix, String dataSize, String outputStream, String[] quasiIdentifiers,
                                      double averageRisk, double lowestRisk, double highestRisk) {
        String outputFile = ANALYSIS_FOLDER + "analysis2.csv"; // Modify this to the actual output file path

        try (FileWriter writer = new FileWriter(outputFile, true)) { // Append mode
            StringBuilder line = new StringBuilder();
            line.append(dataPrefix).append(", ")
                .append(dataSize).append(", ")
                .append(outputStream).append(", ");
            
            for (int i = 0; i < 6; i++) {
                if (i < quasiIdentifiers.length) {
                    line.append(quasiIdentifiers[i]);
                }
                line.append(", ");
            }
            
            line.append(averageRisk).append(", ")
                .append(lowestRisk).append(", ")
                .append(highestRisk);

            writer.write(line.toString() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }     
}
