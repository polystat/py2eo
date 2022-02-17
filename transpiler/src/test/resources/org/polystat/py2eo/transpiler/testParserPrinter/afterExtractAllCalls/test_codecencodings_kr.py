from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e2 = xmultibytecodec_support.xTestBase # 9:17-9:47
e3 = xunittest.xTestCase # 9:50-9:66
class xTest_CP949(e2, e3): # 9:69-21:-1
    xencoding = "cp949" # 10:4-10:21
    e0 = xmultibytecodec_support.xload_teststring # 11:14-11:51
    lhs0 = "cp949" # 11:53-11:59
    e1 = e0(lhs0) # 11:14-11:60
    xtstring = e1 # 11:4-11:60
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffduc894"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffduc894ufffd"), ("abcx80x80xc1xc4", "ignore", "abcuc894")) # 12:4-19:4
e6 = xmultibytecodec_support.xTestBase # 21:17-21:47
e7 = xunittest.xTestCase # 21:50-21:66
class xTest_EUCKR(e6, e7): # 21:69-52:-1
    xencoding = "euc_kr" # 22:4-22:22
    e4 = xmultibytecodec_support.xload_teststring # 23:14-23:51
    lhs2 = "euc_kr" # 23:53-23:60
    e5 = e4(lhs2) # 23:14-23:61
    xtstring = e5 # 23:4-23:61
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffduc894"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffduc894ufffd"), ("abcx80x80xc1xc4", "ignore", "abcuc894"), ("xa4xd4", "strict", None), ("xa4xd4xa4", "strict", None), ("xa4xd4xa4xb6", "strict", None), ("xa4xd4xa4xb6xa4", "strict", None), ("xa4xd4xa4xb6xa4xd0", "strict", None), ("xa4xd4xa4xb6xa4xd0xa4", "strict", None), ("xa4xd4xa4xb6xa4xd0xa4xd4", "strict", "uc4d4"), ("xa4xd4xa4xb6xa4xd0xa4xd4x", "strict", "uc4d4x"), ("axa4xd4xa4xb6xa4", "replace", "aufffd"), ("xa4xd4xa3xb6xa4xd0xa4xd4", "strict", None), ("xa4xd4xa4xb6xa3xd0xa4xd4", "strict", None), ("xa4xd4xa4xb6xa4xd0xa3xd4", "strict", None), ("xa4xd4xa4xffxa4xd0xa4xd4", "replace", "ufffdu6e21ufffdu3160ufffd"), ("xa4xd4xa4xb6xa4xffxa4xd4", "replace", "ufffdu6e21ub544ufffdufffd"), ("xa4xd4xa4xb6xa4xd0xa4xff", "replace", "ufffdu6e21ub544u572dufffd"), ("xa4xd4xffxa4xd4xa4xb6xa4xd0xa4xd4", "replace", "ufffdufffdufffduc4d4"), ("xc1xc4", "strict", "uc894")) # 24:4-50:4
e10 = xmultibytecodec_support.xTestBase # 52:17-52:47
e11 = xunittest.xTestCase # 52:50-52:66
class xTest_JOHAB(e10, e11): # 52:69-68:-1
    xencoding = "johab" # 53:4-53:21
    e8 = xmultibytecodec_support.xload_teststring # 54:14-54:51
    lhs4 = "johab" # 54:53-54:59
    e9 = e8(lhs4) # 54:14-54:60
    xtstring = e9 # 54:4-54:60
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffducd27"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffducd27ufffd"), ("abcx80x80xc1xc4", "ignore", "abcucd27"), ("xD8abc", "replace", "uFFFDabc"), ("xD8xFFabc", "replace", "uFFFDuFFFDabc"), ("x84bxy", "replace", "uFFFDbxy"), ("x8CBxy", "replace", "uFFFDBxy")) # 55:4-66:4
e14 = (x__name__ == "__main__") # 68:3-68:24
if (e14): # 68:26-70:-1 
    e12 = xunittest.xmain # 69:4-69:16
    e13 = e12() # 69:4-69:18
else: # 68:0-70:-1
    pass # 68:0-70:-1