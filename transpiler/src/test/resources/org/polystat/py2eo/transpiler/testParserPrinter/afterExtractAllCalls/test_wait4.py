"This test checks for correct wait4() behavior.\n" # 1:0-1:52
import xos # 4:7-4:8
import xtime # 5:7-5:10
import xsys # 6:7-6:9
import xunittest # 7:7-7:14
from xtest.xfork_wait import xForkWait # 8:27-8:34
from xtest.xsupport import xreap_children # 9:25-9:37
from xtest.xsupport import xget_attribute # 9:40-9:52
e0 = xget_attribute(xos, "fork") # 12:0-12:24
e1 = xget_attribute(xos, "wait4") # 13:0-13:25
class xWait4Test(xForkWait): # 16:0-35:-1
    def xwait_impl(xself, xcpid): # 17:4-35:-1
        e2 = xos.xWNOHANG # 18:17-18:26
        xoption = e2 # 18:8-18:26
        e3 = xsys.xplatform # 19:11-19:22
        e4 = e3.xstartswith # 19:11-19:33
        lhs1 = "aix" # 19:35-19:39
        e5 = e4(lhs1) # 19:11-19:40
        if (e5): # 21:27-23:7 
            xoption = 0  # 22:12-22:21
        else: # 19:8-23:7
            pass # 19:8-23:7
        e6 = xtime.xmonotonic # 23:19-23:32
        e7 = e6() # 23:19-23:34
        lhs4 = 10.0 # 23:38-23:41
        e8 = (e7 + lhs4) # 23:19-23:41
        xdeadline = e8 # 23:8-23:41
        while (True): # 24:14-24:41
            e14 = xtime.xmonotonic # 24:14-24:27
            e15 = e14() # 24:14-24:29
            lhs12 = xdeadline # 24:34-24:41
            e16 = (e15 <= lhs12) # 24:14-24:41
            if ((not e16)): # 24:8-31:7 
                break # 24:8-31:7
            else: # 24:8-31:7
                pass # 24:8-31:7
            e9 = xos.xwait4 # 27:35-27:42
            lhs6 = xcpid # 27:44-27:47
            lhs7 = xoption # 27:50-27:55
            e10 = e9(lhs6, lhs7) # 27:35-27:56
            (xspid, xstatus, xrusage) = e10 # 27:12-27:56
            e11 = (xspid == xcpid) # 28:15-28:26
            if (e11): # 28:28-30:11 
                break # 29:16-29:20
            else: # 28:12-30:11
                pass # 28:12-30:11
            e12 = xtime.xsleep # 30:12-30:21
            lhs9 = 0.1 # 30:23-30:25
            e13 = e12(lhs9) # 30:12-30:26
        else:
            pass # 24:8-31:7
        e17 = xself.xassertEqual # 31:8-31:23
        lhs14 = xspid # 31:25-31:28
        lhs15 = xcpid # 31:31-31:34
        e18 = e17(lhs14, lhs15) # 31:8-31:35
        e19 = xself.xassertEqual # 32:8-32:23
        lhs20 = xstatus # 32:25-32:30
        lhs21 = 0  # 32:33-32:33
        lhs18 = "cause = %d, exit = %d" # 32:36-32:58
        e20 = (xstatus & 255 ) # 32:63-32:73
        e21 = (xstatus >> 8 ) # 32:76-32:84
        lhs17 = (e20, e21) # 32:62-32:85
        e22 = (lhs18 % lhs17) # 32:36-32:85
        e23 = e19(lhs20, lhs21, e22) # 32:8-32:86
        e24 = xself.xassertTrue # 33:8-33:22
        lhs23 = xrusage # 33:24-33:29
        e25 = e24(lhs23) # 33:8-33:30
def xtearDownModule(): # 35:0-38:-1
    e26 = xreap_children() # 36:4-36:18
e29 = (x__name__ == "__main__") # 38:3-38:24
if (e29): # 38:26-40:-1 
    e27 = xunittest.xmain # 39:4-39:16
    e28 = e27() # 39:4-39:18
else: # 38:0-40:-1
    pass # 38:0-40:-1