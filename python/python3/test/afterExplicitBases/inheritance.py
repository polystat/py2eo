import builtins as builtins
from builtins import *
import sys as sys
class A():
    eo_bases = [object,]
    x = 5
class B():
    eo_bases = [A,]
    pass
print(B.x)
print(getattr(B, "x"))
print(setattr(B, "x", 7))