import builtins as builtins
from builtins import *
import sys as sys
def testFib1():
    f0 = 0
    f1 = 1
    ii = 0
    while ((ii < 10)):
        f2 = (f0 + f1)
        ii = (ii + 1)
        f0 = f1
        f1 = f2
        if ((ii == 9)):
            break
        else:
            pass
    else:
        pass
    print(f0)
    return (34 == f0)