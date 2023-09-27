import java.io.File;
import java.util.Arrays;

public class RiskAnalysisManager {
    private static final String FILE_PREFIX = "test_";
    private static final String FILE_EXTENSION = ".txt";

    public static String getNewFileName() {
        System.out.println("\nGenerating new filename for detailed analysis output...");
        File directory = new File(Constants.ANALYSIS_FOLDER + Constants.OUTPUT_DIRECTORY);
        String[] existingFiles = directory.list();

        String totalPrefix = FILE_PREFIX + Constants.FILE_NAME_PREFIX;

        // TesterMethods.TestStringArrays(existingFiles, "getNewFileName");

        if (existingFiles == null) {
            // The directory does not exist or is not a directory
            return Constants.OUTPUT_DIRECTORY + totalPrefix + "1" + FILE_EXTENSION;
        }

        Arrays.sort(existingFiles); // Sort the existing files to get the last one

        // Filter the existing files with the correct prefix and extension
        existingFiles = Arrays.stream(existingFiles)
                .filter(name -> name.startsWith(FILE_PREFIX) && name.endsWith(FILE_EXTENSION))
                .toArray(String[]::new);

        if (existingFiles.length == 0) {
            // No files with the prefix and extension found
            return totalPrefix + "1" + FILE_EXTENSION;
        }

        // Get the last file name
        String lastFileName = existingFiles[existingFiles.length - 1];

        // Extract the number from the last file name
        int lastFileNumber = Integer.parseInt(lastFileName.substring(totalPrefix.length(), lastFileName.length() - FILE_EXTENSION.length()));

        // Create and return the new file name with the next consecutive number
        String newFileName = FILE_PREFIX + Constants.FILE_NAME_PREFIX + (lastFileNumber + 1) + FILE_EXTENSION;
        // printTester(lastFileName, totalPrefix, lastFileNumber, newFileName);
        return newFileName;
    }

    public static void printTester(String lastFileName, String totalPrefix, int lastFileNumber, String newFileName) {
        System.out.println("lastFileName = " + lastFileName);
        System.out.println("lastFileName.length() =  " + lastFileName.length());
        System.out.println("totalPrefix =  " + totalPrefix);
        System.out.println("totalPrefix.length() =  " + totalPrefix.length());
        System.out.println("FILE_EXTENSION =  " + FILE_EXTENSION);
        System.out.println("FILE_EXTENSION.length() =  " + FILE_EXTENSION.length());
        System.out.println("substring = " + lastFileName.substring(totalPrefix.length(), lastFileName.length() - FILE_EXTENSION.length()));
        System.out.println("lastFileNumber = " + lastFileNumber);
        System.out.println("New file name = " + newFileName);
    }
}

