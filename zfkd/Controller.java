package zfkd;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Controller {
    /* Main Contolling class! Everything goes from here.
     * Always start the processing from here by running the main funtion.
     * Beforehand, go to Constants.java and check that the FILE and FOLDER and HIERARCHY paths are correct.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
     * Also check if your choice of QUASI_IDENTIFIER_CHOICE is as desired. 
     * Then run main.
     */

    public static void main(String[] args) {
        System.out.println("\nProgram Start...");
        File directory = new File(Constants.FOLDER_PATH);
        File[] existingFiles = directory.listFiles();
        Constants.setNumOfFiles(existingFiles.length);
        // UNBLOCK CODE BELOW TO RUN OVER ALL AVAILABLE DATASETS IN DATA-FOLDER
        // ---------------------------------------------------------
        // if (existingFiles != null && existingFiles.length > 0) {
        //     // Sort the files by size using the custom comparator
        //     Arrays.sort(existingFiles, new FileSizeComparator());
        //     int i = 0;
        //     while (i < 2) {
        //         for (File file : existingFiles) {
        //             String fileName = file.getName();
        //             System.out.println("\nNow running file: " + fileName);
        //             try {
        //                 fileRunner(fileName);
        //                 i ++;
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         }
        //     }

        // } else {
        //     System.out.println("No files found in the directory.");
        // }
        // -------------------------------------------------------

        // UNBLOCK CODE BELOW TO RUN ONLY OVER DATASET AT INDEX
        // -------------------------------------------------------
        Arrays.sort(existingFiles, new FileSizeComparator());
        String fileName = existingFiles[Constants.getIndexOfFile()].getName();

        System.out.println("\nNow running file: " + fileName);
        try {
            fileRunner(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // -------------------------------------------------------
    }

    public static void fileRunner(String filePath) throws IOException {
        long startTime = System.currentTimeMillis();
        //---------------------------------
        Constants.setData(filePath);
        System.out.println("\nAnalyzing Input Data:");
        RiskEstimator.analyzeData(Constants.getData().getHandle());
        RiskEstimator.defineAttributes(Constants.getData());
        RiskEstimator.setHierarchy(Constants.getData());
        // TesterMethods.printHierarchyToCSV("Age");
        // TesterMethods.printHierarchyToCSV("Geschlecht");

        int totalIterations = calculateTotalIterations() * Constants.getNumOfFiles();
        System.out.println("\n------------------\nAnticipated iterations: " + totalIterations);
        resolutionRectifier();
        iterateQIResolution(0); // this is where the true work is done! create Iteration protocoll and calling the RiskEstimation each time.
        System.out.println("Anticipated iterations: " + totalIterations + "\nFinal total Iterations: " + (Constants.ITERATION_COUNT - 1));
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds.\n");
    }


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
            int maxIterations = Constants.getData().getDefinition().getMaximumGeneralization(currentQuasiIdentifier); // column count -1 since indices start at 0
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
            System.out.println("\nMax Generalisation for: " + quasiIdentifier + " = " + Constants.getData().getDefinition().getMaximumGeneralization(quasiIdentifier));
            totalIterations *= (Constants.getData().getDefinition().getMaximumGeneralization(quasiIdentifier) + 1);
        }
        return totalIterations;
    }
}
