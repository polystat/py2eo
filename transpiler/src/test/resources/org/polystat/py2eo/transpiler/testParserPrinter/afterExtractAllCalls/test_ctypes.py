import xunittest # 1:7-1:14
from xtest.xsupport import ximport_module # 2:25-2:37
e0 = ximport_module("ctypes.test") # 4:14-4:41
xctypes_test = e0 # 4:0-4:41
e1 = xctypes_test.xload_tests # 6:13-6:34
xload_tests = e1 # 6:0-6:34
e4 = (x__name__ == "__main__") # 8:3-8:24
if (e4): # 8:26-10:-1 
    e2 = xunittest.xmain # 9:4-9:16
    e3 = e2() # 9:4-9:18
else: # 8:0-10:-1
    pass # 8:0-10:-1