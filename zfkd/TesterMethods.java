package zfkd;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;


import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class TesterMethods {
    public static void testHierarchyBuildingSuccess(String attribute) {
        // CAVE: This method will print out the whole Hierarchies in full extend line by line and therefor take very long.
        // This should only be used to specifically test a particular hierarchy, else the code will need to be interrupted with CTRL + C. 
        System.out.println("\nTesting Hierarchy Building Success for attribute: " + attribute);
        System.out.println("-------------------");
        System.out.println("Hierarchy for "+ attribute + " is: ");
        String[][] hierarchy = Constants.getData().getDefinition().getHierarchy(attribute);
        printStringArrayOfArrays(hierarchy);
        
    }

    public static void printStringArrayOfArrays(String[][] stringArray) {
        // Iterate through the rows and columns and print the elements
        for (int i = 0; i < stringArray.length; i++) {
           for (int j = 0; j < stringArray[i].length; j++) {
               System.out.print(stringArray[i][j] + "\t"); // Use "\t" for tab spacing
           }
           System.out.println(); // Move to the next line after each row
       }
   }

    public static void testDefineAttributes() {
        System.out.println("\nTesting Define Attributes");
        System.out.println("-------------------------");

        for (String attribute : Constants.QUASI_IDENTIFIER_FULL_SET) {
            System.out.println("Attribute type of " + attribute + " is " + Constants.getData().getHandle().getDefinition().getAttributeType(attribute));
        }
    }

    public static void testData() {
        System.out.println("Number of columns of DATA is: " + Constants.getData().getHandle().getNumColumns());
        System.out.println("Number of Rows of DATA is: " +  Constants.getData().getHandle().getNumRows());
        System.out.println("DATA ist locked: " + Constants.getData().getDefinition().isLocked());
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

    public static void testHierarchy(String attribute) {
        System.out.println("\nTesting Hierarchy for attriibute: " + attribute);
        String[][] hierarchyStringArray = Constants.getData().getDefinition().getHierarchy(attribute);
        printStringArrayOfArrays(hierarchyStringArray);
    }

    public static void printHierarchyToCSV(String attribute) {
        String[][] hierarchyStringArray = Constants.getData().getDefinition().getHierarchy(attribute);
        if (hierarchyStringArray != null) {
            String csvFileName = "zfkd/hierarchy-test-builds/" + attribute.toLowerCase() + ".csv"; // Change this to the desired CSV file name
            try (PrintWriter writer = new PrintWriter(new FileWriter(new File(csvFileName)))) {
                for (String[] row : hierarchyStringArray) {
                    for (int i = 0; i < row.length; i++) {
                        writer.print(row[i]);
                        if (i < row.length - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println(); // Move to the next line for the next row
                }
                System.out.println("CSV file has been created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Hierarchy String array is null");
        }
    }

    public static void testAttribute(String attribute) {
        System.out.println("\nAttribute Testing for attribute: " + attribute);
        System.out.println("-----------------");

        AttributeType attributeType = Constants.getData().getDefinition().getAttributeType(attribute);
        System.out.println("Attribute is of type: " + attributeType);
        if (attributeType != null) {
            System.out.println("AttributeType to String: " + attributeType.toString());
        }

        System.out.println("Hierarchy available: " + Constants.getData().getDefinition().isHierarchyAvailable(attribute));
        System.out.println("Hierarchy object is: " + Constants.getData().getDefinition().getHierarchyObject(attribute));
        if (attributeType == AttributeType.QUASI_IDENTIFYING_ATTRIBUTE) {
            System.out.println("Minimum Generalization: " + Constants.getData().getDefinition().getMinimumGeneralization(attribute));
            System.out.println("Maximum Generalization: " + Constants.getData().getDefinition().getMaximumGeneralization(attribute));
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
