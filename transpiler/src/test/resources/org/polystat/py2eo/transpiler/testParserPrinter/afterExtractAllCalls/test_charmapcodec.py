" Python character mapping codec test\n\nThis uses the test codec in testcodec.py and thus also tests the\nencodings package lookup scheme.\n\nWritten by Marc-Andre Lemburg (mal@lemburg.com).\n\n(c) Copyright 2000 Guido van Rossum.\n\n" # 1:0-1:230
import xunittest # 12:7-12:14
import xcodecs # 14:7-14:12
def xcodec_search_function(xencoding): # 17:0-23:-1
    e3 = (xencoding == "testcodec") # 18:7-18:29
    if (e3): # 18:31-21:3 
        from xtest import xtestcodec # 19:25-19:33
        lhs1 = xtuple # 20:15-20:19
        e0 = xtestcodec.xgetregentry # 20:21-20:41
        e1 = e0() # 20:21-20:43
        e2 = lhs1(e1) # 20:15-20:44
        return e2 # 20:15-20:44
    else: # 18:4-21:3
        pass # 18:4-21:3
    return None # 21:4-21:14
e4 = xcodecs.xregister # 23:0-23:14
lhs3 = xcodec_search_function # 23:16-23:36
e5 = e4(lhs3) # 23:0-23:37
xcodecname = "testcodec" # 26:0-26:22
e55 = xunittest.xTestCase # 28:23-28:39
class xCharmapCodecTest(e55): # 28:42-52:-1
    def xtest_constructorx(xself): # 29:4-36:3
        e6 = xself.xassertEqual # 30:8-30:23
        e7 = xstr("abc", xcodecname) # 30:25-30:46
        lhs5 = "abc" # 30:49-30:53
        e8 = e6(e7, lhs5) # 30:8-30:54
        e9 = xself.xassertEqual # 31:8-31:23
        e10 = xstr("xdef", xcodecname) # 31:25-31:47
        lhs7 = "abcdef" # 31:50-31:57
        e11 = e9(e10, lhs7) # 31:8-31:58
        e12 = xself.xassertEqual # 32:8-32:23
        e13 = xstr("defx", xcodecname) # 32:25-32:47
        lhs9 = "defabc" # 32:50-32:57
        e14 = e12(e13, lhs9) # 32:8-32:58
        e15 = xself.xassertEqual # 33:8-33:23
        e16 = xstr("dxf", xcodecname) # 33:25-33:46
        lhs11 = "dabcf" # 33:49-33:55
        e17 = e15(e16, lhs11) # 33:8-33:56
        e18 = xself.xassertEqual # 34:8-34:23
        e19 = xstr("dxfx", xcodecname) # 34:25-34:47
        lhs13 = "dabcfabc" # 34:50-34:59
        e20 = e18(e19, lhs13) # 34:8-34:60
    def xtest_encodex(xself): # 36:4-43:3
        e21 = xself.xassertEqual # 37:8-37:23
        e22 = "abc".xencode # 37:25-37:36
        lhs15 = xcodecname # 37:38-37:46
        e23 = e22(lhs15) # 37:25-37:47
        lhs17 = "abc" # 37:50-37:55
        e24 = e21(e23, lhs17) # 37:8-37:56
        e25 = xself.xassertEqual # 38:8-38:23
        e26 = "xdef".xencode # 38:25-38:37
        lhs19 = xcodecname # 38:39-38:47
        e27 = e26(lhs19) # 38:25-38:48
        lhs21 = "abcdef" # 38:51-38:59
        e28 = e25(e27, lhs21) # 38:8-38:60
        e29 = xself.xassertEqual # 39:8-39:23
        e30 = "defx".xencode # 39:25-39:37
        lhs23 = xcodecname # 39:39-39:47
        e31 = e30(lhs23) # 39:25-39:48
        lhs25 = "defabc" # 39:51-39:59
        e32 = e29(e31, lhs25) # 39:8-39:60
        e33 = xself.xassertEqual # 40:8-40:23
        e34 = "dxf".xencode # 40:25-40:36
        lhs27 = xcodecname # 40:38-40:46
        e35 = e34(lhs27) # 40:25-40:47
        lhs29 = "dabcf" # 40:50-40:57
        e36 = e33(e35, lhs29) # 40:8-40:58
        e37 = xself.xassertEqual # 41:8-41:23
        e38 = "dxfx".xencode # 41:25-41:37
        lhs31 = xcodecname # 41:39-41:47
        e39 = e38(lhs31) # 41:25-41:48
        lhs33 = "dabcfabc" # 41:51-41:61
        e40 = e37(e39, lhs33) # 41:8-41:62
    def xtest_constructory(xself): # 43:4-49:3
        e41 = xself.xassertEqual # 44:8-44:23
        e42 = xstr("ydef", xcodecname) # 44:25-44:47
        lhs35 = "def" # 44:50-44:54
        e43 = e41(e42, lhs35) # 44:8-44:55
        e44 = xself.xassertEqual # 45:8-45:23
        e45 = xstr("defy", xcodecname) # 45:25-45:47
        lhs37 = "def" # 45:50-45:54
        e46 = e44(e45, lhs37) # 45:8-45:55
        e47 = xself.xassertEqual # 46:8-46:23
        e48 = xstr("dyf", xcodecname) # 46:25-46:46
        lhs39 = "df" # 46:49-46:52
        e49 = e47(e48, lhs39) # 46:8-46:53
        e50 = xself.xassertEqual # 47:8-47:23
        e51 = xstr("dyfy", xcodecname) # 47:25-47:47
        lhs41 = "df" # 47:50-47:53
        e52 = e50(e51, lhs41) # 47:8-47:54
    def xtest_maptoundefined(xself): # 49:4-52:-1
        e53 = xself.xassertRaises # 50:8-50:24
        lhs43 = xUnicodeError # 50:26-50:37
        lhs44 = xstr # 50:40-50:42
        lhs45 = "abc001" # 50:45-50:54
        lhs46 = xcodecname # 50:57-50:65
        e54 = e53(lhs43, lhs44, lhs45, lhs46) # 50:8-50:66
e58 = (x__name__ == "__main__") # 52:3-52:24
if (e58): # 52:26-54:-1 
    e56 = xunittest.xmain # 53:4-53:16
    e57 = e56() # 53:4-53:18
else: # 52:0-54:-1
    pass # 52:0-54:-1