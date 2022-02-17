import xfaulthandler # 1:7-1:18
import xtest.xsupport # 2:7-2:18
import xunittest # 3:7-3:14
e0 = xtest.xsupport # 5:14-5:25
e1 = e0.ximport_module # 5:14-5:39
lhs1 = "_xxtestfuzz" # 5:41-5:53
e2 = e1(lhs1) # 5:14-5:54
x_xxtestfuzz = e2 # 5:0-5:54
e19 = xunittest.xTestCase # 8:17-8:33
class xTestFuzzer(e19): # 8:36-23:-1
    "To keep our https://github.com/google/oss-fuzz API working." # 9:4-9:68
    def xtest_sample_input_smoke_test(xself): # 11:4-23:-1
        "This is only a regression test: Check that it doesn't crash." # 12:8-12:73
        e3 = x_xxtestfuzz.xrun # 13:8-13:22
        lhs3 = "" # 13:24-13:26
        e4 = e3(lhs3) # 13:8-13:27
        e5 = x_xxtestfuzz.xrun # 14:8-14:22
        lhs5 = "0" # 14:24-14:28
        e6 = e5(lhs5) # 14:8-14:29
        e7 = x_xxtestfuzz.xrun # 15:8-15:22
        lhs7 = "{" # 15:24-15:27
        e8 = e7(lhs7) # 15:8-15:28
        e9 = x_xxtestfuzz.xrun # 16:8-16:22
        lhs9 = " " # 16:24-16:27
        e10 = e9(lhs9) # 16:8-16:28
        e11 = x_xxtestfuzz.xrun # 17:8-17:22
        lhs11 = "x" # 17:24-17:27
        e12 = e11(lhs11) # 17:8-17:28
        e13 = x_xxtestfuzz.xrun # 18:8-18:22
        lhs13 = "1" # 18:24-18:27
        e14 = e13(lhs13) # 18:8-18:28
        e15 = x_xxtestfuzz.xrun # 19:8-19:22
        lhs15 = "AAAAAAA" # 19:24-19:33
        e16 = e15(lhs15) # 19:8-19:34
        e17 = x_xxtestfuzz.xrun # 20:8-20:22
        lhs17 = "AAAAAA0" # 20:24-20:34
        e18 = e17(lhs17) # 20:8-20:35
e24 = (x__name__ == "__main__") # 23:3-23:24
if (e24): # 23:26-26:-1 
    e20 = xfaulthandler.xenable # 24:4-24:22
    e21 = e20() # 24:4-24:24
    e22 = xunittest.xmain # 25:4-25:16
    e23 = e22() # 25:4-25:18
else: # 23:0-26:-1
    pass # 23:0-26:-1