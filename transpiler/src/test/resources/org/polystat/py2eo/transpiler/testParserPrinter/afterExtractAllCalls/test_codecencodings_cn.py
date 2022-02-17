from xtest import xmultibytecodec_support # 6:17-6:38
import xunittest # 7:7-7:14
e2 = xmultibytecodec_support.xTestBase # 9:18-9:48
e3 = xunittest.xTestCase # 9:51-9:67
class xTest_GB2312(e2, e3): # 9:70-22:-1
    xencoding = "gb2312" # 10:4-10:22
    e0 = xmultibytecodec_support.xload_teststring # 11:14-11:51
    lhs0 = "gb2312" # 11:53-11:60
    e1 = e0(lhs0) # 11:14-11:61
    xtstring = e1 # 11:4-11:61
    xcodectests = (("abcx81x81xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx81x81xc1xc4", "replace", "abcufffdufffdu804a"), ("abcx81x81xc1xc4xc8", "replace", "abcufffdufffdu804aufffd"), ("abcx81x81xc1xc4", "ignore", "abcu804a"), ("xc1x64", "strict", None)) # 12:4-20:4
e6 = xmultibytecodec_support.xTestBase # 22:15-22:45
e7 = xunittest.xTestCase # 22:48-22:64
class xTest_GBK(e6, e7): # 22:67-36:-1
    xencoding = "gbk" # 23:4-23:19
    e4 = xmultibytecodec_support.xload_teststring # 24:14-24:51
    lhs2 = "gbk" # 24:53-24:57
    e5 = e4(lhs2) # 24:14-24:58
    xtstring = e5 # 24:4-24:58
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffdu804a"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffdu804aufffd"), ("abcx80x80xc1xc4", "ignore", "abcu804a"), ("x83x34x83x31", "strict", None), ("u30fb", "strict", None)) # 25:4-34:4
e10 = xmultibytecodec_support.xTestBase # 36:19-36:49
e11 = xunittest.xTestCase # 36:52-36:68
class xTest_GB18030(e10, e11): # 36:71-60:-1
    xencoding = "gb18030" # 37:4-37:23
    e8 = xmultibytecodec_support.xload_teststring # 38:14-38:51
    lhs4 = "gb18030" # 38:53-38:61
    e9 = e8(lhs4) # 38:14-38:62
    xtstring = e9 # 38:4-38:62
    xcodectests = (("abcx80x80xc1xc4", "strict", None), ("abcxc8", "strict", None), ("abcx80x80xc1xc4", "replace", "abcufffdufffdu804a"), ("abcx80x80xc1xc4xc8", "replace", "abcufffdufffdu804aufffd"), ("abcx80x80xc1xc4", "ignore", "abcu804a"), ("abcx84x39x84x39xc1xc4", "replace", "abcufffd9ufffd9u804a"), ("u30fb", "strict", "x819xa79"), ("abcx84x32x80x80def", "replace", "abcufffd2ufffdufffddef"), ("abcx81x30x81x30def", "strict", "abcx80def"), ("abcx86x30x81x30def", "replace", "abcufffd0ufffd0def"), ("xffx30x81x30", "strict", None), ("x81x30xffx30", "strict", None), ("abcx81x39xffx39xc1xc4", "replace", "abcufffdx39ufffdx39u804a"), ("abcxabx36xffx30def", "replace", "abcufffdx36ufffdx30def"), ("abcxbfx38xffx32xc1xc4", "ignore", "abcx38x32u804a")) # 39:4-57:4
    xhas_iso10646 = True # 58:4-58:22
e14 = xmultibytecodec_support.xTestBase # 60:14-60:44
e15 = xunittest.xTestCase # 60:47-60:63
class xTest_HZ(e14, e15): # 60:66-95:-1
    xencoding = "hz" # 61:4-61:18
    e12 = xmultibytecodec_support.xload_teststring # 62:14-62:51
    lhs6 = "hz" # 62:53-62:56
    e13 = e12(lhs6) # 62:14-62:57
    xtstring = e13 # 62:4-62:57
    xcodectests = (("This sentence is in ASCII.nThe next sentence is in GB.~{<:Ky2;S{#,~}~n~{NpJ)l6HK!#~}Bye.n", "strict", "This sentence is in ASCII.nThe next sentence is in GB.u5df1u6240u4e0du6b32uff0cu52ffu65bdu65bcu4ebau3002Bye.n"), ("This sentence is in ASCII.nThe next sentence is in GB.~n~{<:Ky2;S{#,NpJ)l6HK!#~}~nBye.n", "strict", "This sentence is in ASCII.nThe next sentence is in GB.u5df1u6240u4e0du6b32uff0cu52ffu65bdu65bcu4ebau3002Bye.n"), ("ab~cd", "replace", "abuFFFDcd"), ("abxffcd", "replace", "abuFFFDcd"), ("ab~{x81x81x41x44~}cd", "replace", "abuFFFDuFFFDu804Acd"), ("ab~{x41x44~}cd", "replace", "abu804Acd"), ("ab~{x79x79x41x44~}cd", "replace", "abufffdufffdu804acd"), ("ab~cd", "strict", "ab~~cd"), ("~{Dc~~:C~}", "strict", None), ("~{Dc~n:C~}", "strict", None)) # 63:4-93:4
e18 = (x__name__ == "__main__") # 95:3-95:24
if (e18): # 95:26-97:-1 
    e16 = xunittest.xmain # 96:4-96:16
    e17 = e16() # 96:4-96:18
else: # 95:0-97:-1
    pass # 95:0-97:-1