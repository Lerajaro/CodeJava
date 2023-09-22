import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
// import java.util.Arrays;

public class Controller {
    /* Main Contolling class! Everything goes from here.
     * Always start the processing from here by running the main funtion.
     * Beforehand, go to Constants.java and check that the FILE and FOLDER and HIERARCHY paths are correct.
     * Also check if your choice of QUASI_IDENTIFIER_CHOICE is as desired. 
     * Then run main.
     */
    private static int ITERATION_COUNT = 1;

    public static void main(String[] args) throws IOException {
        Map<String, Integer> result = CSVFileScanner(Constants.HIERARCHY_PATH); // count the number of columns in each Hierarchy-csv-file inside the given Hierarchypath-folder. CAVE: only works if hierarchies have the same name as QI's.
        TesterMethods.TestStringIntMaps(result);
        Map<String, Integer> reorderedResult = reorderHashMap(result); // reorder the the result and adapt the keys to fit QUASI_IDENTIFIER_STRINGS
        TesterMethods.TestStringIntMaps(reorderedResult);

        int[] QI_Resolution = new int[Constants.QUASI_IDENTIFIER_FULL_SET.length]; // creates an integer array with as many empty values as the full set of QI's has attributes.

        int totalIterations = calculateTotalIterations(reorderedResult);
        System.out.println("Anticipated iterations: " + totalIterations);
        
        iterateQIResolution(reorderedResult, QI_Resolution, 0); // create Iteration protocoll and calling the RiskEstimation each time.
        System.out.println("Anticipated iterations: " + totalIterations + "\nFinal total Iterations: " + (ITERATION_COUNT - 1) + "\n");
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
        for (String identifier : Constants.QUASI_IDENTIFIER_CHOICE) {
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
        
        if (index == Constants.QUASI_IDENTIFIER_FULL_SET.length) {
            // Base case: All QI_Resolutions have been set, call RiskEstimator
            System.out.println("------------------\nIterationCount = " + ITERATION_COUNT + "\nnow calling callRiskEstimator\n------------------");
            ITERATION_COUNT += 1;
            QI_Resolution[1] = 1; // Hard coding to avoid the error in Geschlecht
            callRiskEstimator(QI_Resolution);
            return;
        }

        String currentQuasiIdentifier = Constants.QUASI_IDENTIFIER_FULL_SET[index];
        // Determine the maximum number of iterations for the current quasi-identifier
        int maxIterations = reorderedResult.getOrDefault(currentQuasiIdentifier, 0);

        // Try different values for QI_Resolution at the current index
        for (int i = 0; i <= maxIterations; i++) {
            QI_Resolution[index] = i;
            iterateQIResolution(reorderedResult, QI_Resolution, index + 1);
        }
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
            RiskEstimator.RiskEstimation(QI_Resolution);
        } catch (IOException e) {
            // Wrap the checked exception in a runtime exception
            throw new RuntimeException("Error in RiskEstimation", e);
        }
    }

    private static int calculateTotalIterations(Map<String, Integer> reorderedResult) {
        int totalIterations = 1;
        for (String quasiIdentifier : Constants.QUASI_IDENTIFIER_FULL_SET) {
            if (reorderedResult.containsKey(quasiIdentifier)) {
                totalIterations *= (reorderedResult.get(quasiIdentifier) + 1);
            }
        }
        return totalIterations;
    }
}
