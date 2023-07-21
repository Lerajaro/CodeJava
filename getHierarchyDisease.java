import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy;
/* This class serves as helper class to create custom Hierarchies */
public class getHierarchyDisease {
    public static Hierarchy createHierarchy() {

        String[] diseases = {"C81", "C79", "D17", "C90", "C92", "C71", "C86", "C82", "C68",
        "D10", "C91", "C54", "C53", "C72", "C57", "C94", "D11", "C93",
        "C84", "D04", "C24", "D03", "D28", "C95", "D01"};
                
        DefaultHierarchy disease = Hierarchy.create();

        for (int i = 0; i < diseases.length; i++) {
            disease.add(diseases[i], diseases[i] + "*", "***"); 
        }

        return disease;
    }
    public static void main(String[] args) {
        Hierarchy diseaseHierarchy = createHierarchy();
        System.out.println("New file name: " + diseaseHierarchy);
    }


}
