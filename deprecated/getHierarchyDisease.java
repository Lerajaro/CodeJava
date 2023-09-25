package deprecated;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy;
/* This class serves as helper class to create custom Hierarchies */
public class getHierarchyDisease {
    public static Hierarchy createHierarchy(String[] inputDiseaseStrings) {

        String[] diseases = inputDiseaseStrings;
                
        DefaultHierarchy disease = Hierarchy.create();

        for (int i = 0; i < diseases.length; i++) {
            disease.add(diseases[i], diseases[i] + "*", "***"); 
        }

        return disease;
    }
    public static void main(String[] args) {

    }


}
