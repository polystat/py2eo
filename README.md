# Py2EO

<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg"
     height="100px"
     alt="Elegant Objects Cactus"/>

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)
[![Hits-of-Code](https://hitsofcode.com/github/polystat/py2eo)](https://hitsofcode.com/view/github/polystat/py2eo)
![Lines of code](https://img.shields.io/tokei/lines/github/polystat/py2eo)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

## Table of contents

1. [What is Py2EO?](#what-is-py2eo)
1. [Quick Start](#quick-start)
1. [How to contribute](#how-to-contribute)
1. [What's Next?](#whats-next)
1. [How to transpile Py to EO](#how-to-transpile-py-to-eo)
1. [Python syntax and tests coverage](#python-syntax-and-tests-coverage)
1. [Big project transpilation results](#big-project-transpilation-results)
   - [Parser tests](#parser-tests)
   - [Django](#django)
   - [CPython](#cpython)
1. [Architecture and design](#architecture-and-design)
1. [Not supported Python features](#not-supported-python-features)

## What is Py2EO?

This is a transpiler of [Python](https://www.python.org/) to
  [EOLANG](https://www.eolang.org).
It translates `Python` code to `EOLANG` programming language.

This transpiler receives python code as input data.
Then received code is simplified with several AST->AST passes.
After successfull simplification `EOLANG` output is generated.

`EOLANG` code [can be translated to java and executed][eo-quick-start]
  or analyzed statically via [Polystat analyzer][polystat].

> Traspiler is a source-to-source translator, source-to-source compiler
> (S2S compiler), transcompiler, or transpiler is a type of translator that
> takes the source code of a program written in a programming language as
> its input and produces an equivalent source code in the same or a
> different programming language

## Quick Start

Install [Java 14][jdk14], get [Py2EO executable][jar].

Then, start with a simple Python program in app.py file:

```python
print("Hello world!")
```

Transpile it:

```bash
java -jar <path-to-py2eo-executable> app.eo
```

You should get app.eo containing (among a lot of system stuff):

```eo
[] > apply
    stdout (sprintf "%s\n" ((pystring "Hello world!").as-string))
```

For detailed instructions [follow](#how-to-transpile-py-to-eo)

## How to contribute

Fork repository, make changes, send us a pull request.
We will review your changes and apply them to the master branch shortly,
  provided they don't violate our quality standards.
To avoid frustration, before sending us your pull request please run full
  Maven build:

```bash
mvn clean package
```

## What's Next?

Test it now on your own examples
  [following detailed instructions](#how-to-transpile-py-to-eo)

Examine our ways to test it [here](#python-syntax-and-tests-coverage)

Explore requirements and architecture design [here](#architecture-and-design)

Also note that you should use `Maven 3.6.3` with `Java 14` or `Maven 3.8.4`
  with `Java 17` (but there is no `Maven 3.8` package in `Ubuntu` and no
  `Java 14` package, so manual installation is needed anyway).

## How to transpile Py to EO

> Tested on `Ubuntu` (20.04+) and `Windows` (7+), but instructions are for
> `Ubuntu`

Install maven (`sudo apt install maven`) - it also installs default JDK
  (version 11 for now)

Install `Java` (JDK or JRE) version 14 (yes, exactly 14).
For example you can [download it here][jdk14] and unpack it:

```bash
cd ~
```

```bash
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```

```bash
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

You can either use [released transpiler executables][releases] or build it
  on your own:

Obtain [Py2EO master branch sources][sources] via
  `git clone https://github.com/polystat/py2eo.git` (install git via
  `sudo apt install git`), or download [zipped artifacts][zip]

Setup the `PATH` and `JAVA_HOME` variables, for example:

```bash
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```

```bash
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root and run `mvn clean package -DskipTests=true` in the same
  command line runtime were you have set `PATH` and `JAVA_HOME` variables,
  if succeeded you will get
  `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`

Create test file with `python` code (e.g. `sample_test.py` in Py2EO root),
  for example with these contents:

```python
def conditionalCheck2():
    a = 4
    b = 2
```

Run
  `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar <path/to/python/file>`,
  e. g:

```bash
java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar sample_test.py
```

Check output .eo file in the directory with python code with the same name
  (e. g. `sample_test.eo`).
Try using `-o` argument to specify output path and/or name if needed

Follow instructions on [how to run the resulting eo code][eo-quick-start] or
  [analyze with Polystat][polystat]

Additional arguments:

| Option         | Action                         |
|----------------|--------------------------------|
| `-h,--help`    | Display available options      |
| `-o <file>`    | Write output to `<file>`       |
| `-X,--debug`   | Produce execution debug output |
| `-v,--version` | Print version information      |

You can also use [yegor256/py2eo][docker-image] image for [Docker][docker]:

```bash
docker run -v $(pwd):/eo yegor256/py2eo hello.py -o hello.eo
```

This command will translate `hello.py` in the current directory, saving the
  output to the `hello.eo` file.

## Python syntax and tests coverage

For the parser and transpiler modules there are unit tests, located in
  `parser/src/test/scala/org/polystat/py2eo/parser/` and
  `transpiler/src/test/scala/org/polystat/py2eo/transpiler/` respectively.

> You can see this in the CI.
> Go to Actions → Java CI.
> Select any workflow run, go to test job and checkout the Build with Maven
> step.

We have [handwritten tests][tests] that are divided into groups by type:
  functional (also divided into groups by constructs in accordance with the
  language specification), integration tests (tests for the polystat
  analyzer), "negative" tests, etc.

[Functional tests][simple-tests], 1600+ lines of code.
A detailed description of the particular tests is given
  [on a separate wiki page][wiki-tests].
All these tests go through a full cycle of stages: from generating EO to
  executing Java.
Functional tests are grouped by folders corresponding to python syntax
  constructs we support or are going to support, so we have easy way to
  calculate overall coverage and `test passes successfully` state.
Progress is shown in each release description.

Functional tests prefixed with `eo_blocked_` are known to be blocked by
  bugs in EO.
In particular, the test `eo_blocked_nfbce` is blocked by
  [objectionary/eo#1249][eo-1249], all others are blocked by
  [objectionary/eo#1127][eo-1127].

For now we support `100.00%` of the determined python syntax subset and
  `100.00%` are passed successefully

> You can see this in the enabled tests counter CI.
> Go to Actions → Enabled tests counter.
> Select any workflow run and checkout the Run counter step.

To proof this (run all test and get statistics) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK
  (version 11 for now)

Install `Java` (JDK or JRE) version 14 (yes, exactly 14).
For example, you can [download it here][jdk14] and unpack it:

```bash
cd ~
```

```bash
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```

```bash
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources][sources] via
  `git clone https://github.com/polystat/py2eo.git` (install git via
  `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:

```bash
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```

```bash
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root and run in the same command line runtime were you have set
  `PATH` and `JAVA_HOME` variables:

Run transpilation

```bash
mvn clean test -q
```

Resulting eo-files are located in
  `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/results`.

Copy it to the runEO directory

```bash
cp transpiler/src/test/resources/org/polystat/py2eo/transpiler/results/*.eo ./transpiler/src/test/resources/org/polystat/py2eo/transpiler/runEO
```

Then copy the preface lib

```bash
cp -a transpiler/src/main/eo/preface ./transpiler/src/test/resources/org/polystat/py2eo/transpiler/runEO
```

And run EO compiler

```bash
cd ./transpiler/src/test/resources/org/polystat/py2eo/transpiler/runEO
```

```bash
mvn clean test
```

You will get detailed statistics in output.

## Big project transpilation results

Py2EO is capable of transpiling more than hundreds of thousands lines of
  python code.

### Parser tests

We tested the py2eo parser on [CPython][cpython-tests], python language
  implementation tests, version `3.8`.
For all tests (250,000+ lines of Python code), python source code is parsed
  and printed again, replacing the original one.
Then we run CPython's integration test to verify that printed tests are
  still valid.

> You can see this in the Integration Tests CI.
> Go to Actions → Integration Tests.
> Select any workflow run, go to the ParserPrinter job and checkout the Run
> integration tests step.

To proof this (parse all tests from CPython and launch `make test` on
  CPython) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK
  (version 11 for now).

Install `Java` (JDK or JRE) version 14 (yes, exactly 14).
For example you can [download it here][jdk14] and unpack it:

```bash
cd ~
```

```bash
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```

```bash
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources][sources] via
  `git clone https://github.com/polystat/py2eo.git` (install git via
  `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:

```bash
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```

```bash
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root in the same command line runtime were you have set `PATH`
  and `JAVA_HOME` variables and run Py2EO build

```bash
mvn clean package -DskipTests=true
```

if succeeded you will get
  `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.

To reprint python input tests and verify them afterwards run

```bash
mvn clean -Dit.test=ParserPrinterIT verify -B
```

You will get verification results in output.

### Django

We tested it on [Django][django], a popular `Python` web framework.
For all `.py` files (every `.py` is considered as particular test) from
  Django repository (440,000+ lines of Python code) `EO` is generated and
  passes `EO` syntax check stage.
Yet not tried to generate Java for this, since сompiling and execution of
  Java code obtained this way seems to be pointless.

> You can see this in the Integration Tests CI.
> Go to Actions → Integration Tests.
> Select any workflow run, go to the Django job and checkout the Run
> integration tests step.

To proof this (transpile Django python source code and perform EO syntax
  verification) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK
  (version 11 for now).

Install `Java` (JDK or JRE) version 14 (yes, exactly 14).
For example you can [download it here][jdk14] and unpack it:

```bash
cd ~
```

```bash
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```

```bash
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources][sources] via
  `git clone https://github.com/polystat/py2eo.git` (install git via
  `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:

```bash
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```

```bash
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root in the same command line runtime were you have set `PATH`
  and `JAVA_HOME` variables and run Py2EO build

```bash
mvn clean package -DskipTests=true
```

if succeeded you will get
  `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.

To generate EO files and verify EO syntax afterwards run

```bash
mvn clean -Dit.test=DjangoIT verify -B
```

You will get EO source code in
  `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/django`
  and verification (provided with EO) results in output.

### CPython

Also, we tested Py2EO on [CPython][cpython-tests], python language
  implementation tests, version `3.8`.
For all tests (250,000+ lines of Python code), `EO` is generated and passes
  `EO` syntax check stage.
Subsequent `Java` generation (and, therefore, `Java` compilation and
  execution), comes to `Python` runtime transpilation issue.
Got plans to come back to issue after majority of functional "simple" tests
  will pass.

> You can see this in the Integration Tests CI.
> Go to Actions → Integration Tests.
> Go to the CPython job, select any workflow run and checkout the Run
> integration tests step.

To proof this (transpile CPython tests source code and perform EO syntax
  verification) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK
  (version 11 for now).

Install `gcc` compiler.

Install `Java` (JDK or JRE) version 14 (yes, exactly 14).
For example, you can [download it here][jdk14] and unpack it:

```bash
cd ~
```

```bash
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```

```bash
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources][sources] via
  `git clone https://github.com/polystat/py2eo.git` (install git via
  `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:

```bash
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```

```bash
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root and in the same command line runtime were you have set
  `PATH` and `JAVA_HOME` variables run

```bash
mvn clean package -DskipTests=true
```

If succeeded you will get
  `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.

To generate EO files and verify EO syntax afterwards run

```bash
mvn clean -Dit.test=CPythonIT verify -B
```

You will get EO source code in
  `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/testParserPrinter/afterParser/cpython`
  and verification (provided with EO) results in output.

Also, we use **Checker** - a tool that reduces project testing time using
  input test mutations, as a part of test procedure.
It's included in CI.
Checkout more [here][checker].

## Architecture and design

Py2EO meets the following requirements:

- The jar executable should take the path of the python input file as a
  command line argument, and optionally take the path of the output file
- If the Python input file has valid Python 3.9 syntax, translate it to
  eolang and write the result to the output file provided, or place the
  result near the input file if no output files were provided
- If the input python does not have valid Python 3.9 syntax, inform the user
- The repository should provide a set of tests which can be transpiled to
  the executable eolang code
- The repository should provide tools for transpiling some big python
  project, indicate that no exceptions were thrown from the transpiler and
  the resulting eolang files are syntactically correct

Py2EO architecture can be described as the following workflow:

- The main function parses the command line arguments and (if an existing
  input file was provided) reads the file
- The parser module uses the ANTLR parser to build an abstract syntax tree
  and then maps its tree to our own internal representation (also based on
  AST)
- The SimplePass module applies several python-to-python passes to eliminate
  some constructions which are not supported in eolang
- The resulting simplified AST is then translated to the eolang code and
  printed to the provided output path or to the file next to the input file

Py2EO project consists of 3 modules: parser, checker, transpiler.
All the modules have sample unit tests in them.

## Not supported Python features

1. Any kind of yield, also coroutines and generators (incl generator
   expressions) -- no support in EO
1. Threads, async, futures, await -- no support in EO
1. Dynamic features of python (dynamic creation/change/lookup/deletion of
   variables, creation of classes with metaclasses etc., dynamic features
   of import) -- using completely dynamic features would make the output EO
   not statically analyzable
1. Multiple inheritance -- not obvious how to do that for a general case,
   but with the EO delegation principle in mind
1. The majority of standard library -- it is mostly written in C, so even if
   we support all of the python syntax, it is still a problem to support the
   library without rewriting it manually.
1. Star expressions are mostly not supported -- possible, but not yet
   finished
1. Array slicing is partially supported -- possible, but not yet finished
1. The import system is partially supported -- possible, but not yet
   finished

[checker]: https://github.com/polystat/py2eo/blob/master/checker/
[cpython-tests]: https://github.com/python/cpython/tree/3.8/Lib/test
[django]: https://github.com/django/django
[docker-image]: https://hub.docker.com/r/yegor256/py2eo
[docker]: https://docs.docker.com/get-docker/
[eo-1127]: https://github.com/objectionary/eo/issues/1127
[eo-1249]: https://github.com/objectionary/eo/issues/1249
[eo-quick-start]: https://github.com/objectionary/eo#quick-start
[jar]: https://repo1.maven.org/maven2/org/polystat/py2eo/transpiler/0.0.15/transpiler-0.0.15-jar-with-dependencies.jar
[jdk14]: https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz
[polystat]: https://github.com/polystat/polystat
[releases]: https://repo1.maven.org/maven2/org/polystat/py2eo/transpiler/
[simple-tests]: https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler/simple-tests
[sources]: https://github.com/polystat/py2eo
[tests]: https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler
[wiki-tests]: https://github.com/polystat/py2eo/wiki/Tests-Structure
[zip]: https://github.com/polystat/py2eo/archive/refs/heads/master.zip
