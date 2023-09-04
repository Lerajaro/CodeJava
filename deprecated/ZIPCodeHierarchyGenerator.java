package deprecated;
import java.io.*;

public class ZIPCodeHierarchyGenerator {
    public static void main(String[] args) {
        String outputFilePath = "raff_hierarchy_zipcode.csv";

        try {
            generateNumberPatterns(outputFilePath);
            System.out.println("ZIP code hierarchy generation completed. Output file: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void generateNumberPatterns(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int num = 11111; num <= 99999; num++) {
                String number = String.valueOf(num);
                String pattern = generatePattern(number);
                writer.write(number + ";" + pattern);
                writer.newLine();
            }
        }
    }

    private static String generatePattern(String number) {
        StringBuilder pattern = new StringBuilder();

        for (int i = number.length(); i > 0; i--) {
            pattern.append(number.substring(0, i));
            pattern.append("*".repeat(number.length() - i));
            pattern.append(";");
        }

        return pattern.toString();
    }
}
