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

//package org.deidentifier.arx.examples;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased.Order;
import org.deidentifier.arx.aggregates.StatisticsContingencyTable;
import org.deidentifier.arx.aggregates.StatisticsContingencyTable.Entry;
import org.deidentifier.arx.aggregates.StatisticsFrequencyDistribution;
import org.deidentifier.arx.criteria.KAnonymity;

/**
 * This class implements an example of how to use the API for access to basic statistics
 * about the data.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class OwnExample16 extends Example {

    private enum AttributeCategory {
        QUASI_IDENTIFYING, IDENTIFYING, SENSITIVE, INSENSITIVE
    }
    private static int[] QI_RESOLUTION = {0, 0, 0, 0, 0, 0};

    /**
     * Entry point.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) throws IOException {
        Constants.setData();
        defineAttributes(Constants.DATA); 
        setHierarchy(Constants.DATA);
        Data data = Constants.DATA;
        
        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));
        config.setSuppressionLimit(0d);

        ARXResult result = anonymizer.anonymize(data, config);

        // Print info
        printResult(result, data);

        // Print input
        System.out.println(" - Input data:");
        Iterator<String[]> original = data.getHandle().iterator();
        while (original.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(original.next()));
        }

        // Print results
        System.out.println(" - Transformed data:");
        Iterator<String[]> transformed = result.getOutput(false).iterator();
        while (transformed.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(transformed.next()));
        }

        // Print frequencies
        StatisticsFrequencyDistribution distribution;
        System.out.println(" - Distribution of attribute 'age' in input:");
        distribution = data.getHandle().getStatistics().getFrequencyDistribution(0, false);
        System.out.println("   " + Arrays.toString(distribution.values));
        System.out.println("   " + Arrays.toString(distribution.frequency));

        // Print frequencies
        System.out.println(" - Distribution of attribute 'age' in output:");
        distribution = result.getOutput(false).getStatistics().getFrequencyDistribution(0, true);
        System.out.println("   " + Arrays.toString(distribution.values));
        System.out.println("   " + Arrays.toString(distribution.frequency));

        // Print contingency tables
        StatisticsContingencyTable contingency;
        System.out.println(" - Contingency of attribute 'gender' and 'zipcode' in input:");
        contingency = data.getHandle().getStatistics().getContingencyTable(0, true, 2, true);
        System.out.println("   " + Arrays.toString(contingency.values1));
        System.out.println("   " + Arrays.toString(contingency.values2));
        while (contingency.iterator.hasNext()) {
            Entry e = contingency.iterator.next();
            System.out.println("   [" + e.value1 + ", " + e.value2 + ", " + e.frequency + "]");
        }

        // Print contingency tables
        System.out.println(" - Contingency of attribute 'gender' and 'zipcode' in output:");
        contingency = result.getOutput(false).getStatistics().getContingencyTable(0, true, 2, true);
        System.out.println("   " + Arrays.toString(contingency.values1));
        System.out.println("   " + Arrays.toString(contingency.values2));
        while (contingency.iterator.hasNext()) {
            Entry e = contingency.iterator.next();
            System.out.println("   [" + e.value1 + ", " + e.value2 + ", " + e.frequency + "]");
        }
    }

    private static String[][] defineAttributes(Data data) {
        System.out.println("\nSetting categories to attributes...");
        String[] identifyers = {};
        String[] quasiIdentifiers = Constants.QUASI_IDENTIFIER_CHOICE;
        String[] sensitives = Constants.SENSITIVES_CHOICE;
        String[] insensitives = {};
        String[][] variableTypes = {quasiIdentifiers, identifyers, sensitives, insensitives};
        for (AttributeCategory category : AttributeCategory.values()) {
            String[] variables = variableTypes[category.ordinal()];
            for (int i = 0; i < variables.length; i++) {
                String variable = variables[i];
                if (QI_RESOLUTION[i] != -1) { // All QI's that are not chosen are set to -1.
                    setAttributeType(data, variable, category);
                }
            }
        }
        return variableTypes; // this method could possibly return void....
    }
    
    private static void setAttributeType(Data data, String variableName, AttributeCategory category) {
        AttributeType attributeType;
        switch (category) {
            case QUASI_IDENTIFYING:
                attributeType = AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
                break;
            case IDENTIFYING:
                attributeType = AttributeType.IDENTIFYING_ATTRIBUTE;
                break;
            case SENSITIVE:
                attributeType = AttributeType.SENSITIVE_ATTRIBUTE;
                break;
            case INSENSITIVE:
                attributeType = AttributeType.INSENSITIVE_ATTRIBUTE;
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute category");
        }
        data.getDefinition().setAttributeType(variableName, attributeType);
    }

    private static void setHierarchy(Data data){
        try {
            System.out.println("Setting Hierarchies to according Quasi-Identifying attributes...");
            for (String attribute : Constants.QUASI_IDENTIFIER_CHOICE) {
                switch (attribute) {
                    case "Age":
                        data.getDefinition().setAttributeType("Age", Hierarchy.create(Constants.HIERARCHY_PATH + "age.csv", StandardCharsets.UTF_8, ';'));
                        break;
                    case "Geschlecht":
                        data.getDefinition().setAttributeType("Geschlecht", Hierarchy.create(Constants.HIERARCHY_PATH + "geschlecht.csv", StandardCharsets.UTF_8, ';'));
                        break;
                    case "Inzidenzort":
                        // data.getDefinition().setAttributeType("Inzidenzort", Hierarchy.create(Constants.HIERARCHY_PATH + "inzidenzort.csv", StandardCharsets.UTF_8, ';'));
                        data.getDefinition().setAttributeType("Inzidenzort", redationBasedHierarchy("Inzidenzort"));
                        break;
                    case "Diagnose_ICD10_Code":
                        data.getDefinition().setAttributeType("Diagnose_ICD10_Code", ICD10CodeHierarchy.redactHierarchyBuilder(getStringListFromData(data, "Diagnose_ICD10_Code")));
                        break;
                    case "Geburtsdatum":
                        // date(data, "Geburtsdatum");                        
                        break;
                    case "Diagnosedatum":
                        // date(data, "Diagnosedatum")
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid attribute for Hierarchy Settings");
                }
            }
        } catch (IOException e) {
            // Handle the IOException, e.g., print an error message or log the details
            System.err.println("Error creating hierarchies:");
            e.printStackTrace();
            // You can also throw a custom exception or handle it as needed
        }  
    }

    public static Hierarchy redationBasedHierarchy(String attribute) {
        System.out.println("Establishing a redaction Based Hierarchy for attribute: " + attribute + " ...");
        // Create the builder
        HierarchyBuilderRedactionBased<?> builder = HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
                                                                                    Order.RIGHT_TO_LEFT,
                                                                                    ' ', '*');
        int colIndex = Constants.DATA.getHandle().getColumnIndexOf(attribute);
        String[] values = Constants.DATA.getHandle().getDistinctValues(colIndex);
        builder.prepare(values);  
        AttributeType.Hierarchy hierarchy = builder.build();
        return hierarchy;
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
