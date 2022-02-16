from x__future__ import xnested_scopes # 1:23-1:35
from x__future__ import xdivision # 2:23-2:30
import xunittest # 4:7-4:14
xx = 2  # 6:0-6:4
def xnester(): # 7:0-14:-1
    xx = 3  # 8:4-8:8
    def xinner(): # 9:4-11:3
        return xx # 10:8-10:15
    e0 = xinner() # 11:11-11:17
    return e0 # 11:11-11:17
e10 = xunittest.xTestCase # 14:17-14:33
class xTestFuture(e10): # 15:0-25:-1
    def xtest_floor_div_operator(xself): # 16:4-19:3
        e1 = xself.xassertEqual # 17:8-17:23
        e2 = (7  // 2 ) # 17:25-17:30
        lhs0 = 3  # 17:33-17:33
        e3 = e1(e2, lhs0) # 17:8-17:34
    def xtest_true_div_as_default(xself): # 19:4-22:3
        e4 = xself.xassertAlmostEqual # 20:8-20:29
        e5 = (7  / 2 ) # 20:31-20:35
        lhs2 = 3.5 # 20:38-20:40
        e6 = e4(e5, lhs2) # 20:8-20:41
    def xtest_nested_scopes(xself): # 22:4-25:-1
        e7 = xself.xassertEqual # 23:8-23:23
        e8 = xnester() # 23:25-23:32
        lhs4 = 3  # 23:35-23:35
        e9 = e7(e8, lhs4) # 23:8-23:36
e13 = (x__name__ == "__main__") # 25:3-25:24
if (e13): # 25:26-27:-1 
    e11 = xunittest.xmain # 26:4-26:16
    e12 = e11() # 26:4-26:18
else: # 25:0-27:-1
    pass # 25:0-27:-1