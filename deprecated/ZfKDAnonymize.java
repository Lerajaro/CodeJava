package deprecated;
/*
* ARX Data Anonymization Tool
* Copyright 2012 - 2023 Fabian Prasser and contributors
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

// package org.deidentifier.arx.examples;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.aggregates.HierarchyBuilderDate;
import org.deidentifier.arx.aggregates.HierarchyBuilderDate.Granularity;



import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.AverageReidentificationRisk;
import org.deidentifier.arx.metric.Metric;

import Example;
 
/**
 * This class implements an example on how to import a csv file from the ZfKD with reduced variable size to:
 * [Geschlecht,Age,Inzidenzort,Diagnose_ICD10_Code,Diagnosedatum,Verstorben,Anzahl_Tage_Diagnose_Tod]
 * It then defines the different attributes of the variables, like SENSITIVE; INSENSITIVE; IDENTIFYING; QUASI-IDENTIFYING
 * It sets Hierarchies for the SENSITIVE and QUASI-IDENTIFYING attributes.  
 *
 * @author Raffael Kniep
 */
public class ZfKDAnonymize extends Example {
    // test products will follow the naming convention: Fachruppenkürzel + "_" + Herkunft des Datensatzes + "_" + automatic count of testproducts
    // please adapt FILE_NAME_PREFIX and DATA_PROVENIENCE to fit the Fachgruppenkürzel and Herkunft des Datensatzes.
    private static final String FILE_PATH = "test-data/zfkd_QI_adapted_70000.csv";
    private static final String FILE_NAME_PREFIX = "zfkd_";
    private static final String DATA_SIZE = "70000_";
    /**
     * Entry point.
     * 
     * @param args the arguments
     * @throws IOException
    */
    public static void main(String[] args) throws IOException {
         
        Data data = Data.create(FILE_PATH, StandardCharsets.UTF_8, ',');
        String[] quasiIdentifyers = {"Age", "Geschlecht", "Inzidenzort", "Geburtsdatum", "Diagnose_ICD10_Code", "Diagnosedatum"};
        String[] identifyers = {};
        String[] sensitives = {};
        String[] insensitives = {};
        String[][] variableTypes = {quasiIdentifyers, identifyers, sensitives, insensitives};

        // HIERARCHIES
        // Creating Hierarchies for date-variables
        date(data, "Geburtsdatum");
        date(data, "Diagnosedatum");
        // Creating Hierarchies for the other variables
        data.getDefinition().setAttributeType("Age", Hierarchy.create("hierarchies2/age.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("Geschlecht", Hierarchy.create("hierarchies2/gender.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("Inzidenzort", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort")));
        data.getDefinition().setAttributeType("Diagnose_ICD10_Code", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Diagnose_ICD10_Code")));
                
        // Hierarchy hierarchy1 = ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort")).build();
        // System.out.println("Hierarchy 1 = " + hierarchy1);
        // printArray(hierarchy1.getHierarchy());


        for (String[] variableType : variableTypes) {
            if (variableType.length > 0) {
                for (String variableName : variableType) {
                    if (Arrays.asList(quasiIdentifyers).contains(variableName)) {
                        data.getDefinition().setAttributeType(variableName, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                    } else if (Arrays.asList(identifyers).contains(variableName)) {
                        data.getDefinition().setAttributeType(variableName, AttributeType.IDENTIFYING_ATTRIBUTE);
                    } else if (Arrays.asList(sensitives).contains(variableName)) {
                        data.getDefinition().setAttributeType(variableName, AttributeType.SENSITIVE_ATTRIBUTE);
                    } else if (Arrays.asList(insensitives).contains(variableName)) {
                        data.getDefinition().setAttributeType(variableName, AttributeType.INSENSITIVE_ATTRIBUTE);
                    }
                     else {
                        System.out.println("No attribute names to process.");
                    }
                }
            }
        }    

        // data.getDefinition().setDataType("Diagnose_ICD10_Code", DataType.STRING);
        // data.getDefinition().setDataType("Anzahl_Tage_Diagnose_Tod", DataType.DECIMAL);
         
        // Perform risk analysis --> this calls RiskAnalysis.java (Example 29)
        System.out.println("\n - Input data");
        System.out.println("\n - Quasi-identifiers sorted by risk:");
        RiskAnalysis.analyzeAttributes(data.getHandle());
        System.out.println("\n - Risk analysis:");
        RiskAnalysis.analyzeData(data.getHandle());
        System.out.println("\n - Trying out the Risk Estimator");
        System.out.println(data.getHandle().getRiskEstimator());

        // Write results
        System.out.print(" - Writing data...");
        //result.getOutput(false).save("testproducts/" + CSVFileManager.getNewFileName(FILE_NAME_PREFIX, DATA_PROVENIENCE), ';');
        statisticsCreator(variableTypes, data, "risk-analysis/" + riskAnalysisManager.getNewFileName(FILE_NAME_PREFIX, DATA_SIZE));
        System.out.println("Done!");
    }
    
    private static void statisticsCreator(String[][] variableTypes, Data data, String outputFile) {
        try {
            PrintStream originalOut = System.out;  // Store original standard output
            
            // Redirect standard output to a ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            System.out.println("\n - Quasi-identifyers:");
            for (String quasiIdentifyer : variableTypes[0]){
                System.out.println(quasiIdentifyer);
            }
            // Perform risk analysis
            //System.out.println("\n - Risk analysis:");
            //RiskAnalysis.analyzeAttributes(data.getHandle());
            System.out.println("\n - Risk analysis:");
            RiskAnalysis.analyzeData2(data.getHandle());
            System.out.println("\n - Trying out the Risk Estimator");
            data.getHandle().getRiskEstimator();

            // Restore standard output
            System.setOut(originalOut);

            // Write captured output to the file
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(outputStream.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    private static void date(Data data, String variableNameString) {
        
    	String stringDateFormat = "yyyy-MM";
    	
    	DataType<Date> dateType = DataType.createDate(stringDateFormat);
    	
        // Create the builder
        HierarchyBuilderDate builder = HierarchyBuilderDate.create(dateType);
        
        // Define grouping
        builder.setGranularities(new Granularity[] {Granularity.QUARTER_YEAR, 
                                                    Granularity.YEAR, 
                                                    Granularity.DECADE});
        
        System.out.println("---------------------");
        System.out.println("DATE HIERARCHY");
        System.out.println("---------------------");
        System.out.println("");
        System.out.println("SPECIFICATION");
        
        // Print specification
        for (Granularity level : builder.getGranularities()) {
            System.out.println(level);
        }
        Integer columnIndexBirthDate = data.getHandle().getColumnIndexOf("Geburtsdatum");
        System.out.println("Column Index of Geburtsdatum: " + columnIndexBirthDate);
        String columnNameAtIndex = data.getHandle().getAttributeName(columnIndexBirthDate);
        System.out.println("Column Name at Index 3: " + columnNameAtIndex);
        String[] columnBirthDateStrings = data.getHandle().getDistinctValues(columnIndexBirthDate);

        // DataSubset birthdateSubset = 
        // Print info about resulting groups
        System.out.println("Resulting levels: "+Arrays.toString(builder.prepare(columnBirthDateStrings)));
        
        System.out.println("");
        System.out.println("RESULT");
        
        // Print resulting hierarchy
        printArray(builder.build().getHierarchy());
        System.out.println("");
    }
    
    
}