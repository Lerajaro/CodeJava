import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased.Order;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.ARXConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ICD10CodeHierarchy extends Example{

    /**
     * Takes a String[] as argument and creates a redaction based arx-hierarchy from the unique values and returns the hierarchy. Good for e.g. ICD10-Codes
     * @author Raffael Kniep
     */
    public static Hierarchy redactionBasedHierarchy(String[] variableStrings) {
        System.out.println("NOW INSIDE redactionBasedHierarchy");     
        Set<String> uniqueCodes = new HashSet<>();

        // Loop through the input ICD-10 codes and add them to the set
        for (String code : variableStrings) {
            uniqueCodes.add(code);
        }

        String[] variableCodes = uniqueCodes.toArray(new String[0]);

        String[] data = new String[variableCodes.length];
        for (int i = 0; i < variableCodes.length; i++) {
            data[i] = variableCodes[i];
        }
        // Create the builder
        HierarchyBuilderRedactionBased<?> builder = HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
                                                                                    Order.RIGHT_TO_LEFT,
                                                                                    ' ', '*');

        System.out.println("-------------------------");
        System.out.println("REDACTION-BASED HIERARCHY");
        System.out.println("-------------------------");
        System.out.println("");
        System.out.println("SPECIFICATION");
        
        
        // Print info about resulting groups
        // System.out.println("Resulting levels: "+Arrays.toString(builder.prepare(variableCodes)));
        
        System.out.println("");
        System.out.println("RESULT");
        builder.prepare(variableCodes);
        Hierarchy hierarchy1 = builder.build();
        // Print resulting hierarchy
        //printArray(hierarchy1.getHierarchy());
        System.out.println("");

        return hierarchy1;
    }
    public static HierarchyBuilder<?> redactHierarchyBuilder(String[] variableStrings) {
        // takes a column, creates a redacted hierarchy and returns ! the builder !
        // System.out.println("NOW INSIDE redactHierarchyBuilder"); 
        Set<String> uniqueCodes = new HashSet<>();

        // Loop through the input ICD-10 codes and add them to the set
        for (String code : variableStrings) {
            uniqueCodes.add(code);
        }

        String[] variableCodes = uniqueCodes.toArray(new String[0]);

        String[] data = new String[variableCodes.length];
        for (int i = 0; i < variableCodes.length; i++) {
            data[i] = variableCodes[i];
        }
        // Create the builder
        HierarchyBuilderRedactionBased<?> builder = HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
                                                                                    Order.RIGHT_TO_LEFT,
                                                                                    ' ', '*');
        
        builder.prepare(variableCodes);
            
        return builder;
    }
    public static HierarchyBuilder<?> redactHierarchyBuilder2(Data data, String attribute, int[] qiResolution) {
        // takes a column, creates a redacted hierarchy and returns ! the builder !
        // System.out.println("NOW INSIDE redactHierarchyBuilder"); 
        Set<String> uniqueCodes = new HashSet<>();

        String[] variableStrings = getStringListFromData(data, attribute);
        // Loop through the input ICD-10 codes and add them to the set
        for (String code : variableStrings) {
            uniqueCodes.add(code);
        }

        String[] variableCodes = uniqueCodes.toArray(new String[0]);

        String[] dataInput = new String[variableCodes.length];
        for (int i = 0; i < variableCodes.length; i++) {
            dataInput[i] = variableCodes[i];
        }
        // Create the builder
        HierarchyBuilderRedactionBased<?> builder = HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
                                                                                    Order.RIGHT_TO_LEFT,
                                                                                    ' ', '*');
        ARXConfiguration config = ARXConfiguration.create();
        builder.prepare(variableCodes);
        // builder.config.setMinimumGeneralizationLevel();
        // builder.config.setGeneralizationLevel();
        // builder.setMinimumGeneralizationLevel();
            
        return builder;
    }
    private static String[] getStringListFromData(Data inputData, String variableName) {
        // Load the CSV file using ARX's Data class
        Data data = inputData;
        
        // Get the index of the "Diagnose_ICD10_Code" column
        int icd10CodeColumnIndex = data.getHandle().getColumnIndexOf(variableName);

        // Get the number of rows in the column
        int numRows = data.getHandle().getNumRows();

        // Extract the values from the "Diagnose_ICD10_Code" column into a String[]
        String[] icd10Codes = new String[numRows - 1]; // Exclude the header row
        for (int i = 1; i < numRows; i++) { // Start from index 1 to skip the header
            icd10Codes[i - 1] = data.getHandle().getValue(i, icd10CodeColumnIndex);
        }
        return icd10Codes;
        // Now you have the "Diagnose_ICD10_Code" values in the icd10Codes array
        // You can pass this array to your redactionBasedHierarchy method
    }  
}

