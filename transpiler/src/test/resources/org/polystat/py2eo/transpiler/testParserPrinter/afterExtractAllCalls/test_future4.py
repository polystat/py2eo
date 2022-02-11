from x__future__ import xunicode_literals # 1:23-1:38
import xunittest # 2:7-2:14
e2 = xunittest.xTestCase # 5:12-5:28
class xTests(e2): # 5:31-10:-1
    def xtest_unicode_literals(xself): # 6:4-10:-1
        e0 = xself.xassertIsInstance # 7:8-7:28
        lhs0 = "literal" # 7:30-7:38
        lhs1 = xstr # 7:41-7:43
        e1 = e0(lhs0, lhs1) # 7:8-7:44
e5 = (x__name__ == "__main__") # 10:3-10:24
if (e5): # 10:26-12:-1 
    e3 = xunittest.xmain # 11:4-11:16
    e4 = e3() # 11:4-11:18
else: # 10:0-12:-1
    pass # 10:0-12:-1