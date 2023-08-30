import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased.Order;
import org.deidentifier.arx.AttributeType.Hierarchy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ICD10CodeHierarchy extends Example{

    /**
     * Takes a String[] as argument and creates a redaction based arx-hierarchy from the unique values and returns it. Good for e.g. ICD10-Codes
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
        System.out.println("Resulting levels: "+Arrays.toString(builder.prepare(variableCodes)));
        
        System.out.println("");
        System.out.println("RESULT");
        
        Hierarchy hierarchy1 = builder.build();
        // Print resulting hierarchy
        //printArray(hierarchy1.getHierarchy());
        System.out.println("");

        return hierarchy1;
    }
    public static HierarchyBuilder<?> redactHierarchyBuilder(String[] variableStrings) {
        System.out.println("NOW INSIDE redactHierarchyBuilder"); 
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
}

