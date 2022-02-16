from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e0 = xmultibytecodec_support.xTestBase_Mapping # 9:19-9:57
e1 = xunittest.xTestCase # 10:19-10:35
class xTestCP949Map(e0, e1): # 10:38-15:-1
    xencoding = "cp949" # 11:4-11:21
    xmapfileurl = "http://www.pythontest.net/unicode/CP949.TXT" # 12:4-12:61
e2 = xmultibytecodec_support.xTestBase_Mapping # 15:19-15:57
e3 = xunittest.xTestCase # 16:19-16:35
class xTestEUCKRMap(e2, e3): # 16:38-25:-1
    xencoding = "euc_kr" # 17:4-17:22
    xmapfileurl = "http://www.pythontest.net/unicode/EUC-KR.TXT" # 18:4-18:62
    xpass_enctest = [("xa4xd4", "u3164"),] # 21:4-21:43
    xpass_dectest = [("xa4xd4", "u3164"),] # 22:4-22:43
e4 = xmultibytecodec_support.xTestBase_Mapping # 25:19-25:57
e5 = xunittest.xTestCase # 26:19-26:35
class xTestJOHABMap(e4, e5): # 26:38-36:-1
    xencoding = "johab" # 27:4-27:21
    xmapfileurl = "http://www.pythontest.net/unicode/JOHAB.TXT" # 28:4-28:61
    xpass_enctest = [("\\", "u20a9"),] # 33:4-33:37
    xpass_dectest = [("\\", "u20a9"),] # 34:4-34:37
e8 = (x__name__ == "__main__") # 36:3-36:24
if (e8): # 36:26-38:-1 
    e6 = xunittest.xmain # 37:4-37:16
    e7 = e6() # 37:4-37:18
else: # 36:0-38:-1
    pass # 36:0-38:-1