import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
// import java.util.Arrays;

public class Controller {
    // private static final String FILE_PATH = "test-data/zfkd_QI_adapted_50000.csv";
    private static final String HIERARCHY_PATH = "hierarchies2/";

    private static final String[] QUASI_IDENTIFIER_FULL_SET = {"Age", "Geschlecht", "Inzidenzort", "Geburtsdatum", "Diagnose_ICD10_Code", "Diagnosedatum"};
    private static final String[] QUASI_IDENTIFIER_STRINGS = {"Age", "Geschlecht"};

    // private static final String[] QI_RESOLUTION = {"1","1","1","1","0","0"};
    public static void main(String[] args) throws IOException {
        Map<String, Integer> result = CSVFileScanner(HIERARCHY_PATH); // count the number of columns in each Hierarchy-csv-file. CAVE: only works if hierarchies have the same name as QI's.
        Map<String, Integer> reorderedResult = reorderHashMap(result); // reorder the the result and adapt the keys to fit QUASI_IDENTIFIER_STRINGS

        int[] QI_Resolution = new int[QUASI_IDENTIFIER_FULL_SET.length]; // creates an integer array with as many empty values as the full set of QI's has attributes.

        // Arrays.fill(QI_Resolution, -1); // Replaces 0 with -1 for omitted quasi-identifiers


        int totalIterations = calculateTotalIterations(reorderedResult);
        System.out.println("Total iterations: " + totalIterations);
        iterateQIResolution(reorderedResult, QI_Resolution, 0); // create Iteration protocoll and calling the RiskEstimation each time.
        System.out.println("Total iterations: " + totalIterations);
        
        // RiskEstimator.RiskEstimation(QI_Resolution);
    }


    public static void printArrays(int[] arrayToBePrinted){
        for (int i = 0; i < arrayToBePrinted.length; i++) {
            // Print each element followed by a space (or any separator you prefer)
            System.out.print(arrayToBePrinted[i] + " ");
        }
        System.out.println("\n");
    }

    public static Map<String, Integer> CSVFileScanner(String filePath) {
        /* takes in a path to a folder, where all complementary hierarchies should be stored
        takes all the csv files from that folder and puts them in a File-Array csvFiles
        counts the number of columns in each file
        creates a hashmap with filename and number of columns as key-value-pairs
        returns the hashmap
        */
        File folder = new File(filePath);
        Map<String, Integer> results = new HashMap<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

            if (csvFiles != null) {
                for (File csvFile : csvFiles) {
                    try {
                        int numColumns = countColumnsInCSV(csvFile);
                        String fileName = csvFile.getName().replace(".csv", "");
                        results.put(fileName, numColumns);
                        System.out.println("File: " + fileName + ".csv has " + numColumns + " columns.");
                    } catch (IOException e) {
                        System.err.println("Error processing file: " + csvFile.getName());
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("The specified folder: " + folder + " does not exist or is not a directory.");
        }

        return results;
    }



    public static int countColumnsInCSV(File csvFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] columns = headerLine.split(";");
                return columns.length;
            } else {
                // Handle the case where the file is empty or has no header
                return 0;
            }
        }
    }

    public static Map<String, Integer> reorderHashMap(Map<String, Integer> originalMap){
        Map<String, Integer> reorderedMap = new HashMap<>();

        // Reorder the originalMap based on QUASI_IDENTIFIERS
        for (String identifier : QUASI_IDENTIFIER_STRINGS) {
            String lowercaseIdentifier = identifier.toLowerCase();
            for (Map.Entry<String, Integer> entry : originalMap.entrySet()) {
                if (entry.getKey().toLowerCase().equals(lowercaseIdentifier)) {
                    reorderedMap.put(identifier, entry.getValue());
                }
            }
        }

        // Print the contents of the reorderedMap
        System.out.println("Reordered HashMap based on QUASI_IDENTIFIERS:");
        for (Map.Entry<String, Integer> entry : reorderedMap.entrySet()) {
            System.out.println(entry.getKey() + " has " + entry.getValue() + " columns.");
        }
        return reorderedMap;
    }

    private static void iterateQIResolution(Map<String, Integer> reorderedResult, int[] QI_Resolution, int index) {
        /*
         * This Method will iterate over all possible QI's by index
         * It will then determine the number of max Iterations for each QI
         * and set that value at that index in QI_Resolution[index].
         * When all QI_Resolutions have been set, it will call the RiskEstimator with the result.
         */
        int iterationCount = 1;
        if (index == QUASI_IDENTIFIER_FULL_SET.length) {
            // Base case: All QI_Resolutions have been set, call RiskEstimator
            System.out.println("IterationCount = " + iterationCount + "\nnow calling callRiskEstimator");
            iterationCount += 1;
            QI_Resolution[1] = 1; // Hard coding to avoid the error in Geschlecht
            callRiskEstimator(QI_Resolution);
            return;
        }

        String currentQuasiIdentifier = QUASI_IDENTIFIER_FULL_SET[index];
        // Determine the maximum number of iterations for the current quasi-identifier
        int maxIterations = reorderedResult.getOrDefault(currentQuasiIdentifier, 0);
        System.out.println("maxIterations for " + currentQuasiIdentifier + " = " + maxIterations);

        // Try different values for QI_Resolution at the current index
        for (int i = 0; i <= maxIterations; i++) {
            QI_Resolution[index] = i;
            System.out.println("QI_Resolution[" + index +"] / " + currentQuasiIdentifier +" = " + i);
            iterateQIResolution(reorderedResult, QI_Resolution, index + 1);
        }
        System.out.println("Now through with Iterations.\nReturning to main.");
        return;
    }

    private static void callRiskEstimator(int[] QI_Resolution) {
        // Call RiskEstimator with the current QI_Resolution
        System.out.print("QI_Resolution for this Iteration = {");
        for (int i = 0; i < QI_Resolution.length; i++) {
            System.out.print(QI_Resolution[i]);
            if (i < QI_Resolution.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
        try {
        // Uncomment the following line to actually call RiskEstimator with QI_Resolution
            RiskEstimator.RiskEstimation(QI_Resolution, QUASI_IDENTIFIER_STRINGS);
        } catch (IOException e) {
            // Wrap the checked exception in a runtime exception
            throw new RuntimeException("Error in RiskEstimation", e);
        }
    }

    private static int calculateTotalIterations(Map<String, Integer> reorderedResult) {
        int totalIterations = 1;
        for (String quasiIdentifier : QUASI_IDENTIFIER_FULL_SET) {
            if (reorderedResult.containsKey(quasiIdentifier)) {
                totalIterations *= (reorderedResult.get(quasiIdentifier) + 1);
            }
        }
        return totalIterations;
    }
}
