import xunittest # 1:7-1:14
e7 = xunittest.xTestCase # 3:18-3:34
class xLongExpText(e7): # 3:37-9:-1
    def xtest_longexp(xself): # 4:4-9:-1
        xREPS = 65580  # 5:8-5:19
        lhs4 = xeval # 6:12-6:15
        lhs0 = "[" # 6:17-6:19
        e0 = ("2," * xREPS) # 6:23-6:33
        e1 = (lhs0 + e0) # 6:17-6:33
        lhs2 = "]" # 6:37-6:39
        e2 = (e1 + lhs2) # 6:17-6:39
        e3 = lhs4(e2) # 6:12-6:40
        xl = e3 # 6:8-6:40
        e4 = xself.xassertEqual # 7:8-7:23
        e5 = xlen(xl) # 7:25-7:30
        lhs6 = xREPS # 7:33-7:36
        e6 = e4(e5, lhs6) # 7:8-7:37
e10 = (x__name__ == "__main__") # 9:3-9:24
if (e10): # 9:26-11:-1 
    e8 = xunittest.xmain # 10:4-10:16
    e9 = e8() # 10:4-10:18
else: # 9:0-11:-1
    pass # 9:0-11:-1