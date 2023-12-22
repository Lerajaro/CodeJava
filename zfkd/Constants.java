
package zfkd;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.criteria.AverageReidentificationRisk;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.metric.Metric;

public class Constants {
    //-----------------------zfkd-----------------------------
    public static final String[] QUASI_IDENTIFIER_CHOICE = {"Age", "Geschlecht", "Inzidenzort"}; // Change according to needed choice of Quasi-Identifiers. Pick from QUASI_IDENTIFIER_FULL_SET. Mind spelling!
    public static final String[] SENSITIVES_CHOICE = {};
    public static final String FOLDER_PATH = "zfkd/test-data/"; // Foldername or path, where the input test-dataset is stored
    private static int NUM_OF_FILES;
    // public static final String FILE_PATH = "25000_rows.csv"; // Filename of the starting dataset
    public static final String HIERARCHY_PATH = "zfkd/hierarchies/"; // Foldername or path, where the hierarchies are stored
    public static final String[] QUASI_IDENTIFIER_FULL_SET = {"Age", "Geschlecht", "Inzidenzort", "Diagnose_ICD10_Code", "Geburtsdatum", "Diagnosedatum"}; // Will remain constant
    public static final String FILE_NAME_PREFIX = "zfkd_"; // extracts the first word of the input Filepath as an indicator for the type of data to be used further on.
    // public static final String DATA_SIZE = extractNumberWithUnderscore(FILE_PATH); // number of rows of the initial dataset, which will be displayed in the analysis
    public static final String OUTPUT_DIRECTORY = FILE_NAME_PREFIX + "zfkd/testproducts/"; // Output-directory for precise analysis of each iteration
    public static final String ANALYSIS_FOLDER = "zfkd/risk-analysis/";
    public static final String ANALYSIS_PATH = "analysis2.csv";
    private static final String CHANGE_STRING = "Search for Inzidenzort-Error"; // you can insert the change you did between two analysis, to make it visible afterwards
    private static final Integer indexOfFile = 2;

    public static int[] QI_RESOLUTION = new int[QUASI_IDENTIFIER_FULL_SET.length];
    private static Data DATA;
    private static ARXConfiguration CONFIG;
    //-------------------------------------------

    public static int ITERATION_COUNT = 1; 

    public static String getChangesString() {
        return CHANGE_STRING;
    }

    public static void incrementIterationCount() {
        ITERATION_COUNT += 1;
    }

    public static void setNumOfFiles(int numOfFiles) {
        NUM_OF_FILES = numOfFiles;
    }

    public static int getNumOfFiles() {
        return NUM_OF_FILES;
    }

    public static void setData(String filePath) {
        try {
            DATA = Data.create(FOLDER_PATH + filePath, StandardCharsets.UTF_8, ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Data getData() {
        return DATA;
    }

    public static Integer getIndexOfFile() {
        return indexOfFile;
    }

    public static void setARXConfiguration() {
            CONFIG = ARXConfiguration.create();
            // CONFIG.addPrivacyModel(new AverageReidentificationRisk(0.5d));
            CONFIG.addPrivacyModel(new KAnonymity(3));
            CONFIG.setSuppressionLimit(1d); // Recommended default: 1d
            CONFIG.setQualityModel(Metric.createLossMetric(0.5d)); // suppression/generalization-factor
    }

    public static ARXConfiguration getConfig() {
        return CONFIG;
    }

    public static void setQIResolution(int[] QI_Resolution) {
        QI_RESOLUTION = QI_Resolution;
    }

    public static void setQIResolutionAttribute(int indexOfAttribute, int value) {
        QI_RESOLUTION[indexOfAttribute] = value;
    }

    public static int [] getQIResolution() {
        return QI_RESOLUTION;
    }

    public static String extractNumberWithUnderscore(String input) {
        // Define a regular expression pattern to match a number
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        // Find the first occurrence of a number in the input string
        if (matcher.find()) {
            // Extract the matched number and add "_" to the end
            String numberStr = matcher.group() + "_";
            return numberStr;
        } else {
            // Handle the case where no number is found in the input string
            throw new IllegalArgumentException("No number found in the input string");
        }
    }
    public static String extractFirstPart(String input) {
        int indexOfUnderscore = input.indexOf('_');
        if (indexOfUnderscore != -1) {
            // Extract the substring from the beginning to the first underscore (inclusive)
            return input.substring(0, indexOfUnderscore);
        } else {
            // Handle the case where no underscore is found in the input string
            throw new IllegalArgumentException("No underscore found in the input string");
        }
    }
}
