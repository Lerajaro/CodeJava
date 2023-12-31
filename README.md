# This software aims at developping an automization for the risk analysis of the ARX Anonymizer Software library.
# Preparation
## jupyter notebook files:
these files serve to prepare the datasets if necessary. If your dataset is all set, you can skip this part and continue with Java.
- increase the number of rows by randomly picking values from the existing columns and partly also creating new values (e.g. for age or postal code etc.)
- also creating a new "Age"-variable from "Diagnosedatum" and "Geburtsdatum".

## java files:
First go to Constants.java and set the quasi-identifiers, the file and folderpaths as desired.
QUASI_IDENTIFIER_CHOICE = Here should be only the quasi-identifier attribute names that you want to include in this specific run.
QUASI_IDENTIFIER_FULL_SET = This should contain the exact (case-sensitive) names of all quasi-identifiers that could/should be considered at some point. E.g. if in the first calculation you want    different quasi-identifiers than in the second, or third, you should add all unique quasi-identifiers of all in here.
FOLDER_PATH  is  the folder where your test-datasets should be held
FILE_PATH is the name of the specific file that you want to analyze
HIERARCHY_PATH is the folder where all ARX-hierachies for your QUASI_IDENTIFIER_CHOICEs should be stored. If you dont have any csv-hierarchies yet, first check the hierarchies(2) folder and see     if any of the available hierarchies suit your needs. Els you can check the hierarchyCreator-folder, if some of the creator-files are of any use to you, maybe in modified form.
CAVE: you will have to name your hierarchy-csv files exactly as the Quasi-identifiers are called (case-insensitive though). By setting the hierarchies you define the generalization methodology      that will be applied--so this action is very important. 
E.g. a single row for the age-attribute-hierarchy could be like:
  
[...]
19; 15-19; 10-20; <20; *
20; 20-24; 20-30; 20-64; *
[...]
66; 65-69; 60-70; >65; *
[...]
  
### Age Data Anonymization Table (Explanation)
The above table excerpt represents the anonymization of individual age data into different levels to protect privacy. The columns provide the following information:
Column 1: Individual ages in one-year increments.
Column 2: Age ranges, reducing granularity to 5-year increments.
Column 3: Age ranges, further grouping into 10-year increments.
Column 4: Age categories, simplifying to "<20" for ages 0-19 and ">65" for ages 65 and above.
Column 5: Anonymized representation with an asterisk (*) for privacy.

# Running the program
Explanation of the the logic
## Start the program via  Controller.main()
The controller will check in the hierarchies folder for the available hierarchies and count the columns of each file and save the result in a hashMap. This hashMap will then be adapted and ordered according to the actual Choice of Quasi-Identifiers. 
## Iteration over all possible combinations of generalization levels for choice of Quasi-Identifiers (karthesisches Produkt, vgl. Grid-Search). 
The program will call iterateQIResolution(args) and go through all possible QI's by index. In each iteration it will determine the number of max iterations per QI and set that value at that


 
