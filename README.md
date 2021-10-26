
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

This is a translator of Python to [EOLANG](https://www.eolang.org).

Currently it is a clone of the grammars-v4 ANTLR repo with the initial version of py2eo translator in the module python/python3.
All other modules are not needed and should be removed later.  

The python3 module is buildable in IDEA. My build configuration is: ubuntu 20.04 (important!), scala-2.13.6 (important!), jdk 1.8, python3 for tests. 

2 auto tests are available:
* AST.RemoveControlFlowTest checks that trivial.py, which calculates a fibonacci number, works after certain translator passes
* AST.PrintEOTest, which just diffs a generated EO with a "known good" variant

Run them in the project root as a current directory. Create these directories: 
`mkdir afterParser afterEmptyProcStatement afterSimplifyIf afterRemoveControlFlow`
before running the tests for the first time. 


