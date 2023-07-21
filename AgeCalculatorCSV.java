import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgeCalculatorCSV {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the path to the input CSV file.");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = "output.csv";

        try {
            List<String[]> data = readCSV(inputFilePath);
            List<String[]> newData = calculateAge(data);

            writeCSV(outputFilePath, newData);

            System.out.println("Age calculation completed. Output file: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            String[] header = headerLine.split(",");

            data.add(addColumn(header, "Age"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        }

        return data;
    }

    private static List<String[]> calculateAge(List<String[]> data) {
        List<String[]> newData = new ArrayList<>(data);

        int birthdateIndex = Arrays.asList(newData.get(0)).indexOf("Geburtsdatum");
        int diagnosisDateIndex = Arrays.asList(newData.get(0)).indexOf("Diagnosedatum");

        newData.get(0)[newData.get(0).length - 1] = "Age"; // Update header

        for (int i = 1; i < newData.size(); i++) {
            String[] row = newData.get(i);
            String birthdateStr = row[birthdateIndex];
            String diagnosisDateStr = row[diagnosisDateIndex];

            LocalDate birthdate = LocalDate.parse(birthdateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate diagnosisDate = LocalDate.parse(diagnosisDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            Period period = Period.between(birthdate, diagnosisDate);
            int age = period.getYears();

            row[row.length - 1] = Integer.toString(age); // Update the last column with age
        }

        return newData;
    }

    private static void writeCSV(String filePath, List<String[]> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    private static String[] addColumn(String[] row, String value) {
        String[] newRow = new String[row.length + 1];
        System.arraycopy(row, 0, newRow, 0, row.length);
        newRow[row.length] = value;
        return newRow;
    }
}
