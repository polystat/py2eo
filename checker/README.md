This checker is a mutation based tool for verifing the sensitivity of the transpiler to input mutations. It takes an input file, transforms it using one of the pre-made local mutations and checks if the result of transpilation, compilation and running is expected.
For example, for a mutation that breaks the syntax of the input language, the expected result is a failed transpilation step. Test with a mutated literal value is expected to return the wrong result, but the transpilation and compilation stages should pass.

The checker is included in the CI for this repository, resulting in an HTML-table with tests as rows and mutation as columns. In each cell there is one of the following results:
- n/a for tests whose content was not changed by the mutation
- transpiled for tests that passed the transpilation step but could not be compiled into EO
- compiled for tests that passed the EO compilation but returned an incorrect result when run
- passed for tests that returned correct result

The resulting table for simple tests lies on github pages: https://polystat.github.io/py2eo.  
The resulting table for django source code lies in [this branch](https://github.com/polystat/py2eo/tree/django-results). In order to view it, clone this branch and open `index.html` file in any browser.
