from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e2 = xmultibytecodec_support.xTestBase # 9:21-9:51
e3 = xunittest.xTestCase # 9:54-9:70
class xTest_Big5HKSCS(e2, e3): # 9:73-21:-1
    xencoding = "big5hkscs" # 10:4-10:25
    e0 = xmultibytecodec_support.xload_teststring # 11:14-11:51
    lhs0 = "big5hkscs" # 11:53-11:63
    e1 = e0(lhs0) # 11:14-11:64
    xtstring = e1 # 11:4-11:64
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffdu8b10"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffdu8b10ufffd"), ("abcx80x80xc1xc4", "ignore", "abcu8b10")) # 12:4-19:4
e6 = (x__name__ == "__main__") # 21:3-21:24
if (e6): # 21:26-23:-1 
    e4 = xunittest.xmain # 22:4-22:16
    e5 = e4() # 22:4-22:18
else: # 21:0-23:-1
    pass # 21:0-23:-1