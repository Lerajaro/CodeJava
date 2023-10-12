package deprecated;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;


import java.util.Map;
import java.util.Arrays;

public class TesterMethods {
    public static void testDefineAttributes(DataDefinition dataDefinition) {
        System.out.println("\nTesting Define Attributes");
        System.out.println("-------------------------");

        for (String attribute : Constants.QUASI_IDENTIFIER_FULL_SET) {
            System.out.println("Attribute type of " + attribute + " is " + dataDefinition.getAttributeType(attribute));
        }
    }

    public static void testData() {
        System.out.println("Number of columns of DATA is: " + Constants.DATA.getHandle().getNumColumns());
        System.out.println("Number of Rows of DATA is: " +  Constants.DATA.getHandle().getNumRows());
        System.out.println("DATA ist locked: " + Constants.DATA.getDefinition().isLocked());
    }

    public static void testGeneralizationLevelSetting(DataDefinition dataDefinition) {
        System.out.println("\nTesting Generalization Levels");
        System.out.println("-----------------------");
        printQIResolution();
        for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
            int index = Arrays.asList(Constants.QUASI_IDENTIFIER_FULL_SET).indexOf(attribute);
            System.out.println("Generalization levels of "+ attribute + " at index: " + index + ". Minimum: " + dataDefinition.getMinimumGeneralization(attribute) + ". Maximum: " + dataDefinition.getMaximumGeneralization(attribute));
        }
    }
    public static void testHierarchyBuildingSuccess(DataDefinition dataDefinition) {
        // CAVE: This method will print out the whole Hierarchies in full extend line by line and therefor take very long.
        // This should only be used to specifically test a particular hierarchy, else the code will need to be interrupted with CTRL + C. 
        System.out.println("\nTesting Hierarchy Building Success...");
        System.out.println("-------------------");

        for (String attribute : Constants.QUASI_IDENTIFIER_FULL_SET) {
            System.out.println("Hierarchy for "+ attribute + " is: " + dataDefinition.getHierarchyObject(attribute));
        }
    }

    public static void testRedactionBasedHierarchy(String attribute) {
        System.out.println("\nTesting RedactionBasedHierarchy for attriibute: " + attribute);
        // Some logic here
    }

    public static void testAttribute(String attribute) {
        System.out.println("\nAttribute Testing for attribute: " + attribute);
        System.out.println("-----------------");

        AttributeType attributeType = Constants.DATA.getDefinition().getAttributeType(attribute);
        System.out.println("Attribute is of type: " + attributeType);
        if (attributeType != null) {
            System.out.println("AttributeType to String: " + attributeType.toString());
        }

        System.out.println("Hierarchy available: " + Constants.DATA.getDefinition().isHierarchyAvailable(attribute));
        System.out.println("Hierarchy object is: " + Constants.DATA.getDefinition().getHierarchyObject(attribute));
        if (attributeType == AttributeType.QUASI_IDENTIFYING_ATTRIBUTE) {
            System.out.println("Minimum Generalization: " + Constants.DATA.getDefinition().getMinimumGeneralization(attribute));
            System.out.println("Maximum Generalization: " + Constants.DATA.getDefinition().getMaximumGeneralization(attribute));
        }        
    }

    public static void testQIGeneralization(DataDefinition dataDefinition) {
        System.out.println("QI's with Generalization: " + dataDefinition.getQuasiIdentifiersWithGeneralization());
        System.out.println("QI's with Microaggregation: " + dataDefinition.getQuasiIdentifiersWithMicroaggregation());
    }

    public static void testResult(ARXResult result) {
        System.out.println("\nNow testing result Output Definitions:");
        System.out.println("--------------------------------------");
        System.out.println("Input Data: " + result.getInput());
        System.out.println("Output Data: " + result.getOutput());
        System.out.println("Configuration: " + result.getConfiguration());
        System.out.println("Optimum found? : " + result. getOptimumFound());
        ARXLattice lattice = result.getLattice();
        ARXLattice.ARXNode optimumNode = result.getGlobalOptimum();
        System.out.println("\nTesting Optimum Node");
        testNode(optimumNode);
        ARXLattice.ARXNode topNode = lattice.getTop();
        System.out.println("\nTesting Top Node");
        testNode(topNode);
        ARXLattice.ARXNode bottomNode = lattice.getBottom();
        System.out.println("\nTesting Bottom Node");
        testNode(bottomNode);
    }

    public static void testNode(ARXLattice.ARXNode node) {
        System.out.println("Node: " + node);
        System.out.println("Attributes of Node: ");
        TestStringArrays(node.getQuasiIdentifyingAttributes(), "");
        System.out.println("Total Generalization Level: " + node.getTotalGeneralizationLevel());
        System.out.println("Anonymity: " + node.getAnonymity());
    }

    public static void testIntArrays(int[] arrayToBePrinted){
        for (int i = 0; i < arrayToBePrinted.length; i++) {
            // Print each element followed by a space (or any separator you prefer)
            System.out.print(arrayToBePrinted[i] + " ");
        }
        System.out.println("\n");
    }

    public static void printQIResolution() {
        System.out.print("QI_Resolution for this iteration: {");
        for (int i = 0; i < Constants.getQIResolution().length; i++) {
            System.out.print(Constants.getQIResolution()[i]);
            if (i < Constants.getQIResolution().length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }

    public static void TestStringArrays(String[] variables, String clarifyer) {
        // System.out.println("\nTesting String-Array from " + clarifyer);
        for (String str : variables) {
            System.out.print(str + " ");
        }
        System.out.println("\n");
    }

    public static void TestStringIntMaps(Map<String, Integer> mapToBeTested, String variableName) {
        System.out.println("\nTesting String-Int-Map of " + variableName);
        // Iterate through the map and print key-value pairs
        for (Map.Entry<String, Integer> entry : mapToBeTested.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value);
        }
    }
}
