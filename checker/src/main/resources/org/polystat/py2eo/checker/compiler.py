import py_compile
import sys

ret = py_compile.compile("test.py", cfile="output.pyc")
if ret is None:
    sys.exit(1)
