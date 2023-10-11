import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.deidentifier.arx.Data;

public class Constants {
    //-----------------------zfkd-----------------------------
    // public static final String[] QUASI_IDENTIFIER_CHOICE = {"Age", "Geschlecht", "Inzidenzort"}; // Change according to needed choice of Quasi-Identifiers. Pick from QUASI_IDENTIFIER_FULL_SET. Mind spelling!
    
    // public static final String[] SENSITIVES_CHOICE = {"Diagnose_ICD10_Code"};
    // public static final String FOLDER_PATH = "test-data/zfkd/"; // Foldername or path, where the input test-dataset is stored
    // public static final String FILE_PATH = "10000_rows.csv"; // Filename of the starting dataset
    // public static final String HIERARCHY_PATH = "hierarchies/"; // Foldername or path, where the hierarchies are stored

    // public static final String[] QUASI_IDENTIFIER_FULL_SET = {"Age", "Geschlecht", "Inzidenzort", "Diagnose_ICD10_Code", "Geburtsdatum", "Diagnosedatum"}; // Will remain constant
    // public static final String FILE_NAME_PREFIX = "zfkd_"; // extracts the first word of the input Filepath as an indicator for the type of data to be used further on.
    // public static final String DATA_SIZE = extractNumberWithUnderscore(FILE_PATH); // number of rows of the initial dataset, which will be displayed in the analysis
    // public static final String OUTPUT_DIRECTORY = FILE_NAME_PREFIX + "testproducts/"; // Output-directory for precise analysis of each iteration
    // public static final String ANALYSIS_FOLDER = "risk-analysis/";
    // public static final String ANALYSIS_PATH = "analysis.csv";
    // public static final String ANALYSIS_PATH_2 = "analysis4.csv";
    // public static int[] QI_RESOLUTION = new int[QUASI_IDENTIFIER_FULL_SET.length];
    // public static Data DATA = Data.create();
    //---------------------kiggs----------------------
    public static final String[] QUASI_IDENTIFIER_CHOICE = {"age2", "sex", "ppoint"}; // Change according to needed choice of Quasi-Identifiers. Pick from QUASI_IDENTIFIER_FULL_SET. Mind spelling!
    public static final String[] QUASI_IDENTIFIER_FULL_SET = {"age2", "agegrpK", "sex", "sexa", "ppoint"}; // Will remain constant
    public static final String[] SENSITIVES_CHOICE = {"SDEses"};
    public static final String FOLDER_PATH = "test-data/"; // Foldername or path, where the input test-dataset is stored
    public static final String FILE_PATH = "kiggs_GDC_rowcount_3500.csv"; // Filename of the starting dataset
    public static final String HIERARCHY_PATH = "hierarchies/hierarchies_kiggs/"; // Foldername or path, where the hierarchies are stored
    public static final String FILE_NAME_PREFIX = "kiggs_"; // extracts the first word of the input Filepath as an indicator for the type of data to be used further on.
    public static final String DATA_SIZE = extractNumberWithUnderscore(FILE_PATH); // number of rows of the initial dataset, which will be displayed in the analysis
    public static final String OUTPUT_DIRECTORY = FILE_NAME_PREFIX + "testproducts/"; // Output-directory for precise analysis of each iteration
    public static final String ANALYSIS_FOLDER = "risk-analysis/";
    public static final String ANALYSIS_PATH = "analysis.csv";
    public static final String ANALYSIS_PATH_2 = "kiggs-analysis.csv";
    private static int[] QI_RESOLUTION = new int[QUASI_IDENTIFIER_FULL_SET.length];
    public static Data DATA = Data.create();
    //-------------------------------------------
    // public static final int NUM_ROWS = DATA.getHandle().getNumRows();

    public static int ITERATION_COUNT = 1; 

    public static void incrementIterationCount() {
        ITERATION_COUNT += 1;
    }

    public static void setData() {
        try {
            DATA = Data.create(FOLDER_PATH + FILE_PATH, StandardCharsets.UTF_8, ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
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
