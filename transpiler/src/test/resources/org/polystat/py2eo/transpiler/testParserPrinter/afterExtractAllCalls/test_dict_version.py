"\nTest implementation of the PEP 509: dictionary versionning.\n" # 1:0-1:66
import xunittest # 4:7-4:14
from xtest import xsupport # 5:17-5:23
e0 = xsupport.ximport_module # 9:12-9:32
lhs0 = "_testcapi" # 9:34-9:44
e1 = e0(lhs0) # 9:12-9:45
x_testcapi = e1 # 9:0-9:45
e162 = xunittest.xTestCase # 12:23-12:39
class xDictVersionTests(e162): # 12:42-177:-1
    xtype2test = xdict # 13:4-13:19
    def xsetUp(xself): # 15:4-19:3
        e2 = xset() # 16:29-16:33
        xself.xseen_versions = e2 # 16:8-16:33
        xself.xdict = None # 17:8-17:23
    def xcheck_version_unique(xself, xmydict): # 19:4-24:3
        e3 = x_testcapi.xdict_get_version # 20:18-20:43
        lhs2 = xmydict # 20:45-20:50
        e4 = e3(lhs2) # 20:18-20:51
        xversion = e4 # 20:8-20:51
        e5 = xself.xassertNotIn # 21:8-21:23
        lhs4 = xversion # 21:25-21:31
        e6 = xself.xseen_versions # 21:34-21:51
        e7 = e5(lhs4, e6) # 21:8-21:52
        e8 = xself.xseen_versions # 22:8-22:25
        e9 = e8.xadd # 22:8-22:29
        lhs7 = xversion # 22:31-22:37
        e10 = e9(lhs7) # 22:8-22:38
    def xcheck_version_changed(xself, xmydict, xmethod, *xargs, **xkw): # 24:4-29:3
        lhs9 = xmethod # 25:17-25:22
        e11 = *xargs # 25:24-25:28
        e12 = **xkw # 25:31-25:34
        e13 = lhs9(e11, e12) # 25:17-25:35
        xresult = e13 # 25:8-25:35
        e14 = xself.xcheck_version_unique # 26:8-26:32
        lhs11 = xmydict # 26:34-26:39
        e15 = e14(lhs11) # 26:8-26:40
        return xresult # 27:8-27:20
    def xcheck_version_dont_change(xself, xmydict, xmethod, *xargs, **xkw): # 29:4-40:3
        e16 = x_testcapi.xdict_get_version # 30:19-30:44
        lhs13 = xmydict # 30:46-30:51
        e17 = e16(lhs13) # 30:19-30:52
        xversion1 = e17 # 30:8-30:52
        e18 = xself.xseen_versions # 31:8-31:25
        e19 = e18.xadd # 31:8-31:29
        lhs16 = xversion1 # 31:31-31:38
        e20 = e19(lhs16) # 31:8-31:39
        lhs18 = xmethod # 33:17-33:22
        e21 = *xargs # 33:24-33:28
        e22 = **xkw # 33:31-33:34
        e23 = lhs18(e21, e22) # 33:17-33:35
        xresult = e23 # 33:8-33:35
        e24 = x_testcapi.xdict_get_version # 35:19-35:44
        lhs20 = xmydict # 35:46-35:51
        e25 = e24(lhs20) # 35:19-35:52
        xversion2 = e25 # 35:8-35:52
        e26 = xself.xassertEqual # 36:8-36:23
        lhs22 = xversion2 # 36:25-36:32
        lhs23 = xversion1 # 36:35-36:42
        lhs24 = "version changed" # 36:45-36:61
        e27 = e26(lhs22, lhs23, lhs24) # 36:8-36:62
        return xresult # 38:8-38:21
    def xnew_dict(xself, *xargs, **xkw): # 40:4-45:3
        e28 = xself.xtype2test # 41:12-41:25
        e29 = *xargs # 41:27-41:31
        e30 = **xkw # 41:34-41:37
        e31 = e28(e29, e30) # 41:12-41:38
        xd = e31 # 41:8-41:38
        e32 = xself.xcheck_version_unique # 42:8-42:32
        lhs27 = xd # 42:34-42:34
        e33 = e32(lhs27) # 42:8-42:35
        return xd # 43:8-43:15
    def xtest_constructor(xself): # 45:4-55:3
        e34 = xself.xnew_dict # 47:17-47:29
        e35 = e34() # 47:17-47:31
        xempty1 = e35 # 47:8-47:31
        e36 = xself.xnew_dict # 48:17-48:29
        e37 = e36() # 48:17-48:31
        xempty2 = e37 # 48:8-48:31
        e38 = xself.xnew_dict # 49:17-49:29
        e39 = e38() # 49:17-49:31
        xempty3 = e39 # 49:8-49:31
        e40 = xself.xnew_dict # 52:20-52:32
        lhs32 = "x" # 52:36-52:38
        e41 = e40(xx=lhs32) # 52:20-52:39
        xnonempty1 = e41 # 52:8-52:39
        e42 = xself.xnew_dict # 53:20-53:32
        lhs34 = "x" # 53:36-53:38
        lhs35 = "y" # 53:43-53:45
        e43 = e42(xx=lhs34, xy=lhs35) # 53:20-53:46
        xnonempty2 = e43 # 53:8-53:46
    def xtest_copy(xself): # 55:4-63:3
        e44 = xself.xnew_dict # 56:12-56:24
        lhs37 = 1  # 56:28-56:28
        lhs38 = 2  # 56:33-56:33
        e45 = e44(xa=lhs37, xb=lhs38) # 56:12-56:34
        xd = e45 # 56:8-56:34
        e46 = xself.xcheck_version_dont_change # 58:13-58:42
        lhs40 = xd # 58:44-58:44
        e47 = xd.xcopy # 58:47-58:52
        e48 = e46(lhs40, e47) # 58:13-58:53
        xd2 = e48 # 58:8-58:53
        e49 = xself.xcheck_version_unique # 61:8-61:32
        lhs42 = xd2 # 61:34-61:35
        e50 = e49(lhs42) # 61:8-61:36
    def xtest_setitem(xself): # 63:4-74:3
        e51 = xself.xnew_dict # 64:12-64:24
        e52 = e51() # 64:12-64:26
        xd = e52 # 64:8-64:26
        e53 = xself.xcheck_version_changed # 67:8-67:33
        lhs45 = xd # 67:35-67:35
        e54 = xd.x__setitem__ # 67:38-67:50
        lhs46 = "x" # 67:53-67:55
        lhs47 = "x" # 67:58-67:60
        e55 = e53(lhs45, e54, lhs46, lhs47) # 67:8-67:61
        e56 = xself.xcheck_version_changed # 68:8-68:33
        lhs49 = xd # 68:35-68:35
        e57 = xd.x__setitem__ # 68:38-68:50
        lhs50 = "y" # 68:53-68:55
        lhs51 = "y" # 68:58-68:60
        e58 = e56(lhs49, e57, lhs50, lhs51) # 68:8-68:61
        e59 = xself.xcheck_version_changed # 71:8-71:33
        lhs53 = xd # 71:35-71:35
        e60 = xd.x__setitem__ # 71:38-71:50
        lhs54 = "x" # 71:53-71:55
        lhs55 = 1  # 71:58-71:58
        e61 = e59(lhs53, e60, lhs54, lhs55) # 71:8-71:59
        e62 = xself.xcheck_version_changed # 72:8-72:33
        lhs57 = xd # 72:35-72:35
        e63 = xd.x__setitem__ # 72:38-72:50
        lhs58 = "y" # 72:53-72:55
        lhs59 = 2  # 72:58-72:58
        e64 = e62(lhs57, e63, lhs58, lhs59) # 72:8-72:59
    def xtest_setitem_same_value(xself): # 74:4-92:3
        e65 = xobject() # 75:16-75:23
        xvalue = e65 # 75:8-75:23
        e66 = xself.xnew_dict # 76:12-76:24
        e67 = e66() # 76:12-76:26
        xd = e67 # 76:8-76:26
        e68 = xself.xcheck_version_changed # 79:8-79:33
        lhs62 = xd # 79:35-79:35
        e69 = xd.x__setitem__ # 79:38-79:50
        lhs63 = "key" # 79:53-79:57
        lhs64 = xvalue # 79:60-79:64
        e70 = e68(lhs62, e69, lhs63, lhs64) # 79:8-79:65
        e71 = xself.xcheck_version_dont_change # 83:8-83:37
        lhs66 = xd # 83:39-83:39
        e72 = xd.x__setitem__ # 83:42-83:54
        lhs67 = "key" # 83:57-83:61
        lhs68 = xvalue # 83:64-83:68
        e73 = e71(lhs66, e72, lhs67, lhs68) # 83:8-83:69
        e74 = xself.xcheck_version_dont_change # 87:8-87:37
        lhs70 = xd # 87:39-87:39
        e75 = xd.xupdate # 87:42-87:49
        lhs71 = xvalue # 87:56-87:60
        e76 = e74(lhs70, e75, xkey=lhs71) # 87:8-87:61
        e77 = xself.xnew_dict # 89:13-89:25
        lhs73 = xvalue # 89:31-89:35
        e78 = e77(xkey=lhs73) # 89:13-89:36
        xd2 = e78 # 89:8-89:36
        e79 = xself.xcheck_version_dont_change # 90:8-90:37
        lhs75 = xd # 90:39-90:39
        e80 = xd.xupdate # 90:42-90:49
        lhs76 = xd2 # 90:52-90:53
        e81 = e79(lhs75, e80, lhs76) # 90:8-90:54
    def xtest_setitem_equal(xself): # 92:4-116:3
        class xAlwaysEqual(): # 93:8-97:7
            def x__eq__(xself, xother): # 94:12-97:7
                return True # 95:16-95:26
        e82 = xAlwaysEqual() # 97:17-97:29
        xvalue1 = e82 # 97:8-97:29
        e83 = xAlwaysEqual() # 98:17-98:29
        xvalue2 = e83 # 98:8-98:29
        e84 = xself.xassertTrue # 99:8-99:22
        e85 = (xvalue1 == xvalue2) # 99:24-99:39
        e86 = e84(e85) # 99:8-99:40
        e87 = xself.xassertFalse # 100:8-100:23
        e88 = (xvalue1 != xvalue2) # 100:25-100:40
        e89 = e87(e88) # 100:8-100:41
        e90 = xself.xnew_dict # 102:12-102:24
        e91 = e90() # 102:12-102:26
        xd = e91 # 102:8-102:26
        e92 = xself.xcheck_version_changed # 103:8-103:33
        lhs81 = xd # 103:35-103:35
        e93 = xd.x__setitem__ # 103:38-103:50
        lhs82 = "key" # 103:53-103:57
        lhs83 = xvalue1 # 103:60-103:65
        e94 = e92(lhs81, e93, lhs82, lhs83) # 103:8-103:66
        e95 = xself.xcheck_version_changed # 107:8-107:33
        lhs85 = xd # 107:35-107:35
        e96 = xd.x__setitem__ # 107:38-107:50
        lhs86 = "key" # 107:53-107:57
        lhs87 = xvalue2 # 107:60-107:65
        e97 = e95(lhs85, e96, lhs86, lhs87) # 107:8-107:66
        e98 = xself.xcheck_version_changed # 111:8-111:33
        lhs89 = xd # 111:35-111:35
        e99 = xd.xupdate # 111:38-111:45
        lhs90 = xvalue1 # 111:52-111:57
        e100 = e98(lhs89, e99, xkey=lhs90) # 111:8-111:58
        e101 = xself.xnew_dict # 113:13-113:25
        lhs92 = xvalue2 # 113:31-113:36
        e102 = e101(xkey=lhs92) # 113:13-113:37
        xd2 = e102 # 113:8-113:37
        e103 = xself.xcheck_version_changed # 114:8-114:33
        lhs94 = xd # 114:35-114:35
        e104 = xd.xupdate # 114:38-114:45
        lhs95 = xd2 # 114:48-114:49
        e105 = e103(lhs94, e104, lhs95) # 114:8-114:50
    def xtest_setdefault(xself): # 116:4-125:3
        e106 = xself.xnew_dict # 117:12-117:24
        e107 = e106() # 117:12-117:26
        xd = e107 # 117:8-117:26
        e108 = xself.xcheck_version_changed # 120:8-120:33
        lhs98 = xd # 120:35-120:35
        e109 = xd.xsetdefault # 120:38-120:49
        lhs99 = "key" # 120:52-120:56
        lhs100 = "value1" # 120:59-120:66
        e110 = e108(lhs98, e109, lhs99, lhs100) # 120:8-120:67
        e111 = xself.xcheck_version_dont_change # 123:8-123:37
        lhs102 = xd # 123:39-123:39
        e112 = xd.xsetdefault # 123:42-123:53
        lhs103 = "key" # 123:56-123:60
        lhs104 = "value2" # 123:63-123:70
        e113 = e111(lhs102, e112, lhs103, lhs104) # 123:8-123:71
    def xtest_delitem(xself): # 125:4-135:3
        e114 = xself.xnew_dict # 126:12-126:24
        lhs106 = "value" # 126:30-126:36
        e115 = e114(xkey=lhs106) # 126:12-126:37
        xd = e115 # 126:8-126:37
        e116 = xself.xcheck_version_changed # 129:8-129:33
        lhs108 = xd # 129:35-129:35
        e117 = xd.x__delitem__ # 129:38-129:50
        lhs109 = "key" # 129:53-129:57
        e118 = e116(lhs108, e117, lhs109) # 129:8-129:58
        e119 = xself.xcheck_version_dont_change # 132:8-132:37
        lhs111 = xd # 132:39-132:39
        e120 = xself.xassertRaises # 132:42-132:58
        lhs112 = xKeyError # 132:61-132:68
        e121 = xd.x__delitem__ # 133:39-133:51
        lhs113 = "key" # 133:54-133:58
        e122 = e119(lhs111, e120, lhs112, e121, lhs113) # 132:8-133:59
    def xtest_pop(xself): # 135:4-145:3
        e123 = xself.xnew_dict # 136:12-136:24
        lhs115 = "value" # 136:30-136:36
        e124 = e123(xkey=lhs115) # 136:12-136:37
        xd = e124 # 136:8-136:37
        e125 = xself.xcheck_version_changed # 139:8-139:33
        lhs117 = xd # 139:35-139:35
        e126 = xd.xpop # 139:38-139:42
        lhs118 = "key" # 139:45-139:49
        e127 = e125(lhs117, e126, lhs118) # 139:8-139:50
        e128 = xself.xcheck_version_dont_change # 142:8-142:37
        lhs120 = xd # 142:39-142:39
        e129 = xself.xassertRaises # 142:42-142:58
        lhs121 = xKeyError # 142:61-142:68
        e130 = xd.xpop # 143:39-143:43
        lhs122 = "key" # 143:46-143:50
        e131 = e128(lhs120, e129, lhs121, e130, lhs122) # 142:8-143:51
    def xtest_popitem(xself): # 145:4-155:3
        e132 = xself.xnew_dict # 146:12-146:24
        lhs124 = "value" # 146:30-146:36
        e133 = e132(xkey=lhs124) # 146:12-146:37
        xd = e133 # 146:8-146:37
        e134 = xself.xcheck_version_changed # 149:8-149:33
        lhs126 = xd # 149:35-149:35
        e135 = xd.xpopitem # 149:38-149:46
        e136 = e134(lhs126, e135) # 149:8-149:47
        e137 = xself.xcheck_version_dont_change # 152:8-152:37
        lhs128 = xd # 152:39-152:39
        e138 = xself.xassertRaises # 152:42-152:58
        lhs129 = xKeyError # 152:61-152:68
        e139 = xd.xpopitem # 153:39-153:47
        e140 = e137(lhs128, e138, lhs129, e139) # 152:8-153:48
    def xtest_update(xself): # 155:4-167:3
        e141 = xself.xnew_dict # 156:12-156:24
        lhs131 = "value" # 156:30-156:36
        e142 = e141(xkey=lhs131) # 156:12-156:37
        xd = e142 # 156:8-156:37
        e143 = xself.xcheck_version_dont_change # 159:8-159:37
        lhs133 = xd # 159:39-159:39
        e144 = xd.xupdate # 159:42-159:49
        e145 = e143(lhs133, e144) # 159:8-159:50
        e146 = xself.xcheck_version_changed # 162:8-162:33
        lhs135 = xd # 162:35-162:35
        e147 = xd.xupdate # 162:38-162:45
        lhs136 = "new value" # 162:52-162:62
        e148 = e146(lhs135, e147, xkey=lhs136) # 162:8-162:63
        e149 = xself.xnew_dict # 164:13-164:25
        lhs138 = "value 3" # 164:31-164:39
        e150 = e149(xkey=lhs138) # 164:13-164:40
        xd2 = e150 # 164:8-164:40
        e151 = xself.xcheck_version_changed # 165:8-165:33
        lhs140 = xd # 165:35-165:35
        e152 = xd.xupdate # 165:38-165:45
        lhs141 = xd2 # 165:48-165:49
        e153 = e151(lhs140, e152, lhs141) # 165:8-165:50
    def xtest_clear(xself): # 167:4-177:-1
        e154 = xself.xnew_dict # 168:12-168:24
        lhs143 = "value" # 168:30-168:36
        e155 = e154(xkey=lhs143) # 168:12-168:37
        xd = e155 # 168:8-168:37
        e156 = xself.xcheck_version_changed # 171:8-171:33
        lhs145 = xd # 171:35-171:35
        e157 = xd.xclear # 171:38-171:44
        e158 = e156(lhs145, e157) # 171:8-171:45
        e159 = xself.xcheck_version_dont_change # 174:8-174:37
        lhs147 = xd # 174:39-174:39
        e160 = xd.xclear # 174:42-174:48
        e161 = e159(lhs147, e160) # 174:8-174:49
class xDict(xdict): # 177:0-181:-1
    pass # 178:4-178:7
class xDictSubtypeVersionTests(xDictVersionTests): # 181:0-185:-1
    xtype2test = xDict # 182:4-182:19
e165 = (x__name__ == "__main__") # 185:3-185:24
if (e165): # 185:26-187:-1 
    e163 = xunittest.xmain # 186:4-186:16
    e164 = e163() # 186:4-186:18
else: # 185:0-187:-1
    pass # 185:0-187:-1