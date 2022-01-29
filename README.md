
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)

This is a translator of Python to [EOLANG](https://www.eolang.org).

### Prerequisites ###
* Linux Ubuntu(16.04+) or Windows (7+)
* [Java 11+](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip) - check in command line `java --version`
* [Maven 3.8+](https://maven.apache.org/download.cgi) - check in command line `mvn --version`

### User Quick Start ###
----

Transpile Python to EOLang:
1. get jar from [release](https://mvnrepository.com/artifact/org.polystat/py2eo) with name lasting jar-with-dependencies.jar
2. Put yor python code to .py
3. open folder with jar file
4. run `java -jar path_to_jar path_to_py_code`
5. check output .eo file in `path_of_py/genCageEO/name_of_py.eo`


### Developer Quick Start ###
----

Transpile Python to EOLang:
1. Download project of [Python to EOLANG transpiler](https://github.com/polystat/py2eo) with source code (via git clone or downloading of zip)
2. Put yor python code to .py next to jar
3. Open the folder with downloaded project
4. run `mvn clean package -DskipTests=true`
5. open ./target folder (via `cd target` for example)
6. run `java -jar .\py2eo-0.0.1-SNAPSHOT-jar-with-dependencies.jar path_to_py_code `
7. check output .eo file in `path_of_py/genCageEO/name_of_py.eo`


### Sample for Quick Start ###
-----
Sample .py code:
```
def conditionalCheck2():
    a = 4
    b = 2

    return a > b
```
-----
Sample .eo output from the code scope above in the `path_of_py/genCageEO/name_of_py.eo`:
```
+package org.eolang
+alias goto org.eolang.gray.goto
+alias stdout org.eolang.io.stdout
+alias cage org.eolang.gray.cage
+junit

[unused] > test1
  cage > result
  cage > tmp
  [unused] > conditionalCheck1
    cage > result
    cage > tmp
    cage > a
    cage > e0
    cage > b
    goto > @
      [returnLabel]
        seq > @
          stdout "conditionalCheck1\n"
          (a).write (1)
          (b).write (2)
          (e0).write (((a).less (b)).@)
          [] > tmp1
            e0' > copy
            copy.< > @
          (result).write (tmp1.copy)
          returnLabel.forward 0
          123
  goto > @
    [returnLabel]
      seq > @
        stdout "test1\n"
        tmp.write (((conditionalCheck1)) 0)
        (tmp.@)
        (result).write (tmp.result)
        xresult
```



-----
-----
#### What is the traspiler? ####
> A source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a > program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

#### What is it? ####
This is a translator of Python to [EOLANG](https://www.eolang.org). It is needed for transpiling Python code to EO programming language. After successful transpiling final EO code will be available for analyzing via Polystat analyser which will notify you about leak places in the code. 

#### How does it work? ####
This transpiler receives as input data python code. Then with usage AST received code is simplified. After successfull simplyfying it is sent to the py2eo translator for getting EO.

#### What do you need to use it? ####
* Linux Ubuntu(16.04+) or Windows (7+)
* [Java 11+](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip) - check in command line `java --version`


