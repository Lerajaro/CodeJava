import org.deidentifier.arx.Data;

import deprecated.getHierarchyDisease;

public class HierarchyFileCreator {
    public static void main(Data inputData) {
        String folderPath = "hierarchies3/";
        Data data = inputData; 
        folderCreator(folderPath);
        redactCodeHierarchy(ICD10Codes);
        redactCodeHierarchy(ZIPCodes);
        dateHierarchies(birthDates);
        dateHierarchies(diagnosisDates);
        ageHierarchy();
        genderHierarchy();

    }
    public static void folderCreator(String folderPath) {

    }
    public static void redactCodeHierarchy(Data data) {
        getHierarchyDisease.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort"));
    }
    public static void dateHierarchies() {

    }
    public static void ageHierarchy() {

    }
    public static void genderHierarchy() {

    }
}
