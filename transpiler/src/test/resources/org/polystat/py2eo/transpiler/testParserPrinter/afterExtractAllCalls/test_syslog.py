from xtest import xsupport # 2:17-2:23
e0 = xsupport.ximport_module # 3:9-3:29
lhs0 = "syslog" # 3:31-3:38
e1 = e0(lhs0) # 3:9-3:39
xsyslog = e1 # 3:0-3:39
import xunittest # 4:7-4:14
e31 = xunittest.xTestCase # 10:11-10:27
class xTest(e31): # 11:0-39:-1
    def xtest_openlog(xself): # 12:4-17:3
        e2 = xsyslog.xopenlog # 13:8-13:21
        lhs2 = "python" # 13:23-13:30
        e3 = e2(lhs2) # 13:8-13:31
        e4 = xself.xassertRaises # 15:8-15:24
        lhs4 = xUnicodeEncodeError # 15:26-15:43
        e5 = xsyslog.xopenlog # 15:46-15:59
        lhs5 = "uD800" # 15:62-15:69
        e6 = e4(lhs4, e5, lhs5) # 15:8-15:70
    def xtest_syslog(xself): # 17:4-22:3
        e7 = xsyslog.xopenlog # 18:8-18:21
        lhs7 = "python" # 18:23-18:30
        e8 = e7(lhs7) # 18:8-18:31
        e9 = xsyslog.xsyslog # 19:8-19:20
        lhs9 = "test message from python test_syslog" # 19:22-19:59
        e10 = e9(lhs9) # 19:8-19:60
        e11 = xsyslog.xsyslog # 20:8-20:20
        e12 = xsyslog.xLOG_ERR # 20:22-20:35
        lhs11 = "test error from python test_syslog" # 20:38-20:73
        e13 = e11(e12, lhs11) # 20:8-20:74
    def xtest_closelog(xself): # 22:4-26:3
        e14 = xsyslog.xopenlog # 23:8-23:21
        lhs13 = "python" # 23:23-23:30
        e15 = e14(lhs13) # 23:8-23:31
        e16 = xsyslog.xcloselog # 24:8-24:22
        e17 = e16() # 24:8-24:24
    def xtest_setlogmask(xself): # 26:4-29:3
        e18 = xsyslog.xsetlogmask # 27:8-27:24
        e19 = xsyslog.xLOG_DEBUG # 27:26-27:41
        e20 = e18(e19) # 27:8-27:42
    def xtest_log_mask(xself): # 29:4-32:3
        e21 = xsyslog.xLOG_MASK # 30:8-30:22
        e22 = xsyslog.xLOG_INFO # 30:24-30:38
        e23 = e21(e22) # 30:8-30:39
    def xtest_log_upto(xself): # 32:4-35:3
        e24 = xsyslog.xLOG_UPTO # 33:8-33:22
        e25 = xsyslog.xLOG_INFO # 33:24-33:38
        e26 = e24(e25) # 33:8-33:39
    def xtest_openlog_noargs(xself): # 35:4-39:-1
        e27 = xsyslog.xopenlog # 36:8-36:21
        e28 = e27() # 36:8-36:23
        e29 = xsyslog.xsyslog # 37:8-37:20
        lhs20 = "test message from python test_syslog" # 37:22-37:59
        e30 = e29(lhs20) # 37:8-37:60
e34 = (x__name__ == "__main__") # 39:3-39:24
if (e34): # 39:26-41:-1 
    e32 = xunittest.xmain # 40:4-40:16
    e33 = e32() # 40:4-40:18
else: # 39:0-41:-1
    pass # 39:0-41:-1