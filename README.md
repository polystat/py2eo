
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/PetrB09/py2eo/actions/workflows/github-ci.yml)

This is a translator of Python to [EOLANG](https://www.eolang.org).

The project is in the module python/python3.

`mvn test` should work, the CI log may also help with commands to run.

The python3 module is also buildable in IDEA with the followin configuration at least: ubuntu 20.04 (important!), scala-2.13.6 (important!), jdk 1.8, python3 for tests. 

## Project structure

- py2eo - root maven project
      - test - maven project for run generated EO code via `eo-maven-plugin`, also contains Python files for tests
      - Java - directory with sources
         - Tests.scala - main tests file

For run EO code you need copy `.eo` files to `test/genCageEO` and run `cd ./test && mvn clean test`