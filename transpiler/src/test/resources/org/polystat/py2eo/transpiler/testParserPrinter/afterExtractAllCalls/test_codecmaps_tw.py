from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e0 = xmultibytecodec_support.xTestBase_Mapping # 9:18-9:56
e1 = xunittest.xTestCase # 10:18-10:34
class xTestBIG5Map(e0, e1): # 10:37-14:-1
    xencoding = "big5" # 11:4-11:20
    xmapfileurl = "http://www.pythontest.net/unicode/BIG5.TXT" # 12:4-12:60
e2 = xmultibytecodec_support.xTestBase_Mapping # 14:19-14:57
e3 = xunittest.xTestCase # 15:19-15:35
class xTestCP950Map(e2, e3): # 15:38-26:-1
    xencoding = "cp950" # 16:4-16:21
    xmapfileurl = "http://www.pythontest.net/unicode/CP950.TXT" # 17:4-17:61
    xpass_enctest = [("xa2xcc", "u5341"), ("xa2xce", "u5345")] # 18:4-21:4
    xcodectests = (("xFFxy", "replace", "ufffdxy"),) # 22:4-24:4
e6 = (x__name__ == "__main__") # 26:3-26:24
if (e6): # 26:26-28:-1 
    e4 = xunittest.xmain # 27:4-27:16
    e5 = e4() # 27:4-27:18
else: # 26:0-28:-1
    pass # 26:0-28:-1