from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e2 = xmultibytecodec_support.xTestBase # 9:17-9:47
e3 = xunittest.xTestCase # 9:50-9:66
class xTest_CP932(e2, e3): # 9:69-27:-1
    xencoding = "cp932" # 10:4-10:21
    e0 = xmultibytecodec_support.xload_teststring # 11:14-11:51
    lhs0 = "shift_jis" # 11:53-11:63
    e1 = e0(lhs0) # 11:14-11:64
    xtstring = e1 # 11:4-11:64
    xcodectests = (("abcx81x00x81x00x82x84", "strict", None), ("abcxf8", "strict", None), ("abcx81x00x82x84", "replace", "abcufffdx00uff44"), ("abcx81x00x82x84x88", "replace", "abcufffdx00uff44ufffd"), ("abcx81x00x82x84", "ignore", "abcx00uff44"), ("abxEBxy", "replace", "abuFFFDxy"), ("abxF0x39xy", "replace", "abuFFFD9xy"), ("abxEAxF0xy", "replace", "abufffdue038y"), ("\\x7e", "replace", "\\x7e"), ("x81x5fx81x61x81x7c", "replace", "uff3cu2225uff0d")) # 12:4-25:4
xeuc_commontests = (("abcx80x80xc1xc4", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffdu7956"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffdu7956ufffd"), ("abcx80x80xc1xc4", "ignore", "abcu7956"), ("abcxc8", "strict", None), ("abcx8fx83x83", "replace", "abcufffdufffdufffd"), ("x82xFCxy", "replace", "ufffdufffdxy"), ("xc1x64", "strict", None), ("xa1xc0", "strict", "uff3c"), ("xa1xc0\\", "strict", "uff3c\\"), ("x8eXY", "replace", "ufffdXY")) # 27:0-40:0
e6 = xmultibytecodec_support.xTestBase # 42:24-42:54
e7 = xunittest.xTestCase # 43:24-43:40
class xTest_EUC_JIS_2004(e6, e7): # 43:43-52:-1
    xencoding = "euc_jis_2004" # 44:4-44:28
    e4 = xmultibytecodec_support.xload_teststring # 45:14-45:51
    lhs2 = "euc_jisx0213" # 45:53-45:66
    e5 = e4(lhs2) # 45:14-45:67
    xtstring = e5 # 45:4-45:67
    xcodectests = xeuc_commontests # 46:4-46:31
    xxmlcharnametest = ("xabu211cxbb = u2329u1234u232a", "xa9xa8&real;xa9xb2 = &lang;&#4660;&rang;") # 47:4-50:4
e10 = xmultibytecodec_support.xTestBase # 52:24-52:54
e11 = xunittest.xTestCase # 53:24-53:40
class xTest_EUC_JISX0213(e10, e11): # 53:43-62:-1
    xencoding = "euc_jisx0213" # 54:4-54:28
    e8 = xmultibytecodec_support.xload_teststring # 55:14-55:51
    lhs4 = "euc_jisx0213" # 55:53-55:66
    e9 = e8(lhs4) # 55:14-55:67
    xtstring = e9 # 55:4-55:67
    xcodectests = xeuc_commontests # 56:4-56:31
    xxmlcharnametest = ("xabu211cxbb = u2329u1234u232a", "xa9xa8&real;xa9xb2 = &lang;&#4660;&rang;") # 57:4-60:4
e15 = xmultibytecodec_support.xTestBase # 62:25-62:55
e16 = xunittest.xTestCase # 63:25-63:41
class xTest_EUC_JP_COMPAT(e15, e16): # 63:44-71:-1
    xencoding = "euc_jp" # 64:4-64:22
    e12 = xmultibytecodec_support.xload_teststring # 65:14-65:51
    lhs6 = "euc_jp" # 65:53-65:60
    e13 = e12(lhs6) # 65:14-65:61
    xtstring = e13 # 65:4-65:61
    e14 = (xeuc_commontests + (("xa5", "strict", "x5c"), ("u203e", "strict", "x7e"))) # 66:17-69:4
    xcodectests = e14 # 66:4-69:4
xshiftjis_commonenctests = (("abcx80x80x82x84", "strict", None), ("abcxf8", "strict", None), ("abcx80x80x82x84def", "ignore", "abcuff44def")) # 71:0-75:0
e20 = xmultibytecodec_support.xTestBase # 77:23-77:53
e21 = xunittest.xTestCase # 77:56-77:72
class xTest_SJIS_COMPAT(e20, e21): # 77:75-91:-1
    xencoding = "shift_jis" # 78:4-78:25
    e17 = xmultibytecodec_support.xload_teststring # 79:14-79:51
    lhs8 = "shift_jis" # 79:53-79:63
    e18 = e17(lhs8) # 79:14-79:64
    xtstring = e18 # 79:4-79:64
    e19 = (xshiftjis_commonenctests + (("abcx80x80x82x84", "replace", "abcufffdufffduff44"), ("abcx80x80x82x84x88", "replace", "abcufffdufffduff44ufffd"), ("\\x7e", "strict", "\\x7e"), ("x81x5fx81x61x81x7c", "strict", "uff3cu2016u2212"), ("abcx81x39", "replace", "abcufffd9"), ("abcxEAxFC", "replace", "abcufffdufffd"), ("abcxFFx58", "replace", "abcufffdX"))) # 80:17-89:4
    xcodectests = e19 # 80:4-89:4
e25 = xmultibytecodec_support.xTestBase # 91:21-91:51
e26 = xunittest.xTestCase # 91:54-91:70
class xTest_SJIS_2004(e25, e26): # 91:73-109:-1
    xencoding = "shift_jis_2004" # 92:4-92:30
    e22 = xmultibytecodec_support.xload_teststring # 93:14-93:51
    lhs10 = "shift_jis" # 93:53-93:63
    e23 = e22(lhs10) # 93:14-93:64
    xtstring = e23 # 93:4-93:64
    e24 = (xshiftjis_commonenctests + (("\\x7e", "strict", "xa5u203e"), ("x81x5fx81x61x81x7c", "strict", "\\u2016u2212"), ("abcxEAxFC", "strict", "abcu64bf"), ("x81x39xy", "replace", "ufffd9xy"), ("xFFx58xy", "replace", "ufffdXxy"), ("x80x80x82x84xy", "replace", "ufffdufffduff44xy"), ("x80x80x82x84x88xy", "replace", "ufffdufffduff44u5864y"), ("xFCxFBxy", "replace", "ufffdu95b4y"))) # 94:17-103:4
    xcodectests = e24 # 94:4-103:4
    xxmlcharnametest = ("xabu211cxbb = u2329u1234u232a", "x85G&real;x85Q = &lang;&#4660;&rang;") # 104:4-107:4
e30 = xmultibytecodec_support.xTestBase # 109:21-109:51
e31 = xunittest.xTestCase # 109:54-109:70
class xTest_SJISX0213(e30, e31): # 109:73-125:-1
    xencoding = "shift_jisx0213" # 110:4-110:30
    e27 = xmultibytecodec_support.xload_teststring # 111:14-111:51
    lhs12 = "shift_jisx0213" # 111:53-111:68
    e28 = e27(lhs12) # 111:14-111:69
    xtstring = e28 # 111:4-111:69
    e29 = (xshiftjis_commonenctests + (("abcx80x80x82x84", "replace", "abcufffdufffduff44"), ("abcx80x80x82x84x88", "replace", "abcufffdufffduff44ufffd"), ("\\x7e", "replace", "xa5u203e"), ("x81x5fx81x61x81x7c", "replace", "x5cu2016u2212"))) # 112:17-119:4
    xcodectests = e29 # 112:4-119:4
    xxmlcharnametest = ("xabu211cxbb = u2329u1234u232a", "x85G&real;x85Q = &lang;&#4660;&rang;") # 120:4-123:4
e34 = (x__name__ == "__main__") # 125:3-125:24
if (e34): # 125:26-127:-1 
    e32 = xunittest.xmain # 126:4-126:16
    e33 = e32() # 126:4-126:18
else: # 125:0-127:-1
    pass # 125:0-127:-1