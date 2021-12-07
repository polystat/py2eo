
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/PetrB09/py2eo/actions/workflows/github-ci.yml)

This is a translator of Python to [EOLANG](https://www.eolang.org).

`mvn test` should work, the CI log may also help with commands to run.

To run EO code you need to copy `.eo` files to `test/src/main/eo` and run `cd ./test && mvn clean test`

The python3 module is also buildable in IDEA with the followin configuration at least: ubuntu 20.04 (important!), scala-2.13.6 (important!), jdk 1.8, python3 for tests. 

## Project structure

- py2eo - root maven project
    - test - maven project for run generated EO code via `eo-maven-plugin`, also contains Python files for tests
        - afterParser - result of working AST.java with .py files. In the output will be available identical files with 
    - Java - directory with sources
       - Tests.scala - main tests file
       - AST.scala - python parser
       - SimpleAnalysis.java - processor for python constructions
       - PrintPython.java - 
       - common.skala - 
       - ClosureWithCage.java - 

