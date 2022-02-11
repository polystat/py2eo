import xos # 3:7-3:8
import xunittest # 3:11-3:18
e2 = xhasattr(xos, "openpty") # 5:7-5:28
e3 = (not e2) # 5:3-5:28
if (e3): # 5:30-9:-1 
    e0 = xunittest.xSkipTest # 6:10-6:26
    lhs0 = "os.openpty() not available." # 6:28-6:56
    e1 = e0(lhs0) # 6:10-6:57
    return e1 # 6:10-6:57
else: # 5:0-9:-1
    pass # 5:0-9:-1
e23 = xunittest.xTestCase # 9:18-9:34
class xOpenptyTest(e23): # 9:37-20:-1
    def xtest(xself): # 10:4-20:-1
        e4 = xos.xopenpty # 11:24-11:33
        e5 = e4() # 11:24-11:35
        (xmaster, xslave) = e5 # 11:8-11:35
        e6 = xself.xaddCleanup # 12:8-12:22
        e7 = xos.xclose # 12:24-12:31
        lhs4 = xmaster # 12:34-12:39
        e8 = e6(e7, lhs4) # 12:8-12:40
        e9 = xself.xaddCleanup # 13:8-13:22
        e10 = xos.xclose # 13:24-13:31
        lhs6 = xslave # 13:34-13:38
        e11 = e9(e10, lhs6) # 13:8-13:39
        e14 = xos.xisatty # 14:15-14:23
        lhs10 = xslave # 14:25-14:29
        e15 = e14(lhs10) # 14:15-14:30
        e16 = (not e15) # 14:11-14:30
        if (e16): # 14:32-17:7 
            e12 = xself.xfail # 15:12-15:20
            lhs8 = "Slave-end of pty is not a terminal." # 15:22-15:58
            e13 = e12(lhs8) # 15:12-15:59
        else: # 14:8-17:7
            pass # 14:8-17:7
        e17 = xos.xwrite # 17:8-17:15
        lhs13 = xslave # 17:17-17:21
        lhs14 = "Ping!" # 17:24-17:31
        e18 = e17(lhs13, lhs14) # 17:8-17:32
        e19 = xself.xassertEqual # 18:8-18:23
        e20 = xos.xread # 18:25-18:31
        lhs16 = xmaster # 18:33-18:38
        lhs17 = 1024  # 18:41-18:44
        e21 = e20(lhs16, lhs17) # 18:25-18:45
        lhs19 = "Ping!" # 18:48-18:55
        e22 = e19(e21, lhs19) # 18:8-18:56
e26 = (x__name__ == "__main__") # 20:3-20:24
if (e26): # 20:26-22:-1 
    e24 = xunittest.xmain # 21:4-21:16
    e25 = e24() # 21:4-21:18
else: # 20:0-22:-1
    pass # 20:0-22:-1