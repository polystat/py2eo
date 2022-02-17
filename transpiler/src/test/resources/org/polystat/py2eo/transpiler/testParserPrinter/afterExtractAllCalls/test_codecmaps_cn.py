from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e0 = xmultibytecodec_support.xTestBase_Mapping # 9:20-9:58
e1 = xunittest.xTestCase # 10:19-10:35
class xTestGB2312Map(e0, e1): # 10:38-14:-1
    xencoding = "gb2312" # 11:4-11:22
    xmapfileurl = "http://www.pythontest.net/unicode/EUC-CN.TXT" # 12:4-12:62
e2 = xmultibytecodec_support.xTestBase_Mapping # 14:17-14:55
e3 = xunittest.xTestCase # 15:19-15:35
class xTestGBKMap(e2, e3): # 15:38-19:-1
    xencoding = "gbk" # 16:4-16:19
    xmapfileurl = "http://www.pythontest.net/unicode/CP936.TXT" # 17:4-17:61
e4 = xmultibytecodec_support.xTestBase_Mapping # 19:21-19:59
e5 = xunittest.xTestCase # 20:21-20:37
class xTestGB18030Map(e4, e5): # 20:40-25:-1
    xencoding = "gb18030" # 21:4-21:23
    xmapfileurl = "http://www.pythontest.net/unicode/gb-18030-2000.xml" # 22:4-22:69
e8 = (x__name__ == "__main__") # 25:3-25:24
if (e8): # 25:26-27:-1 
    e6 = xunittest.xmain # 26:4-26:16
    e7 = e6() # 26:4-26:18
else: # 25:0-27:-1
    pass # 25:0-27:-1