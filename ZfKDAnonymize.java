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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;

import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.AverageReidentificationRisk;
import org.deidentifier.arx.metric.Metric;
 
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
    private static final String FILE_PATH = "test-data/zfkd_QI_adapted_100000.csv";
    //private static final String FILE_NAME_PREFIX = "zfkd_";
    //private static final String DATA_PROVENIENCE = "QI_";
    /**
     * Entry point.
     * 
     * @param args the arguments
     * @throws IOException
    */
    public static void main(String[] args) throws IOException {
         
        Data data = Data.create(FILE_PATH, StandardCharsets.UTF_8, ',');
        String[] quasiIdentifyers = {"Age", "Geschlecht", "Inzidenzort", "Geburtsdatum"};
        String[] identifyers = {};
        String[] sensitives = {};
        String[] insensitives = {};


        // Define available hierarchies  

        //data.getDefinition().setAttributeType("Age", Hierarchy.create("data/adult_hierarchy_age.csv", StandardCharsets.UTF_8, ';'));
        //data.getDefinition().setAttributeType("Geschlecht", Hierarchy.create("raff-hierarchies/raff_hierarchy_gender.csv", StandardCharsets.UTF_8, ';'));
        //data.getDefinition().setAttributeType("Inzidenzort", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Inzidenzort")));

        // Quasi-Identifying Attributes: these will used for the risk analyisis and are subject to generalization in anonymization procedures
        for (String quasiIdentifyer : quasiIdentifyers) {
            data.getDefinition().setAttributeType(quasiIdentifyer, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        }
        // Identifying Attributes: these should be erased and not be visible in the output.
        for (String identifyer : identifyers) {
            data.getDefinition().setAttributeType(identifyer, AttributeType.IDENTIFYING_ATTRIBUTE);
        }
        // Sensitive Attributes: These should remain unaltered but be subject to privacy models.
        for (String sensitive : sensitives) {
            data.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
        }
        // Insensitive Attributes: Will remain untouched and should not be considered in risk analysis.
        for (String insensitive : insensitives) {
            data.getDefinition().setAttributeType(insensitive, AttributeType.INSENSITIVE_ATTRIBUTE);
        }

        // data.getDefinition().setDataType("Diagnose_ICD10_Code", DataType.STRING);
        // data.getDefinition().setDataType("Anzahl_Tage_Diagnose_Tod", DataType.DECIMAL);
         
        // Perform risk analysis --> this calls RiskAnalysis.java (Example 29)
        System.out.println("\n - Input data");
        System.out.println("\n - Quasi-identifiers sorted by risk:");
        RiskAnalysis.analyzeAttributes(data.getHandle());
        System.out.println("\n - Risk analysis:");
        RiskAnalysis.analyzeData(data.getHandle());

        // Create an instance of the anonymizer (Example 13)
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.addPrivacyModel(new HierarchicalDistanceTCloseness("Diagnose_ICD10_Code", 0.6d, ICD10CodeHierarchy.redactionBasedHierarchy(getStringListFromData(data, "Diagnose_ICD10_Code"))));
        // config.addPrivacyModel(new HierarchicalDistanceTCloseness("Diagnose_ICD10_Code", 0.6d, getHierarchyDisease.createHierarchy()));
        // config.addPrivacyModel(new RecursiveCLDiversity("Anzahl_Tage_Diagnose_Tod", 3d, 2));
        config.addPrivacyModel(new AverageReidentificationRisk(0.5d));
        config.setSuppressionLimit(0d);
        config.setQualityModel(Metric.createEntropyMetric());       
        
        // Execute the algorithm
        //ARXResult result = anonymizer.anonymize(data, config); 
         
        // Print info
        //printResult(result, data);

        // Write results
        System.out.print(" - Writing data...");
        //result.getOutput(false).save("testproducts/" + CSVFileManager.getNewFileName(FILE_NAME_PREFIX, DATA_PROVENIENCE), ';');
        System.out.println("Done!");
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