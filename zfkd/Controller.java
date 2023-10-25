package zfkd;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


// import java.util.Arrays;

public class Controller {
    /* Main Contolling class! Everything goes from here.
     * Always start the processing from here by running the main funtion.
     * Beforehand, go to Constants.java and check that the FILE and FOLDER and HIERARCHY paths are correct.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
     * Also check if your choice of QUASI_IDENTIFIER_CHOICE is as desired. 
     * Then run main.
     */
    // private static int ITERATION_COUNT = 1;
    // public static void main(String[] args) throws IOException {
    //     System.out.println("\nProgram Start...");
    //     File directory = new File(Constants.FOLDER_PATH);
    //     System.out.println("\nFile directory: ");
    //     String[] existingFiles = directory.list();
    //     TesterMethods.TestStringArrays(existingFiles, null);

    //     for (String filePath : existingFiles) {
    //         System.out.println("\nNow running file: " + filePath);
    //         fileRunner(filePath);
    //     }
    // }

    public static void main(String[] args) {
        System.out.println("\nProgram Start...");
        File directory = new File(Constants.FOLDER_PATH);
        System.out.println("\nFile directory: ");
        File[] existingFiles = directory.listFiles();

        if (existingFiles != null && existingFiles.length > 0) {
            // Sort the files by size using the custom comparator
            Arrays.sort(existingFiles, new FileSizeComparator());
            
            for (File file : existingFiles) {
                String fileName = file.getName();
                System.out.println("\nNow running file: " + fileName);
                try {
                    fileRunner(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No files found in the directory.");
        }
    }

    public static void fileRunner(String filePath) throws IOException {
        long startTime = System.currentTimeMillis();
        //---------------------------------
        Constants.setData(filePath);
        System.out.println("\nAnalyzing Input Data:");
        RiskEstimator.analyzeData(Constants.DATA.getHandle());
        RiskEstimator.defineAttributes(Constants.DATA);
        RiskEstimator.setHierarchy(Constants.DATA);

        int[] QI_Resolution = new int[Constants.QUASI_IDENTIFIER_FULL_SET.length]; // creates an integer array with as many empty values as the full set of QI's has attributes.
        QI_Resolution = resolutionChecker(QI_Resolution);

        int totalIterations = calculateTotalIterations();
        System.out.println("\n------------------\nAnticipated iterations: " + totalIterations);
        resolutionRectifier();
        iterateQIResolution(0); // this is where the true work is done! create Iteration protocoll and calling the RiskEstimation each time.
        System.out.println("Anticipated iterations: " + totalIterations + "\nFinal total Iterations: " + (Constants.ITERATION_COUNT - 1));
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds.\n");
    }

    // public static Map<String, Integer> CSVFileScanner(String filePath) {
    //     /* takes in a path to a folder, where all complementary hierarchies should be stored,
    //     takes all the csv files from that folder and puts them in a File-Array csvFiles,
    //     then counts the number of columns in each file,
    //     creates a hashmap with filename and number of columns as key-value-pairs,
    //     returns the hashmap
    //     */
    //     File folder = new File(filePath);
    //     Map<String, Integer> results = new HashMap<>();

    //     if (folder.exists() && folder.isDirectory()) {
    //         File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
    //         System.out.println("\nHIERARCHY_PATH: " + filePath + "\nChecking folder for .csv's and counting the columns...");
    //         if (csvFiles != null) {
    //             for (File csvFile : csvFiles) {
    //                 try {
    //                     int numColumns = countColumnsInCSV(csvFile);
    //                     String fileName = csvFile.getName().replace(".csv", "");
    //                     results.put(fileName, numColumns);
    //                     System.out.println(fileName + ".csv has " + numColumns + " columns.");
    //                 } catch (IOException e) {
    //                     System.err.println("Error processing file: " + csvFile.getName());
    //                     e.printStackTrace();
    //                 }
    //             }
    //         }
    //     } else {
    //         System.err.println("The specified folder: " + folder + " does not exist or is not a directory.");
    //     }

    //     return results;
    // }

    // public static int countColumnsInCSV(File csvFile) throws IOException {
    //     try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
    //         String headerLine = reader.readLine();
    //         if (headerLine != null) {
    //             String[] columns = headerLine.split(";");
    //             return columns.length;
    //         } else {
    //             // Handle the case where the file is empty or has no header
    //             return 0;
    //         }
    //     }
    // }

    // public static Map<String, Integer> reorderHashMap(Map<String, Integer> originalMap){
    //     Map<String, Integer> reorderedMap = new HashMap<>();

    //     // Reorder the originalMap based on QUASI_IDENTIFIERS 
    //     for (String identifier : Constants.QUASI_IDENTIFIER_CHOICE) {
    //         String lowercaseIdentifier = identifier.toLowerCase();
    //         for (Map.Entry<String, Integer> entry : originalMap.entrySet()) {
    //             if (entry.getKey().toLowerCase().equals(lowercaseIdentifier)) {
    //                 reorderedMap.put(identifier, entry.getValue());
    //             }
    //         }
    //     }
    //     return reorderedMap;
    // }

    private static void resolutionRectifier() {
        for (int i = 0; i < Constants.QUASI_IDENTIFIER_FULL_SET.length; i++) {
            String attribute = Constants.QUASI_IDENTIFIER_FULL_SET[i];
            boolean isInQIChoice = false;

            for (String choice : Constants.QUASI_IDENTIFIER_CHOICE) {
                if (choice.equals(attribute)) {
                    isInQIChoice = true;
                    break;
                }
            }

            if (!isInQIChoice) {
                Constants.setQIResolutionAttribute(i, -1);
            }
        }
    }

    private static void iterateQIResolution(int index) {
        /*
         * This Method will iterate over all possible QI's by index
         * For each QI it will go through all possibilities from 0 to maxIterations 
         * (which is equal to the number of columns in the equivalent hierarchy - 1).
         * When all QI_Resolutions have been set, it will call the RiskEstimator with the result.
         */
        
        if (index == Constants.QUASI_IDENTIFIER_FULL_SET.length) {
            // Base case: All QI_Resolutions have been set, call RiskEstimator
            System.out.println("Iteration Count: " + Constants.ITERATION_COUNT);
            callRiskEstimator();
            Constants.incrementIterationCount();
            return;
        }
        
        if (Constants.getQIResolution()[index] != -1) {
            String currentQuasiIdentifier = Constants.QUASI_IDENTIFIER_FULL_SET[index];
            int maxIterations = Constants.DATA.getDefinition().getMaximumGeneralization(currentQuasiIdentifier); // column count -1 since indices start at 0
            for (int i = 0; i <= maxIterations; i++) {
                Constants.setQIResolutionAttribute(index, i);
                iterateQIResolution(index + 1);
            }
        }
        else {
            iterateQIResolution(index + 1);
        }
        return;
    }

    // private static void iterateQIResolution(Map<String, Integer> reorderedResult, int[] QI_Resolution, int index) {
    //     /*
    //      * This Method will iterate over all possible QI's by index
    //      * For each QI it will go through all possibilities from 0 to maxIterations 
    //      * (which is equal to the number of columns in the equivalent hierarchy - 1).
    //      * When all QI_Resolutions have been set, it will call the RiskEstimator with the result.
    //      */
        
    //     if (index == Constants.QUASI_IDENTIFIER_FULL_SET.length) {
    //         // Base case: All QI_Resolutions have been set, call RiskEstimator
    //         System.out.println("Iteration Count: " + Constants.ITERATION_COUNT);
    //         Constants.setQIResolution(QI_Resolution);
    //         callRiskEstimator();
    //         Constants.incrementIterationCount();
    //         return;
    //     }
        
    //     if (QI_Resolution[index] != -1) {
    //         String currentQuasiIdentifier = Constants.QUASI_IDENTIFIER_FULL_SET[index];
    //         int maxIterations = reorderedResult.getOrDefault(currentQuasiIdentifier, 0) -1; // column count -1 since indices start at 0
    //         for (int i = 0; i <= maxIterations; i++) {
    //             QI_Resolution[index] = i;
    //             iterateQIResolution(reorderedResult, QI_Resolution, index + 1);
    //         }
    //     }
    //     else {
    //         iterateQIResolution(reorderedResult, QI_Resolution, index + 1);
    //     }
    //     return;
    // }

    private static int[] resolutionChecker(int[] QI_Resolution) {
        for (int i = 0; i < Constants.QUASI_IDENTIFIER_FULL_SET.length; i++) {
            String attribute = Constants.QUASI_IDENTIFIER_FULL_SET[i];
            boolean isInQIChoice = false;

            for (String choice : Constants.QUASI_IDENTIFIER_CHOICE) {
                if (choice.equals(attribute)) {
                    isInQIChoice = true;
                    break;
                }
            }

            if (!isInQIChoice) {
                QI_Resolution[i] = -1;
            }
        }
        return QI_Resolution;
    }

    private static void callRiskEstimator() {
        // Call RiskEstimator with the current QI_Resolution
        TesterMethods.printQIResolution();
        System.out.println("------------------\n");
        try {
            RiskEstimator.RiskEstimation();
            //RiskEstimator.RiskEstimation();
        } catch (IOException e) {
            // Wrap the checked exception in a runtime exception
            throw new RuntimeException("Error in RiskEstimation", e);
        }
    }

    private static int calculateTotalIterations() {
        int totalIterations = 1;
        System.out.println("\nQI-Choice = ");
        TesterMethods.TestStringArrays(Constants.QUASI_IDENTIFIER_CHOICE, "QI-Choice");
        for (String quasiIdentifier : Constants.QUASI_IDENTIFIER_CHOICE) {
            System.out.println("\nMax Generalisation for: " + quasiIdentifier + " = " + Constants.DATA.getDefinition().getMaximumGeneralization(quasiIdentifier));
            totalIterations *= (Constants.DATA.getDefinition().getMaximumGeneralization(quasiIdentifier) + 1);
        }
        return totalIterations;
    }

    // private static int calculateTotalIterations(Map<String, Integer> reorderedResult) {
    //     int totalIterations = 1;
    //     for (String quasiIdentifier : Constants.QUASI_IDENTIFIER_FULL_SET) {
    //         if (reorderedResult.containsKey(quasiIdentifier)) {
    //             totalIterations *= (reorderedResult.get(quasiIdentifier));
    //         }
    //     }
    //     return totalIterations;
    // }
}
