package deprecated; 
import java.io.*;

public class ZIPCodeHierarchyGenerator {
    public static void main(String[] args) {
        String outputFilePath = "hierarchies2/hierarchy_zipcode.csv";

        try {
            generateNumberPatterns(outputFilePath);
            System.out.println("ZIP code hierarchy generation completed. Output file: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void generateNumberPatterns(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int num = 0; num <= 99999; num++) {
                String number = String.format("%05d", num); // Format with leading zeros
                String[] columns = new String[6];

                for (int i = 0; i < 6; i++) {
                    StringBuilder pattern = new StringBuilder(number);
                    for (int j = 0; j < i; j++) {
                        int index = number.length() - 1 - j;
                        pattern.setCharAt(index, '*');
                    }
                    columns[i] = pattern.toString();
                }

                writer.write(String.join(";", columns));
                writer.newLine();
            }
        }
    }
}
