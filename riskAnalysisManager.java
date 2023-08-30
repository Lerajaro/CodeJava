import java.io.File;
import java.util.Arrays;

public class riskAnalysisManager {
    private static String FILE_DIRECTORY = "zfkd-testproducts/";
    private static final String FILE_PREFIX = "test_";
    private static final String FILE_EXTENSION = ".txt";

    public static String getNewFileName(String fileNamePrefix, String DATA_SIZE) {
        System.out.println("Now inside riskAnalysisManager.java");
        File directory = new File(FILE_DIRECTORY);
        String[] existingFiles = directory.list();
        
        String totalPrefix = FILE_PREFIX + fileNamePrefix;
        if (fileNamePrefix == "kiggs_") {
            FILE_DIRECTORY = "kiggs-testproducts/";
        }
        if (fileNamePrefix == "zfkd_") {
            FILE_DIRECTORY = "zfkd-testproducts/";
        }
        if (existingFiles == null) {
            // The directory does not exist or is not a directory
            return FILE_DIRECTORY + totalPrefix + "1" + FILE_EXTENSION;
        }

        // Filter the existing files with the correct prefix and extension
        existingFiles = Arrays.stream(existingFiles)
                .filter(name -> name.startsWith(FILE_PREFIX) && name.endsWith(FILE_EXTENSION))
                .toArray(String[]::new);

        if (existingFiles.length == 0) {
            System.out.println("Now in existingFiles.length == 0");
            // No files with the prefix and extension found
            return totalPrefix + "1" + FILE_EXTENSION;
        }

        // Sort the existing files to get the last one
        Arrays.sort(existingFiles);

        // Get the last file name
        String lastFileName = existingFiles[existingFiles.length - 1];
        System.out.println("lastFileName = " + lastFileName);
        System.out.println("lastFileName.length() =  " + lastFileName.length());
        System.out.println("totalPrefix =  " + totalPrefix);
        System.out.println("totalPrefix.length() =  " + totalPrefix.length());
        System.out.println("FILE_EXTENSION =  " + FILE_EXTENSION);
        System.out.println("FILE_EXTENSION.length() =  " + FILE_EXTENSION.length());
        System.out.println("substring = " + lastFileName.substring(totalPrefix.length(), lastFileName.length() - FILE_EXTENSION.length()));

        // Extract the number from the last file name
        int lastFileNumber = Integer.parseInt(lastFileName.substring(totalPrefix.length(), lastFileName.length() - FILE_EXTENSION.length()));

        // Create and return the new file name with the next consecutive number
        String newFileName = FILE_PREFIX + fileNamePrefix + (lastFileNumber + 1) + FILE_EXTENSION;
        System.out.println("New file name: " + newFileName);
        return newFileName;
    }

    // Example of using the getNewFileName method
    public static void main(String[] args) {
        // String newFileName = getNewFileName(args);
        // System.out.println("New file name: " + newFileName);
    }
}

