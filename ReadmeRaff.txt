ReadmeRaff.txt

JDK:
https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.7%2B7/OpenJDK17U-jdk_x86-32_windows_hotspot_17.0.7_7.zip 

absPathForJavac: "C:\Users\kniepr\Desktop\jdk-17.0.7+7\bin\javac.exe"

absPathforJava: "C:\Users\kniepr\Desktop\jdk-17.0.7+7\bin\java.exe"

absPathforJar: "C:\Users\kniepr\Desktop\jdk-17.0.7+7\bin\jar.exe"

get arx.library as jar-file to work: download from: https://arx.deidentifier.org/?ddownload=1924



absPathtoJarFile: "P:\CodeJava\lib\libarx-3.9.1.jar"

ARX examples: https://github.com/arx-deidentifier/arx/blob/master/src/example/org/deidentifier/arx/examples/Example.java

Laut ARX-Github: 
"Development setup

Currently, the main development of ARX is carried out using Eclipse as an IDE and Ant as a build tool. Support for further IDEs such as IntelliJ IDEA and Maven is experimental.

The Ant build script features various targets that can be used to build different versions of ARX (e.g. including GUI code or not). To build only the core code using Maven, set the system property core to true. This will build a platform independent jar with the ARX main code module and no GUI components:

$ mvn compile -Dcore=true
"
Aber, obwohl Maven im VSC installiert ist, erkennt mein Terminal nicht den mvn --version command und ich kann maven nicht nutzen.
stackoverflow: search for mvn.bat

C:\Users\kniepr\Desktop\jdk-17.0.7+7\bin\javac.exe -cp P:\CodeJava\lib\libarx-3.9.1.jar:. ZfKD.java 

to make libarx ARX library available, open jar-file:
Add Jar int JavaProjects -- Referenced Libraries --> "+" --> Select jar file from folder
--> Press Play

Hint: Work in wrong folder --> right click on file --> reveal in explorer --> VSCb folder works in C:/ at the moment


VSC-Shortcuts:
select all occurences of word: select word, press ctrl+shift+l to select all

Ctrl + K + Ctrl + C --> Comment in/out a whole textblock


from settings.json

    "maven.executable.path": ,
    "maven.excludedFolders": [

        "**/.*",
        "**/node_modules",
        "**/target",
        "**/bin",
        "**/archetype-resources"
    ], 
