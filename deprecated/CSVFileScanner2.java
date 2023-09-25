import java.io.File;
import java.io.IOException;
package deprecated;



public class CSVFileScanner2 {
    public static int[] CSVFileScanner2(String filePath) {

        File folder = new File(filePath);
        
        if (folder.exists() && folder.isDirectory()) {
            File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

            if (csvFiles != null) {
                int[] results = new int[csvFiles.length]; // Create an array to store results

                for (int i = 0; i < csvFiles.length; i++) {
                    try {
                        int numColumns = countColumnsInCSV(csvFiles[i]);
                        results[i] = numColumns; // Store the result in the array
                        System.out.println("File: " + csvFiles[i].getName() + " has " + numColumns + " columns.");
                    } catch (IOException e) {
                        System.err.println("Error processing file: " + csvFiles[i].getName());
                        e.printStackTrace();
                    }
                }
                System.out.println("Now returning real RESULTS");
                return results;
            }
        } else {
            System.err.println("The specified folder: '" + folder + "' does not exist or is not a directory.");
        }
        int[] test = new int[6];
        System.out.println("Now returning TEST");
        return test;
    }
}