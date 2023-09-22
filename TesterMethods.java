import org.deidentifier.arx.Data;
import java.util.Map;

public class TesterMethods {
    public static void testDefineAttributes(Data data) {
        System.out.println("Testing Define Attributes");
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            System.out.println("Attribute type of " + attribute + " is " + data.getDefinition().getAttributeType(attribute));
        }
    }

    public static void testGeneralizationSuccess(Data data) {
        System.out.println("Testing Generalizations");
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            System.out.println("Generalizationlevels of : "+ attribute + " are min: " + data.getDefinition().getMinimumGeneralization(attribute) + " and max: " + data.getDefinition().getMaximumGeneralization(attribute));
        }
    }
    public static void testHierarchyBuildingSuccess(Data data) {
        // CAVE: This method will print out the whole Hierarchies in full extend line by line and therefor take very long.
        // This should only be used to specifically test a particular hierarchy, else the code will need to be interrupted with CTRL + C. 
        System.out.println("Testing Hierarchies");
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            System.out.println("Hierarchy for "+ attribute + " is:\n" + data.getDefinition().getHierarchy(attribute));
            for (String[] strA : data.getDefinition().getHierarchy(attribute)) {
                for (String str : strA) {
                    System.out.print(str + ", ");
                }
                System.out.println("\n");
            }
        }
    }

    public static void TestIntArrays(int[] arrayToBePrinted){
        for (int i = 0; i < arrayToBePrinted.length; i++) {
            // Print each element followed by a space (or any separator you prefer)
            System.out.print(arrayToBePrinted[i] + " ");
        }
        System.out.println("\n");
    }

    public static void TestStringArrays(String[] variables, String clarifyer) {
        System.out.println("\nTesting String-Array from " + clarifyer);
        for (String str : variables) {
            System.out.print(str + " ");
        }
        System.out.println("\n");
    }

    public static void TestStringIntMaps(Map<String, Integer> mapToBeTested) {
        System.out.println("\nTesting String-Int-Map");
        // Iterate through the map and print key-value pairs
        for (Map.Entry<String, Integer> entry : mapToBeTested.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value);
        }
    }
}
