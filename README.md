
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" /> 

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)
[![Hits-of-Code](https://hitsofcode.com/github/polystat/py2eo)](https://hitsofcode.com/view/github/polystat/py2eo)
![Lines of code](https://img.shields.io/tokei/lines/github/polystat/py2eo)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/polystat/py2eo)](http://www.rultor.com/p/polystat/py2eo)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

#### What is Py2EO? ####
This is a transpiler of [Python](https://www.python.org/) to [EOLANG](https://www.eolang.org). It is translates `Python` code to `EOLANG` programming language.

This transpiler receives python code as input data. Then received code is simplified with AST usage. After successfull simplyfying it is sent to the `py2eo` translator for getting `EOLANG`.

`EOLANG` code [can be translated to java and executed](https://github.com/objectionary/eo#quick-start) or analyzed via [Polystat analyzer](https://github.com/polystat/polystat).

> Traspiler is a source-to-source translator, source-to-source compiler (S2S compiler), transcompiler, or transpiler is a type of translator that takes the source code of a program written in a programming language as its input and produces an equivalent source code in the same or a different programming language

#### How to transpile Py to EO? ####
* Tested on `Ubuntu` (20.04+) and `Windows` (7+), but instuctions is for `Ubuntu`
* Install maven (`sudo apt install maven`) - it also installs default JDK (version 11 for now)
* Install `Java` (JDK or JRE) version 14 (yes, exactly 14). For example you can [download it here](https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz) and unpack it:
```
cd ~
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
tar x -z < openjdk-14.0.1_linux-x64_bin.tar.gz
```
* You can either use [released transpiler executables](https://repo1.maven.org/maven2/org/polystat/py2eo/transpiler/0.0.11.3/transpiler-0.0.11.3-jar-with-dependencies.jar) or build it on your own: 
* Obtain [Py2EO master branch sources](https://github.com/polystat/py2eo) via `git clone https://github.com/polystat/py2eo.git` ('sudo apt install git'), or download [`zipped artifacts`](https://github.com/polystat/py2eo/archive/refs/heads/master.zip)
* Setup the `PATH` and `JAVA_HOME` variables, for example:
    ```
    PATH="$PWD/jdk-14.0.1/bin/:$PATH"
    export JAVA_HOME="$PWD/jdk-14.0.1/"
    ```
* Check (e. g. via `java -version`) that version `14.*` is used
* Go to Py2RO root and run `mvn clean package -DskipTests=true` in the same command line runtime were you have set `PATH` and `JAVA_HOME` variables, if succeeded you will get `transpiler/target/transpiler-${version_code}-SNAPSHOT-jar-with-dependencies.jar`
* Create test file with `python` code (e.g. `sample_test.py` in Py2Ro root), for example with these contents:
    ```
    def conditionalCheck2():
        a = 4
        b = 2
    ```
* Run `java -jar .\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar <path/to/python/file>`
- Check output .eo file in the directory with python code with the same name
- Try using `-o` argument to specify output path and/or name if needed
- Follow instructions from [eolang repository](https://github.com/objectionary/eo#quick-start) on how to run the resulting eo code.

Additional arguments:

| Option         | Action                         |
|----------------|--------------------------------|
| `-h,--help`    | Display available options      |
| `-o <file>`    | Write output to <file>         |
| `-X,--debug`   | Produce execution debug output |
| `-v,--version` | Print version information      |

#### How to contribute? ####


* For contributors: `Maven 3.6.3` with `Java 14` or `Maven 3.8.4` with `Java 17` (but there is no `Maven 3.8` package in `Ubuntu` and no `Java 14` package, so manual installation is needed anyway)
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

## Test coverage
At the moment, we have 3 different gorups of tests for Py2EO transpiler (checker with mutations are out of scope here):

- [CPython](https://github.com/python/cpython/tree/3.8/Lib/test), Python language implementation tests, version `3.8`)
For all tests (250,000+ lines of Python code), `EO` is generated and passes `EO` syntax check stage. Subsequent `Java` generation (and, therefore, `Java` compilation and execution), comes to `Python` runtime transpilation issue (link?). Java generation will take about a week of total runtime as estimated. Got plans to come back to issue after majority of functional "simple" tests will pass.
  
  To run please install python 3.8.4 or newer, any gcc, then use this from the repository root:
  `mvn clean verify -B -Pcpython`

- [Django](https://github.com/django/django), a popular `Python` web framework
For all `.py` files (every `.py` is considered as particular test) from Django repository (440,000+ lines of Python code) `EO` is generated and passes `EO` syntax check stage. Yet not tried to generate Java (estimates about a week of total runtime) for this group, since сompiling and execution of Java code obtained this way seems to be pointless.
  
  To run use this from the repository root:
  `mvn clean verify -B -Pdjango`

- [Handwritten tests](https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler), set of tests divided into groups by type: functional (also divided into groups by constructs in accordance with the language specification), integration tests (tests for the polystat analyzer), "negative" tests, etc.
[Functional tests](https://github.com/polystat/py2eo/tree/master/transpiler/src/test/resources/org/polystat/py2eo/transpiler/simple-tests), 1600+ lines of code. A detailed description of the particular tests is given [on a separate wiki page](https://github.com/polystat/py2eo/wiki/Tests-Structure). All these tests go through a full cycle of stages: from generating EO to executing Java. Progress is shown in each release description.
  
  To run use this from the repository root:
  `mvn clean verify`
  
  Resulting eo-files are located in `py2eo/transpiler/src/test/resources/org/polystat/py2eo/transpiler/results`. 
  - Copy it to the runEO directory:
    `cp transpiler/src/test/resources/org/polystat/py2eo/transpiler/results/*.eo ./runEO`
  - Copy the preface lib:
    `cp -a transpiler/src/main/eo/preface ./runEO`
  - Run EO compiler 
     `cd ./runEO && mvn clean test`

## Examples of translation projections

## 2.1 Comments, Identation, Explicit and Implicit line joining, Whitespace between tokens
These things are supported by the parser. No additional support is needed, because these are just pecularities of the syntax. 

## 4. Execution model
### 4.2.1 4.2.2 Names
Dynamically adding/removing names is not supported. All the statically known names are implemented as a `cage` object of EO. This allows to implement assignments. EO objects are also visibility scopes for identifiers, so several variables with the same name in different scopes are implemented directly. 
    
### 4.3 Exceptions

Exceptions, `break`, `continue`, `return` are currently all implemented with the help of the `goto` object.   
    
Consider this python code
```
while True: break
```
We can translate it this way
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

We may also imagine a `return` that happens somewhere deep in the hierarchy of whiles and trys. This is a kind of semi-manual stack unwinding, IMHO.     
    
    
## 6. Expressions

### 6.1 Arithmetic conversion
Complex, float and int numbers are implemented as EO objects, which have a `__class__` field, which is used to get a type of an object. Each arithmetic operation compares types of its operands and performs necessary conversion before evaluating the result.

### 6.2.1 Identifiers
Each identifier is prepended with `x` because a python identifier may start with a capital letter, while one in EO cannot. 

### 6.2.2 Literals
Integer, boolean, string and float literals are wrapped in respective EO wrapper objects:
* `10` is translated to `(pyint 10)`
* `"Hello, world"` is translated to `(pystring "Hello, world")`

Try to translate `print(10)` or `print("Hello, world")`, for example.

### 6.2.3 Parenthesized forms
Almost every generated EO expression is parenthesized, but these parantheses are not related to the parantheses in the original python expressions.

### 6.2.4 6.2.5 6.2.6 6.2.7 Displays for lists, sets and dictionaries
Will be supported via a python-to-python pass: a display will be converted to a `for` loop

### 6.2.8 Generator expressions
Same as displays

### 6.2.9 Yield expression
Is a part of coroutines. No plans to support the coroutines now.

### 6.3.1 Attribute references
Statically known attributes of python classes are translated into attributes of the respective EO objects, so `obj.attr` is translated to `obj.attr`.

### 6.3.2-3 Subscriptions, slicing
This is not yet implemented, but should be implemented with calling `.__getitem__` and `.__setitem__` methods of array objects. That is,
* `a[i]` should be translated to something like `(a.__getitem__ i)`
* and `a[i] = x` should be translated to something like `(a.__setitem__ i x)`

### 6.3.4 Call
See [the section on function definition](https://github.com/polystat/py2eo#86-function-definition) for the examples on how to call a function.

### 6.4 Await
Is a part of coroutines, no plans to support it.

### 6.5-11 Different arithmetics and logic binary and unary operations
Are translated to calls of the respective functions of the EO standard library. Like, `a == b` is translated to `(a.eq b)`, `not x` goes to `(x.not)` and so on.

### 6.12 Assignment expressions
Not yet supported.

### 6.13 Conditional expressions
`a if c else b` -> `(c).if (a) (b)`

Please, add enough context if you want to try this with our transpiler, for example:
``` 
a = 7 
b = 6
x = a if a < b else b
print(x)
``` 

### 6.14 lambda
An anonymous function is extracted to a named function (this is not hard because complex expressions are splitted into simpler as described [here](https://github.com/polystat/py2eo#616-evaluation-order)). 
For example code `f = lambda x: x * 10` is translated to something like
```
def anonFun0(xx):
  e0 = xx * 10
  return e0
```
Then this python is translated to EO.
    
Please, add enough context if you want to try this with our transpiler, for example:
```
f = lambda x: x * 10
x = f(11)
print(x)
```

### 6.15 Expression lists
Not yet supported. Should be supported by explicitly constructing a tuple out of an expression list. A star sholud be implemented as a function, which unfolds an iterable object.

### 6.16 Evaluation order
Python is not lazy and the order of execution of a complex expression is documented. EO is lazy. Thus, each expression must be split into simple pieces and a series of statements must be generated, which force each piece in the correct order. 

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

Please, add enough context if you want to try this with our transpiler, for example:
```
def f(a, b): return a + b
x = (1 + 2) * f(3 + 4, 5)
print(x)
```

### 6.17 Operator precedence
This feature is supported by the parser. For example, for an expression `1 + 2 * 3` the parser generates a syntax tree like `Add(1, Mult(2, 3))`, not `Mult(Add(1, 2), 3)`. 
    
Try to translate, for example
```
x = 1 + 2 * 3
print(x)
```

### 7.1 Expressions statements
Should be easy but not yet done.

### 7.2 Assignment statements
Note:
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
Seems overly complicated, but this is a generalized variant of [this](https://github.com/objectionary/eo/issues/462#issuecomment-989631295) hack.
Maybe the part with `dddata` can be simplified.
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

### 7.3 Assert
Will be done as a python-to-python pass, which basically substitutes `assert` to `raise AssertionException`

### 7.4 Pass
`pass` is a statement, which does nothing

### 7.5 Del
Is an operator to delete attributes of objects dynamically. Not supported therefore.

### 7.6 Return
See [the section on function definition](https://github.com/polystat/py2eo#86-function-definition)

### 7.7 Yield
Is a part of coroutines. No plans to support the coroutines now.

### 7.8 Raise
See [the section on exceptions](https://github.com/polystat/py2eo#84-try)

### 7.9 Break
See [the section on while](https://github.com/polystat/py2eo#82-while)

### 7.10 Continue
See [the section on while](https://github.com/polystat/py2eo#82-while)

### 7.11 Import
Not yet supported. 

### 7.12 Global
Later. Needs closure for full support. 

### 7.13 Nonlocal 
Later. Needs closure for full support. 

### 8.1 If-elif-else

(Try adding `print(x)` at the end of examples below to run them).
    
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
##### Tests
https://github.com/polystat/py2eo/wiki/Tests-Structure#the-if-statement

### 8.2 While
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

#### Try to run this example
```
x = 1
while x < 100:
    x = 2 * x

print(x)
```
 
### 8.3 For
A `for` loop over an iterator is transformed into a `while` inside a `try`:
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
              
#### Try to run this example
```
x = 0
for i in range(4):
    x = x + i

print(x)
```              

### 8.4 Try
#### try_2
![image](https://user-images.githubusercontent.com/5425660/166906535-171657ff-b0fd-4843-b668-39d7cf56ff79.png)
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
##### Tests
https://github.com/polystat/py2eo/wiki/Tests-Structure#the-try-statement

#### Try this example
```
try:
  1 // 0
except ZeroDivisionError:
  print("Hello, world")
``` 
    
### 8.5 With
Not yet implemented. The plan is to do it as a python-to-python pass according do the example [here](https://docs.python.org/3.8/reference/compound_stmts.html#the-with-statement)

### 8.6 Function definition
The non-trivial part here is to allow a function body to both do a `return` and to throw exceptions, which are not caught inside the function. This necessity forces us to wrap a function body in an object to be called with the help of `goto`. 

#### Function definition example
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

#### Function call example
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

#### Try this example
```
def f(a, b): return a + b
x = (1 + 2) * f(3 + 4, 5)
print(x)
```

### 8.7 Class
A class is basically its constructor, i.e., a function, which returns an object. 
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

#### Try this example
```
class c:
  field = 1
o = c()
o.field = 2

x = o.field

print(x)
```
        
### 8.8 Coroutines
No plans to support this. 
    
### Print
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
## How to run the translator on the Django project
We checked that it is possible to translate all the python files of the Django project to EO. It is not possible to run the generated EO, because parts of python are not supported. But the generated EO is always syntatically correct (though it uses the `unsupported` EO object instead of some not supported python constructs). 
In order to generate the EO and run the syntax check one may checkout this project and run `mvn verify -Pdjango`. This command runs 2 tests: one downloads sources of Django version 4.0, the other one runs the EO compiler partially (without the "transpile" stage) to check syntax. Both passes run many threads in parallel. The first one takes ~10minutes on Ryzen 3800x with 32gb of RAM, the second one takes ~3 hours.
 
