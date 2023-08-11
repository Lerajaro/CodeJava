import java.io.File;
import java.util.Arrays;

public class CSVFileManager {
    private static final String FILE_DIRECTORY = "testproducts/";
    private static final String FILE_NAME_PREFIX = "test_zfkd_";
    private static final String FILE_EXTENSION = ".csv";

    public static String getNewFileName() {
        System.out.println("Now inside CSVFileManager.java");
        File directory = new File(FILE_DIRECTORY);
        String[] existingFiles = directory.list();

        if (existingFiles == null) {
            // The directory does not exist or is not a directory
            return FILE_NAME_PREFIX + "1" + FILE_EXTENSION;
        }

        // Filter the existing files with the correct prefix and extension
        existingFiles = Arrays.stream(existingFiles)
                .filter(name -> name.startsWith(FILE_NAME_PREFIX) && name.endsWith(FILE_EXTENSION))
                .toArray(String[]::new);

        if (existingFiles.length == 0) {
            // No files with the prefix and extension found
            return FILE_NAME_PREFIX + "1" + FILE_EXTENSION;
        }

        // Sort the existing files to get the last one
        Arrays.sort(existingFiles);

        // Get the last file name
        String lastFileName = existingFiles[existingFiles.length - 1];

        // Extract the number from the last file name
        int lastFileNumber = Integer.parseInt(lastFileName.substring(FILE_NAME_PREFIX.length(), lastFileName.length() - FILE_EXTENSION.length()));

        // Create and return the new file name with the next consecutive number
        return FILE_NAME_PREFIX + (lastFileNumber + 1) + FILE_EXTENSION;
    }

    // Example of using the getNewFileName method
    public static void main(String[] args) {
        String newFileName = getNewFileName();
        System.out.println("New file name: " + newFileName);
    }
}
