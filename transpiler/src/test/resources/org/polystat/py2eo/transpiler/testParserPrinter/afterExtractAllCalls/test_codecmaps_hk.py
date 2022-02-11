from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e0 = xmultibytecodec_support.xTestBase_Mapping # 9:23-9:61
e1 = xunittest.xTestCase # 10:23-10:39
class xTestBig5HKSCSMap(e0, e1): # 10:42-14:-1
    xencoding = "big5hkscs" # 11:4-11:25
    xmapfileurl = "http://www.pythontest.net/unicode/BIG5HKSCS-2004.TXT" # 12:4-12:70
e4 = (x__name__ == "__main__") # 14:3-14:24
if (e4): # 14:26-16:-1 
    e2 = xunittest.xmain # 15:4-15:16
    e3 = e2() # 15:4-15:18
else: # 14:0-16:-1
    pass # 14:0-16:-1