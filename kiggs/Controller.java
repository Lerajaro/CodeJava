package kiggs;
import java.io.IOException;

// import java.util.Arrays;

public class Controller {
    /* Controller for Kiggs Data
     * Trying to solve the hierarchy problem: no more csv hierarchies, but automated hierarchy creation from the Controller
     */
    /* Main Contolling class! Everything goes from here.
     * Always start the processing from here by running the main funtion.
     * Beforehand, go to Constants.java and check that the FILE and FOLDER and HIERARCHY paths are correct.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
     * Also check if your choice of QUASI_IDENTIFIER_CHOICE is as desired. 
     * Then run main.
     */
    // private static int ITERATION_COUNT = 1;

    public static void main(String[] args) throws IOException {
        System.out.println("\nProgram Start...");
        long startTime = System.currentTimeMillis();
        /* TODO: 
         * First initialize the data
         * set attributes according to QI-Choices
         * define hierarchies
         * count generalization levels
         * iterate over generalization levels and run the risk estimator
         */
        Constants.setData();
        System.out.println("\nAnalyzing Input Data:");
        RiskEstimator.analyzeData(Constants.DATA.getHandle());
        RiskEstimator.defineAttributes(Constants.DATA);
        RiskEstimator.setHierarchy(Constants.DATA);
        TesterMethods.testHierarchyBuildingSuccess("sex");
        TesterMethods.testDefineAttributes(Constants.DATA.getDefinition());

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

    private static int[] resolutionChecker(int[] QI_Resolution) {
        /* Sets all values of QI_Resolution to -1 for attributes that are not chosen in QI_CHOICE*/
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
            // RiskEstimator.RiskEstimation();
            RiskEstimator.RiskEstimation();
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
}

