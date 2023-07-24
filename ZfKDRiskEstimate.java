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

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;

import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataType;

 
/**
 * This class implements an example on how to use the API by providing CSV files
 * as input.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class ZfKDRiskEstimate extends Example {
     
    /**
     * Entry point.
     * 
     * @param args the arguments
     * @throws IOException
    */
    public static void main(String[] args) throws IOException {
         
        Data data = Data.create("test-data/zfkd_+_synthetic_rows_11001.csv", StandardCharsets.UTF_8, ',');

        // Define available hierarchies
        data.getDefinition().setAttributeType("Age", Hierarchy.create("data/adult_hierarchy_age.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("Geschlecht", Hierarchy.create("raff-hierarchies/raff_hierarchy_gender.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("Inzidenzort", Hierarchy.create("raff-hierarchies/raff_hierarchy_zipcode.csv", StandardCharsets.UTF_8, ';'));
                  
        // Define AttributeTypes
        // Quasi-Identifying Attributes: thes should be subject to gradual anonymization processes.
        data.getDefinition().setAttributeType("Age", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Geschlecht", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Inzidenzort", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);

        // Identifying Attributes: these should be erased and not visible in the output
        data.getDefinition().setAttributeType("Diagnosedatum", AttributeType.IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Verstorben", AttributeType.IDENTIFYING_ATTRIBUTE);
        
        // Sensitive Attributes: These should remain unaltered but be subject to privacy models.
        data.getDefinition().setAttributeType("Diagnose_ICD10_Code", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("Anzahl_Tage_Diagnose_Tod", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setDataType("Diagnose_ICD10_Code", DataType.STRING);
        data.getDefinition().setDataType("Anzahl_Tage_Diagnose_Tod", DataType.DECIMAL);
         
        // Perform risk analysis --> this calls RiskAnalysis.java (Example 29)
        // System.out.println("\n - Input data");
        // print(data.getHandle());
        System.out.println("\n - Quasi-identifiers sorted by risk:");
        RiskAnalysis.analyzeAttributes(data.getHandle());
        System.out.println("\n - Risk analysis:");
        RiskAnalysis.analyzeData(data.getHandle());

        // // Create an instance of the anonymizer
        // ARXAnonymizer anonymizer = new ARXAnonymizer();
        // ARXConfiguration config = ARXConfiguration.create();
        // config.addPrivacyModel(new KAnonymity(3));
        // config.addPrivacyModel(new HierarchicalDistanceTCloseness("Diagnose_ICD10_Code", 0.6d, getHierarchyDisease.createHierarchy()));
        // config.addPrivacyModel(new AverageReidentificationRisk(0.5d));
        // config.setSuppressionLimit(1d);

        // // Anonymize
        // ARXResult result = anonymizer.anonymize(data, config);

        // Perform risk analysis
        // System.out.println("\n - Output data");
        // print(result.getOutput());
        // System.out.println("\n - Risk analysis after anonymization:");
        // RiskAnalysis.analyzeData(result.getOutput());

        // Write results
        System.out.print(" - No Writing of data, because no anonymization is being done...");
        // result.getOutput(false).save("testproducts/riskEstimate_" + CSVFileManager.getNewFileName(), ';');
        System.out.println("Done!");
    }
}