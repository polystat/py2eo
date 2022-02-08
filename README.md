
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

This is a translator of Python to [EOLANG](https://www.eolang.org).
#### What is the traspiler? ####
> A source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a > program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

#### What is it? ####
This is a translator of Python to [EOLANG](https://www.eolang.org). It is needed for transpiling Python code to EO programming language. After successful transpiling final EO code will be available for analyzing via Polystat analyser which will notify you about leak places in the code. 

#### How does it work? ####
This transpiler receives as input data python code. Then received code is simplified with AST usage. After successfull simplyfying it is sent to the py2eo translator for getting EO.

#### What do you need to use it? ####
* Linux Ubuntu(16.04+) or Windows (7+)
* [Java 17+](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip) - check in command line `java --version`

#### How to use it? ####
- go to some directory on your PC
- create directory `Transpiler Test`
- download and save into this folder py2eo trsnspiler executable from this [link](https://repo1.maven.org/maven2/org/polystat/py2eo/0.0.3/py2eo-0.0.3-jar-with-dependencies.jar)
- create in this folder test file with python code named `sample_test.py` and paste the code below into it:
    ```
    def conditionalCheck2():
        a = 4
        b = 2
    ```
- open command line and move to the folder `Transpiler Test`
- run command `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar .\sample_test.py`
- check output .eo file in `Transpiler Test/genCageEO/sample_test.eo`


#### How to contribute? ####
- check java version `java -version` or install `sudo apt install openjdk-17-jdk-headless`
- check in command line installer Maven `mvn --version` or install it from [Maven 3.8+](https://maven.apache.org/download.cgi)
- download project of [Python to EOLANG transpiler](https://github.com/polystat/py2eo) with source code (via git clone or downloading of zip)
- open command line and go to the folder with downloaded project
- run comand `mvn clean package -DskipTests=true`
- open ./target folder (via `cd target` for example)
- create in this folder test file with python code named `sample_test.py` and paste the code below into it:
    ```
    def conditionalCheck2():
        a = 4
        b = 2
    ```
- run command `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar .\sample_test.py`
- check output .eo file in `./genCageEO/sample_test.eo`
