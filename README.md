
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![CI checks](https://github.com/polystat/polystat/actions/workflows/mvn.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=polystat/py2eo)](http://www.0pdd.com/p?name=polystat/py2eo)
[![codecov](https://codecov.io/gh/polystat/py2eo/branch/master/graph/badge.svg)](https://codecov.io/gh/polystat/py2eo)

[![Javadoc](http://www.javadoc.io/badge/org.polystat/polystat.svg)](http://www.javadoc.io/doc/org.polystat/polystat)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/polystat/py2eo/blob/master/LICENSE.txt)
[![Maven Central](https://img.shields.io/maven-central/v/org.polystat/polystat.svg)](https://maven-badges.herokuapp.com/maven-central/org.polystat/polystat)
[![Hits-of-Code](https://hitsofcode.com/github/polystat/py2eo)](https://hitsofcode.com/view/github/polystat/py2eo)

This is a translator of Python to [EOLANG](https://www.eolang.org).

#### What is the traspiler? ####
> A source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a > program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

#### What is it? ####
This is a translator of Python to [EOLANG](https://www.eolang.org). It is needed for transpiling Python code to EO programming language. After successful transpiling final EO code will be available for analyzing via Polystat analyser which will notify you about leak places in the code. 

#### How does it work? ####
This transpiler receives as input data python code. Then with usage AST received code is simplified. After successfull simplyfying it is sent to the py2eo translator for getting EO.

#### What do you need to use it? ####
* Linux Ubuntu(16.04+) or Windows (7+)
* [Java 11+](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip) - check in command line `java --version`

#### How to use it? ####
- go to user directory on your PC
- create directory `Transpiler Test`
- download and save into this folder py2eo trsnspiler executable from this [link](https://repo1.maven.org/maven2/org/polystat/py2eo/0.0.3/py2eo-0.0.3-jar-with-dependencies.jar)
- download and unzip to this folder test python file [sample_test.zip](https://github.com/AndrewG0R/py2eo/files/7966053/sample_test.zip)
- open command line and move to the folder `Transpiler Test`
- run command `java -jar .\py2eo-0.0.3-jar-with-dependencies.jar .\sample_test.py`
- check output .eo file in `Transpiler Test/genCageEO/sample_test.eo`


#### How to contribute? ####
- check in command line installer Maven `mvn --version` or install it from [Maven 3.8+](https://maven.apache.org/download.cgi)
- download project of [Python to EOLANG transpiler](https://github.com/polystat/py2eo) with source code (via git clone or downloading of zip)
- go to user directory on your PC
- create directory `Transpiler Test`
- open command line and move to the folder `Transpiler Test`
- run comand `mvn clean package -DskipTests=true`
- open ./target folder (via `cd target` for example)
- download and unzip to this folder test python file [sample_test.zip](https://github.com/AndrewG0R/py2eo/files/7966053/sample_test.zip)
- create .py file with name `sample_test.py` with code:
```
def conditionalCheck2():
    a = 4
    b = 2

    return a > b
 ```  
- run command `java -jar .\py2eo-0.0.3-jar-with-dependencies.jar .\sample_test.py`
- check output .eo file in `Transpiler Test/genCageEO/sample_test.eo`

#### Related links ####
- https://github.com/cqfn/eo#:~:text=EO%20(stands%20for%20Elegant%20Objects,something%20we%20don't%20tolerate.
- https://www.eolang.org/
- https://www.yegor256.com/2016/11/29/eolang.html
