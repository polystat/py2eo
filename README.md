
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" /> 

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)
[![Hits-of-Code](https://hitsofcode.com/github/polystat/py2eo)](https://hitsofcode.com/view/github/polystat/py2eo)
![Lines of code](https://img.shields.io/tokei/lines/github/polystat/py2eo)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

## What is Py2EO?
This is a transpiler of [Python](https://www.python.org/) to [EOLANG](https://www.eolang.org). It translates `Python` code to `EOLANG` programming language.

This transpiler receives python code as input data. Then received code is simplified with several AST->AST passes. After successfull simplification `EOLANG` output is generated.

`EOLANG` code [can be translated to java and executed](https://github.com/objectionary/eo#quick-start) or analyzed statically via [Polystat analyzer](https://github.com/polystat/polystat).

> Traspiler is a source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

## Quick Start
Install [Java 14](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz), get [Py2EO executable](https://repo1.maven.org/maven2/org/polystat/py2eo/transpiler/0.0.15/transpiler-0.0.15-jar-with-dependencies.jar).

Then, start with a simple Python program in app.py file:
```
print("Hello world!")
```

Transpile it:
```
java -jar <path-to-py2eo-executable> app.eo
```

You should get app.eo containing (among a lot of system stuff):
```
[] > apply
    stdout (sprintf "%s\n" ((pystring "Hello world!").as-string))
```

For detailed instructions [follow](#how-to-transpile-py-to-eo)

## How to contribute
    
Fork repository, make changes, send us a pull request. We will review your changes and apply them to the master branch shortly, provided they don't violate our quality standards. To avoid frustration, before sending us your pull request please run full Maven build:
```
mvn clean package
```
    
## What's Next?

Test it now on your own examples [following detailed instructions](#how-to-transpile-py-to-eo)
    
Examine our ways to test it [here](#python-syntax-and-tests-coverage)
    
Explore requirements and architecture design [here](#architecture-and-design)

Also note that you should use `Maven 3.6.3` with `Java 14` or `Maven 3.8.4` with `Java 17` (but there is no `Maven 3.8` package in `Ubuntu` and no `Java 14` package, so manual installation is needed anyway).

## How to transpile Py to EO
> Tested on `Ubuntu` (20.04+) and `Windows` (7+), but instuctions are for `Ubuntu`

Install maven (`sudo apt install maven`) - it also installs default JDK (version 11 for now)

Install `Java` (JDK or JRE) version 14 (yes, exactly 14). For example you can [download it here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) and unpack it:

```
cd ~
```
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```
```
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

You can either use [released transpiler executables](https://repo1.maven.org/maven2/org/polystat/py2eo/transpiler/) or build it on your own: 

Obtain [Py2EO master branch sources](https://github.com/polystat/py2eo) via `git clone https://github.com/polystat/py2eo.git` (install git via `sudo apt install git`), or download [zipped artifacts](https://github.com/polystat/py2eo/archive/refs/heads/master.zip)

Setup the `PATH` and `JAVA_HOME` variables, for example:
```
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```
```
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2EO root and run `mvn clean package -DskipTests=true` in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables, if succeeded you will get `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`

Create test file with `python` code (e. g. `sample_test.py` in Py2EO root), for example with these contents:
```
def conditionalCheck2():
    a = 4
    b = 2
```

Run `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar <path/to/python/file>`, e. g:
```
java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar sample_test.py
```

Check output .eo file in the directory with python code with the same name (e. g. `sample_test.eo`). Try using `-o` argument to specify output path and/or name if needed

Follow instructions on [how to run the resulting eo code](https://github.com/objectionary/eo#quick-start) or [analyze with Polystat](https://github.com/polystat/polystat)

Additional arguments:

| Option         | Action                         |
|----------------|--------------------------------|
| `-h,--help`    | Display available options      |
| `-o <file>`    | Write output to <file>         |
| `-X,--debug`   | Produce execution debug output |
| `-v,--version` | Print version information      |

You can also use [yegor256/py2eo](https://hub.docker.com/r/yegor256/py2eo) image for [Docker](https://docs.docker.com/get-docker/):

```
$ docker run -v $(pwd):/eo yegor256/py2eo hello.py -o hello.eo
```

This command will translate `hello.py` in the current directory, saving the output to the `hello.eo` file.

## Python syntax and tests coverage
We have [handwritten tests](https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler) that are divided into groups by type: functional (also divided into groups by constructs in accordance with the language specification), integration tests (tests for the polystat analyzer), "negative" tests, etc.

[Functional tests](https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler/simple-tests), 1600+ lines of code. A detailed description of the particular tests is given [on a separate wiki page](https://github.com/polystat/py2eo/wiki/Tests-Structure). All these tests go through a full cycle of stages: from generating EO to executing Java. Functional tests are grouped by folders corresponding to python syntax constructs we support or are going to support, so we have easy way to calculate overall coverage and `test passes successefully` state. Progress is shown in each release description.

#### For now we support `52.9%` of python syntax and `57.2%` are passed successefully ####
  
To proof this (run all test and get statistics) on clean `Ubuntu` (20.04+):
       
Install maven (`sudo apt install maven`) - it also installs default JDK (version 11 for now)

Install `Java` (JDK or JRE) version 14 (yes, exactly 14). For example you can [download it here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) and unpack it:

```
cd ~
```
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```
```
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources](https://github.com/polystat/py2eo) via `git clone https://github.com/polystat/py2eo.git` (install git via `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:
```
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```
```
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2RO root and run in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables
```
mvn clean package -DskipTests=true`
```
if succeeded you will get `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.
       
Run transpilation
```
mvn clean verify
```
Resulting eo-files are located in `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/results`.

Copy it to the runEO directory 
```
cp transpiler/src/test/resources/org/polystat/py2eo/transpiler/results/*.eo ./runEO
```
Then copy the preface lib
```
cp -a transpiler/src/main/eo/preface ./runEO
```
And run EO compiler
```
cd ./runEO
```
```
mvn clean test
```
You will get detailed statistics in output.

#### Py2EO is capable of transpiling more than hundreds of thousands lines of python code ####

We tested it on [Django](https://github.com/django/django), a popular `Python` web framework. For all `.py` files (every `.py` is considered as particular test) from Django repository (440,000+ lines of Python code) `EO` is generated and passes `EO` syntax check stage. Yet not tried to generate Java for this, since сompiling and execution of Java code obtained this way seems to be pointless.

To proof this (transpile Django python source code and perform EO syntax verification) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK (version 11 for now).

Install `Java` (JDK or JRE) version 14 (yes, exactly 14). For example you can [download it here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) and unpack it:

```
cd ~
```
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```
```
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources](https://github.com/polystat/py2eo) via `git clone https://github.com/polystat/py2eo.git` (install git via `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:
```
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```
```
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2RO root in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables and run Py2EO build
```
mvn clean package -DskipTests=true
```
if succeeded you will get `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.

To generate EO files and verify EO syntax afterwards run
```
mvn clean verify -B -Pdjango
```
You will get EO source code in `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/results` and verification (provided with EO) results in output.
       
Also, we tested Py2EO on [CPython](https://github.com/python/cpython/tree/3.8/Lib/test), python language implementation tests, version `3.8`. For all tests (250,000+ lines of Python code), `EO` is generated and passes `EO` syntax check stage. Subsequent `Java` generation (and, therefore, `Java` compilation and execution), comes to `Python` runtime transpilation issue. Got plans to come back to issue after majority of functional "simple" tests will pass.

To proof this (transpile CPython tests source code and perform EO syntax verification) on clean `Ubuntu` (20.04+):

Install maven (`sudo apt install maven`) - it also installs default JDK (version 11 for now)
*Install gcc compiler**

Install `Java` (JDK or JRE) version 14 (yes, exactly 14). For example you can [download it here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) and unpack it:

```
cd ~
```
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
```
```gz
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```

Obtain [Py2EO master branch sources](https://github.com/polystat/py2eo) via `git clone https://github.com/polystat/py2eo.git` (install git via `sudo apt install git`).

Setup the `PATH` and `JAVA_HOME` variables, for example:
```
PATH="$PWD/jdk-14.0.1/bin/:$PATH"
```
```
export JAVA_HOME="$PWD/jdk-14.0.1/"
```

> Check (e. g. via `java -version`) that version `14.*` is used

Go to Py2RO root and in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables run 
```
mvn clean package -DskipTests=true
```
If succeeded you will get `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`.
       
To generate EO files and verify EO syntax afterwards run 
```
mvn clean verify -B -Pcpython
```
You will get EO source code in `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/results` and verification (provided with EO) results in output.

Also we use **Checker** - a tool that reduces project testing time using input test mutations, as a part of test procedure . It's included in CI. Checkout more [here](https://github.com/polystat/py2eo/blob/master/checker/).

## Architecture and design
After we performed a deep reliminary analysis of the [python language](https://docs.python.org/3.8/reference/) and [EOlang](https://github.com/objectionary/eo) we had choosen methods, techniques and tools that are implemented now:

Py2EO meets the following requirements:
- The jar executable should take the path of the python input file as a command line argument, and optionally take the path of the output file
- If the Python input file has valid Python 3.9 syntax, translate it to eolang and write the result to the output file provided, or place the result near the input file if no output files were provided
- If the input python does not have valid Python 3.9 syntax, inform the user
- The repository should provide a set of tests which can be transpiled to the executable eolang code
- The repository should provide tools for transpiling some big python project, indicate that no exceptions were thrown from the transpiler and the resulting eolang files are syntactically correct

Py2EO architecture can be described as the following workflow:
* The main function parses the command line arguments and (if an existing input file was provided) reads the file
* The parser module uses the ANTLR parser to build an abstract syntax tree and then maps its tree to our own internal representation (also based on AST)
* The SimplePass module applies several python-to-python passes to eliminate some constructions which are not supported in eolang. These include:
  * Eliminate `if-else` statements with > 2 branches (rewrite an if-elsif-else as many if-else statements)
  * Eliminate `for`: rewrite it as a `while` + `try`
  * Prepend each identifier with `x`, so that no identifier starts with a capital letter 
  * Standardize (simplify) quotes for string literals
  * Simplify `except` clauses for exception handling: change multiple `except` clauses of a `try` block to one `except:`, which catches everything, and a series of `if-else`, which emulates the behaviour of all the typed `except` clauses and reraises the exception if it should not be caught 
  * Eliminate `if-else` again (because complex if-else statements appear as a result of the previous pass)
  * Eliminate complex assignments (i.e., translate `a = b = c` to `b = c; a = b`)
  * Eliminate simple cases of method calls (that is, substitute pointer to `this` explicitly)
  * Extract all function calls to the statement level to make the execution order explicit (i.e., translate `a = f(1) + g(2)` to `tmp1 = f(1); tmp2 = g(2); a = tmp1 + tmp2`
* The resulting simplified AST is then translated to the eolang code and printed to the provided output path or to the file next to the input file

## How do we project Python to EOLang

After we performed a deep reliminary analysis of the [python language](https://docs.python.org/3.8/reference/) and [EOlang](https://github.com/objectionary/eo) we determined the subset of Python features (listed in this section) for implementing together with full list of restrictions to made design decisions that are illustrated with the following examples of translation projections.

> Here we reference to the [python language reference version 3.8.1](https://docs.python.org/3.8/reference/) and are following the order of presentaion prodosed there.
       
Let's start from classic "Hello, world!"
       
`print(x)` is translated to `stdout (sprintf "%s" (xx.as-string))`
 You may use this example:
 ```
 x = 1
 print(x)
 ```
 or this
 ```
 print("Hello, world!")
 ```

Comments, Identation, Explicit and Implicit line joining, Whitespace between tokens ([see sec 2](https://docs.python.org/3.8/reference/lexical_analysis.html)) are supported by the parser. No additional support is needed, because these are just pecularities of the syntax. 

A Python program is constructed from code blocks ([see sec 4](https://docs.python.org/3.8/reference/executionmodel.html)). A block is a piece of Python program text that is executed as a unit. Names refer to objects. Names are introduced by name binding operations. Dynamically adding/removing names is not supported. All the statically known names are implemented as a `cage` object of EO. This allows to implement assignments. EO objects are also visibility scopes for identifiers, so several variables with the same name in different scopes are implemented directly.
    
Exceptions, `break`, `continue`, `return` ([see sec 4](https://docs.python.org/3.8/reference/executionmodel.html)) are currently all implemented with the help of the `goto` object.   
    
Consider this python code
```
while True: break
```
We translate it this way
```
goto
  [doBreak]
    TRUE.while
      doBreak.forward 0
```

Now consider this:
```
flag = 5
while True:
  try:
    break
  finally: flag = 10
```
From [this section](https://docs.python.org/3/reference/compound_stmts.html#the-try-statement) we know that "When a [return](https://docs.python.org/3/reference/simple_stmts.html#return), [break](https://docs.python.org/3/reference/simple_stmts.html#break) or [continue](https://docs.python.org/3/reference/simple_stmts.html#continue) statement is executed in the [try](https://docs.python.org/3/reference/compound_stmts.html#try) suite of a try…[finally](https://docs.python.org/3/reference/compound_stmts.html#finally) statement, the finally clause is also executed ‘on the way out.’".
So, our implementation of `try` by means of `goto` must catch both exceptions and the `break`. It then must execute `finally` and rethrow everything that was not processed by the `except` clause (including `break`). The `break` then will be caught again up the stack by the implementation of `while`.
Like so:
```
flag.write 5
goto
  [stackUp]
    TRUE.while
      write.
        resultOfTry
        goto
          [stackUp]
            stackUp.forward breakConstant // this is the break
      if.
        is-exception resultOfTry
        // here goes the implementation of except clause, which is BTW empty in our example
        0
      flag.write 10 // here goes the implementation of finally, so it is executed for exceptions and for the break
      if.
        is-break resultOfTry
        stackUp.forward resultOfTry  // redirect the break further up the stack
        0
```

We may also imagine a `return` that happens somewhere deep in the hierarchy of whiles and tries. This is a kind of semi-manual stack unwinding.     
    
Complex, float and int numbers ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are implemented as EO objects, which have a `__class__` field, which is used to get a type of an object. Each arithmetic operation compares types of its operands and performs necessary conversion before evaluating the result.

Each identifier ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is prepended with `x` because a python identifier may start with a capital letter, while one in EO cannot. 
    
Integer, boolean, string and float literals ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are wrapped in respective EO wrapper objects:
* `10` is translated to `(pyint 10)`
* `"Hello, world"` is translated to `(pystring "Hello, world")`

Try to translate `print(10)` or `print("Hello, world")`, for example.

Considering parenthesized forms ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)). Almost every generated EO expression is parenthesized, but these parantheses are not related to the parantheses in the original python expressions.

Displays for lists, sets and dictionaries ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are supported via a python-to-python pass: a display will be converted to a `for` loop. Generator expressions ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are processed the same way as displays.

Yield expression ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is a part of coroutines. We have no plans to support the coroutines right now.

Statically known attributes of python classes ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are translated into attributes of the respective EO objects, so `obj.attr` is translated to `obj.attr`.

Subscriptions, slicing ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are not yet implemented, but should be implemented with calling `.__getitem__` and `.__setitem__` methods of array objects. That is,
* `a[i]` should be translated to something like `(a.__getitem__ i)`
* and `a[i] = x` should be translated to something like `(a.__setitem__ i x)`

See [the section on function definition](https://github.com/polystat/py2eo#86-function-definition) for the examples on how to call ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) a function.

([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is also a part of coroutines, no plans to support it.

Different arithmetics and logic binary and unary operations ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are translated to calls of the respective functions of the EO standard library. Like, `a == b` is translated to `(a.eq b)`, `not x` goes to `(x.not)` and so on.

Assignment expressions ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are not yet supported.

Conditional expressions ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are passed like the following:
`a if c else b` -> `(c).if (a) (b)`

Add enough context when try this, for example:
``` 
a = 7 
b = 6
x = a if a < b else b
print(x)
``` 

An anonymous function/lambda expression ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is extracted to a named function. This is not hard because complex expressions are splitted into simpler as described [here](https://github.com/polystat/py2eo#616-evaluation-order)). 
For example code `f = lambda x: x * 10` is translated to something like:
```
def anonFun0(xx):
  e0 = xx * 10
  return e0
```
Then this python is translated to EO.
    
Add enough context when try this, for example:
```
f = lambda x: x * 10
x = f(11)
print(x)
```

Expression lists ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) are not yet supported. Should be supported by explicitly constructing a tuple out of an expression list. A star sholud be implemented as a function, which unfolds an iterable object.

Evaluation order ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is about lazyness. Python is not lazy and the order of execution of a complex expression is documented. EO is lazy. Thus, each expression must be split into simple pieces and a series of statements must be generated, which force each piece in the correct order. 

For example, this `x = (1 + 2) * f(3 + 4, 5)` is translated to 
```
  (e1).write (((pyint 1).add (pyint 2)))
  (e1).force
  ((e1).<)
  mkCopy (xf) > tmp1
  (lhs0).write (tmp1.copy)
  (e2).write (((pyint 3).add (pyint 4)))
  (e2).force
  ((e2).<)
  (lhs1).write ((pyint 5))
  (lhs1).force
  tmp.write (goto ((((lhs0)).apply ((e2)) ((lhs1))).@))
  (tmp.xclass.xid.neq (return.xclass.xid)).if (stackUp.forward tmp) 0
  (e3).write (tmp.result)
  ((e3).<)
  (e4).write (((e1).mul (e3)))
  (e4).force
  ((e4).<)
  mkCopy (e4) > tmp2
  (xx).write (tmp2.copy)
```

Add enough context when try this, for example:
```
def f(a, b): return a + b
x = (1 + 2) * f(3 + 4, 5)
print(x)
```

Operator precedence feature ([see sec 6](https://docs.python.org/3.8/reference/expressions.html)) is supported by the parser. For example, for an expression `1 + 2 * 3` the parser generates a syntax tree like `Add(1, Mult(2, 3))`, not `Mult(Add(1, 2), 3)`. 
    
Try to translate, for example
```
x = 1 + 2 * 3
print(x)
```

Expressions statements ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) should be easy to get with but are not yet done.

Assignment statements ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) are passed as follows:
* `x` is prepended to each variable name in order to support capital first letter of a name
* local variable names are statically extracted and declared as `cage` in the beginning of a generated output
##### Python
`x = 1`
##### EO
This is added at the start of the generated function
```
cage > xx
```
This is put at the appropriate place according to the execution order
```
(xx).write (1)
```

##### Python
`x = x + 1`
##### EO
Seems overly complicated, but this is a generalized variant of [this hack](https://github.com/objectionary/eo/issues/462#issuecomment-989631295). Possibly the part with `dddata` can be simplified.
```
seq > @
  [] > tmp1
    memory > dddata
    dddata.write (((xx).add 1)) > @
  (e0).write (tmp1.dddata)
  ((e0).<)
  mkCopy (e0) > tmp2
  (xx).write (tmp2.copy)
  123
```
where `mkCopy` looks like this
```
[x] > mkCopy
  x' > copy
  copy.< > @
```
You may try to run this example:
```
x = 1 
x = x + 2 * 3
print(x)
```

Assert ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) will be done as a python-to-python pass, which basically substitutes `assert` to `raise AssertionException`

`Pass` ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is a statement, which does nothing.

`Del` ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is an operator to delete attributes of objects dynamically. Not supported therefore.

Return ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is discussed in function definition below.

Yield ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is a part of coroutines. No plans to support the coroutines now.

Raise ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is described in exceptions section before.

Break and Continue ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is described in while section before.
See [the section on while](https://github.com/polystat/py2eo#82-while).

Import ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) is not yet supported. 

For global and Nonlocal ([see sec 7](https://docs.python.org/3.8/reference/simple_stmts.html)) we need closure for full support. 

If-elif-else ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) passes illustrated below:

> Try adding `print(x)` at the end of examples below to run them).
    
##### Python
```
x = 1
if True:
  x = 2
```

##### EO
```
cage > tmp
cage > xx
seq > @
  (xx).write (1)
  TRUE.if
    seq
      (xx).write (2)
      TRUE
    seq
      TRUE
  123
```

##### Python
```
x = 1
if True:
  x = 2
elif False:
  x = 3
else:
  x = 4
```

##### EO
```
cage > tmp
cage > xx
seq > @
  (xx).write (1)
  TRUE.if
    seq
      (xx).write (2)
      TRUE
    seq
      FALSE.if
        seq
          (xx).write (3)
          TRUE
        seq
          (xx).write (4)
          TRUE
      TRUE
  123
```

While ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) passes illustrated below:
##### Python
Imagine that the following code is a whole body of a function
```
x = 0
while True:
  x = x + 1
```

##### EO
Here, we have to support `break` with the help of `goto`, but also we should be able to pass other non-trivial returns like exceptions and `return` through the `goto` construct. This makes the code more complicated than just a direct use of `while`
First, we declare `x` and some temporary variables
```
[stackUp]
  seq > @
    cage > tmp
    cage > xx
    cage > e0
```
```
    seq > @
      (xx).write (0)
```
Here, we wrap the `while` in a `goto` and store the result
```
      write.
        xcurrent-exception
        goto
          [stackUp]
            seq > @
              TRUE.while
                [unused]
                  seq > @
 ```
 This part is just an overly generalized implementation of `x = x + 1`
 ```
                    [] > tmp1
                      memory > dddata
                      dddata.write (((xx).add 1)) > @
                    (e0).write (tmp1.dddata)
                    ((e0).<)
                    mkCopy (e0) > tmp2
                    (xx).write (tmp2.copy)
                    TRUE
 ```
 Now, if we leave the `while` normally, we must still return something to be written to the `current-exception` variable, which is the `raiseNothing` object
 ```
              stackUp.forward raiseNothing
 ```
 Next, if the `goto` result is not `break`, we basically rethrow it to the outer frame. Note, there are two `stackUp` objects here. The inner one is used to exit the while loop, while the outer one is used to exit the whole code block. It is only needed if the block is a body of another `while` or function or `try` block. 
 ```
       if.
        xcurrent-exception.xclass.xid.neq (break.xclass.xid)
        stackUp.forward xcurrent-exception
        0
```

Try to run this example:
    
```
x = 1
while x < 100:
    x = 2 * x

print(x)
```
 
A `for` ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) loop over an iterator is transformed into a `while` inside a `try`:
##### Python
```
x = 0
for i in r: x = x + i
```
##### Python after `for` elimination
```
x = 0
it0 = r.__iter__()
try:
    while (True):
        i = it0.__next__()
        x = (x + i)
except StopIteration:
    pass
```
The resulting code is then transformed to EO.
              
Try to run this example
```
x = 0
for i in range(4):
    x = x + i

print(x)
```              

Try ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) is passed as follows:

##### Python
```
  def f(a, b):
    try:
      return a
    finally:
      return b
```
##### EO
```
[] > f
  [xaNotCopied xbNotCopied] > apply
    [stackUp] > @
      cage > tmp
      xaNotCopied' > xa
      xbNotCopied' > xb
      seq > @
        stdout "xf\n"
        xa.<
        xb.<
        write.
          xcurrent-exception
          goto
            [stackUp]
              seq > @
                stackUp.forward (return (xa))
                stackUp.forward raiseNothing
        seq
          if.
            xcurrent-exception.xclass.xid.eq (raiseNothing.xclass.xid)
            seq
            0
          stackUp.forward (return (xb))
          (xcurrent-exception.xclass.xid.neq (raiseNothing.xclass.xid)).if (stackUp.forward xcurrent-exception) 0
        123
```

Also, try this example
```
try:
  1 // 0
except ZeroDivisionError:
  print("Hello, world")
``` 
    
With ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) is not yet implemented. The plan is to do it as a python-to-python pass according do the example [here](https://docs.python.org/3.8/reference/compound_stmts.html#the-with-statement)

Function definition ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) is non-trivial part here is to allow a function body to both do a `return` and to throw exceptions, which are not caught inside the function. This necessity forces us to wrap a function body in an object to be called with the help of `goto`. 

Function definition example
##### Python
```
def f(): return 5
```
##### EO
Here, `apply` subobject is used to pass parameters (none in this example), the inner anonymous object with parameter `stackUp` should be called with the `goto`. 

The `stackUp` is used both 
* to throw exceptions 
* to return values normally, in which case they are wrapped into a `return` object, so that they can be differentiated from exceptions
```
[] > f
  [] > apply
    [stackUp] > @
      cage > tmp
      seq > @
        stackUp.forward (return 5)
        123
```

Function call example
##### Python
`x = f()`
##### EO
Here, we first actually call `f`, then we must check if it returned normally or with exception. The exception is rethrown.
The call
```
tmp.write (goto ((((xf)).apply).@))
```
Check for exception
```
(tmp.xclass.xid.neq (return.xclass.xid)).if (stackUp.forward tmp) 0
```
Extract a result from the return object
```
(e0).write (tmp.result)
((e0).<)
```
Assign its copy to `x`. 
```
mkCopy (e0) > tmp1
(xx).write (tmp1.copy)
123

```

Try this example
```
def f(a, b): return a + b
x = (1 + 2) * f(3 + 4, 5)
print(x)
```
    
A class ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)) is basically its constructor, i.e., a function, which returns an object. 
##### Python
```
class c:
  field = 1
o = c()
o.field = 2
```
##### EO
Here `c` has the same calling convention as other functions (it has a field `apply` and must be called with `goto`). The inner `result` is the object to be returned. It is returned through a cage, otherwise this code just hangs.
```
[] > c
  newUID.apply 0 > xid
  [] > apply
    [stackUp] > @
      cage > pResult
      [] > result
        cage > xfield
        xc > xclass
        seq > initFields
          xfield.write 1
      seq (result.initFields) (pResult.write result) (stackUp.forward (return pResult)) > @
```
This is how object `o` is created:
```
tmp.write (goto ((((xc)).apply).@))
(tmp.xclass.xid.neq (return.xclass.xid)).if (stackUp.forward tmp) 0
(e0).write (tmp.result)
((e0).<)
mkCopy (e0) > tmp1
(xo).write (tmp1.copy)
```
Field assignment is then straightforward:
```
((xo).xfield).write (2)
```

Try this example
```
class c:
  field = 1
o = c()
o.field = 2

x = o.field

print(x)
```
        
No plans to support coroutines ([see sec 8](https://docs.python.org/3.8/reference/compound_stmts.html)).
