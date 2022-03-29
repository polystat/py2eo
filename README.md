
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

This is a translator of [Python](https://www.python.org/) to [EOLANG](https://www.eolang.org).
#### What is the traspiler? ####
> A source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

#### What is it? ####
This is a translator of [Python](https://www.python.org/) to [EOLANG](https://www.eolang.org). It is needed for transpiling `Python` code to `EOLANG` programming language. After successful transpiling final `EOLANG` code will be available for analyzing via [Polystat analyzer](https://github.com/polystat/polystat) which will notify you about leak places in the code. 

#### How does it work? ####
This transpiler receives as input data python code. Then received code is simplified with AST usage. After successfull simplyfying it is sent to the `py2eo` translator for getting `EOLANG`.

#### What do you need to use it? ####
* `Ubuntu` (20.04+) or `Windows` (7+)
* [Java 14+](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) - check in command line `java --version`. Use `sudo apt install openjdk-16-jdk-headless` in `Ubuntu 20` (there is no `java 14` package there).
* For contributors: `Maven 3.6.3` with `Java 14` or `Maven 3.8.4` with `Java 17` (but there is no `Maven 3.8` package in `Ubuntu` and no `Java 14` package, so manual installation is needed anyway)

#### How to use it? ####
- Go to some directory on your PC
- Create directory `Transpiler Test`
- Download and save into this folder `py2eo` transpiler executable from this [link](https://repo1.maven.org/maven2/org/polystat/py2eo/0.0.4/py2eo-0.0.4-jar-with-dependencies.jar)
- Create in this folder test file with `python` code named `sample_test.py` and paste the code below into it:
    ```
    def conditionalCheck2():
        a = 4
        b = 2
    ```
- Open command line and move to the folder `Transpiler Test`
- Run command `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar .\sample_test.py`
- Check output .eo file in `Transpiler Test/genCageEO/sample_test.eo` or in `path_to_output_folder/sample_test.eo` in case of usage `-o` command line parameter


#### Command line arguments ####
| Option  | Action |
| ------------- | ------------- |
| `-h,--help`  | Display help information  |
| `-o`  | path to output .eo file  |
| `-X,--debug`  | Produce execution debug output  |
| `-v,--version`  | Display version information  |

#### How to contribute? ####
- This instruction is for `Ubuntu 20.04`
- Check git version `git -version` or install it via `sudo apt install git`
- Install maven (`sudo apt install maven`)
- You need exactly `Java version 14`, so download it [here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz), unpack it and manually setup the `PATH` and `JAVA_HOME` variables. For example, you may open command line and run these commands:
```
cd ~
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
export JAVA_HOME="$PWD/jdk-14.0.1/"
```
- Check `java` version `java -version`
- Download project of [Python to EOLANG transpiler](https://github.com/polystat/py2eo) with source code (via `git clone` or downloading of `zip`)
- Run command `mvn clean package -DskipTests=true` in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables
- Open `./target` folder (via `cd target` for example)
- Create test file named `sample_test.py` in this folder and paste the code below into it:
    ```
    def conditionalCheck2():
        a = 4
        b = 2
    ```
- Run command `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar .\sample_test.py`
- Check output `.eo` file in `./genCageEO/sample_test.eo` 

#### Checker ####
This repository's CI includes checker - a tool that reduces project testing time using input test mutations. Checkout more [here](https://github.com/polystat/py2eo/blob/master/checker/).
