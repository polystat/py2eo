"This test checks for correct wait3() behavior.\n" # 1:0-1:52
import xos # 4:7-4:8
import xtime # 5:7-5:10
import xunittest # 6:7-6:14
from xtest.xfork_wait import xForkWait # 7:27-7:34
from xtest.xsupport import xreap_children # 8:25-8:37
e2 = xhasattr(xos, "fork") # 10:7-10:25
e3 = (not e2) # 10:3-10:25
if (e3): # 10:27-13:-1 
    e0 = xunittest.xSkipTest # 11:10-11:26
    lhs0 = "os.fork not defined" # 11:28-11:48
    e1 = e0(lhs0) # 11:10-11:49
    return e1 # 11:10-11:49
else: # 10:0-13:-1
    pass # 10:0-13:-1
e6 = xhasattr(xos, "wait3") # 13:7-13:26
e7 = (not e6) # 13:3-13:26
if (e7): # 13:28-16:-1 
    e4 = xunittest.xSkipTest # 14:10-14:26
    lhs3 = "os.wait3 not defined" # 14:28-14:49
    e5 = e4(lhs3) # 14:10-14:50
    return e5 # 14:10-14:50
else: # 13:0-16:-1
    pass # 13:0-16:-1
class xWait3Test(xForkWait): # 16:0-34:-1
    def xwait_impl(xself, xcpid): # 17:4-34:-1
        e8 = xtime.xmonotonic # 21:19-21:32
        e9 = e8() # 21:19-21:34
        lhs7 = 10.0 # 21:38-21:41
        e10 = (e9 + lhs7) # 21:19-21:41
        xdeadline = e10 # 21:8-21:41
        while (True): # 22:14-22:41
            e17 = xtime.xmonotonic # 22:14-22:27
            e18 = e17() # 22:14-22:29
            lhs13 = xdeadline # 22:34-22:41
            e19 = (e18 <= lhs13) # 22:14-22:41
            if ((not e19)): # 22:8-30:7 
                break # 22:8-30:7
            else: # 22:8-30:7
                pass # 22:8-30:7
            e11 = xos.xwait3 # 25:35-25:42
            e12 = xos.xWNOHANG # 25:44-25:53
            e13 = e11(e12) # 25:35-25:54
            (xspid, xstatus, xrusage) = e13 # 25:12-25:54
            e14 = (xspid == xcpid) # 26:15-26:26
            if (e14): # 26:28-28:11 
                break # 27:16-27:20
            else: # 26:12-28:11
                pass # 26:12-28:11
            e15 = xtime.xsleep # 28:12-28:21
            lhs10 = 0.1 # 28:23-28:25
            e16 = e15(lhs10) # 28:12-28:26
        else:
            pass # 22:8-30:7
        e20 = xself.xassertEqual # 30:8-30:23
        lhs15 = xspid # 30:25-30:28
        lhs16 = xcpid # 30:31-30:34
        e21 = e20(lhs15, lhs16) # 30:8-30:35
        e22 = xself.xassertEqual # 31:8-31:23
        lhs21 = xstatus # 31:25-31:30
        lhs22 = 0  # 31:33-31:33
        lhs19 = "cause = %d, exit = %d" # 31:36-31:58
        e23 = (xstatus & 255 ) # 31:63-31:73
        e24 = (xstatus >> 8 ) # 31:76-31:84
        lhs18 = (e23, e24) # 31:62-31:85
        e25 = (lhs19 % lhs18) # 31:36-31:85
        e26 = e22(lhs21, lhs22, e25) # 31:8-31:86
        e27 = xself.xassertTrue # 32:8-32:22
        lhs24 = xrusage # 32:24-32:29
        e28 = e27(lhs24) # 32:8-32:30
def xtearDownModule(): # 34:0-37:-1
    e29 = xreap_children() # 35:4-35:18
e32 = (x__name__ == "__main__") # 37:3-37:24
if (e32): # 37:26-39:-1 
    e30 = xunittest.xmain # 38:4-38:16
    e31 = e30() # 38:4-38:18
else: # 37:0-39:-1
    pass # 37:0-39:-1