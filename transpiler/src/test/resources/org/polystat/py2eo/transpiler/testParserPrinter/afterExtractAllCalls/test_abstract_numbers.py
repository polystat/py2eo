"Unit tests for numbers.py." # 1:0-1:31
import xmath # 3:7-3:10
import xoperator # 4:7-4:14
import xunittest # 5:7-5:14
from xnumbers import xComplex # 6:20-6:26
from xnumbers import xReal # 6:29-6:32
from xnumbers import xRational # 6:35-6:42
from xnumbers import xIntegral # 6:45-6:52
e83 = xunittest.xTestCase # 8:18-8:34
class xTestNumbers(e83): # 8:37-43:-1
    def xtest_int(xself): # 9:4-20:3
        e0 = xself.xassertTrue # 10:8-10:22
        e1 = xissubclass(xint, xIntegral) # 10:24-10:48
        e2 = e0(e1) # 10:8-10:49
        e3 = xself.xassertTrue # 11:8-11:22
        e4 = xissubclass(xint, xComplex) # 11:24-11:47
        e5 = e3(e4) # 11:8-11:48
        e6 = xself.xassertEqual # 13:8-13:23
        lhs3 = 7  # 13:25-13:25
        e7 = xint(7 ) # 13:28-13:33
        e8 = e7.xreal # 13:28-13:38
        e9 = e6(lhs3, e8) # 13:8-13:39
        e10 = xself.xassertEqual # 14:8-14:23
        lhs6 = 0  # 14:25-14:25
        e11 = xint(7 ) # 14:28-14:33
        e12 = e11.ximag # 14:28-14:38
        e13 = e10(lhs6, e12) # 14:8-14:39
        e14 = xself.xassertEqual # 15:8-15:23
        lhs10 = 7  # 15:25-15:25
        e15 = xint(7 ) # 15:28-15:33
        e16 = e15.xconjugate # 15:28-15:43
        e17 = e16() # 15:28-15:45
        e18 = e14(lhs10, e17) # 15:8-15:46
        e19 = xself.xassertEqual # 16:8-16:23
        e20 = (-7 ) # 16:25-16:26
        lhs12 = xint # 16:29-16:31
        e21 = (-7 ) # 16:33-16:34
        e22 = lhs12(e21) # 16:29-16:35
        e23 = e22.xconjugate # 16:29-16:45
        e24 = e23() # 16:29-16:47
        e25 = e19(e20, e24) # 16:8-16:48
        e26 = xself.xassertEqual # 17:8-17:23
        lhs18 = 7  # 17:25-17:25
        e27 = xint(7 ) # 17:28-17:33
        e28 = e27.xnumerator # 17:28-17:43
        e29 = e26(lhs18, e28) # 17:8-17:44
        e30 = xself.xassertEqual # 18:8-18:23
        lhs21 = 1  # 18:25-18:25
        e31 = xint(7 ) # 18:28-18:33
        e32 = e31.xdenominator # 18:28-18:45
        e33 = e30(lhs21, e32) # 18:8-18:46
    def xtest_float(xself): # 20:4-29:3
        e34 = xself.xassertFalse # 21:8-21:23
        e35 = xissubclass(xfloat, xRational) # 21:25-21:51
        e36 = e34(e35) # 21:8-21:52
        e37 = xself.xassertTrue # 22:8-22:22
        e38 = xissubclass(xfloat, xReal) # 22:24-22:46
        e39 = e37(e38) # 22:8-22:47
        e40 = xself.xassertEqual # 24:8-24:23
        lhs26 = 7.3 # 24:25-24:27
        e41 = xfloat(7.3) # 24:30-24:39
        e42 = e41.xreal # 24:30-24:44
        e43 = e40(lhs26, e42) # 24:8-24:45
        e44 = xself.xassertEqual # 25:8-25:23
        lhs29 = 0  # 25:25-25:25
        e45 = xfloat(7.3) # 25:28-25:37
        e46 = e45.ximag # 25:28-25:42
        e47 = e44(lhs29, e46) # 25:8-25:43
        e48 = xself.xassertEqual # 26:8-26:23
        lhs33 = 7.3 # 26:25-26:27
        e49 = xfloat(7.3) # 26:30-26:39
        e50 = e49.xconjugate # 26:30-26:49
        e51 = e50() # 26:30-26:51
        e52 = e48(lhs33, e51) # 26:8-26:52
        e53 = xself.xassertEqual # 27:8-27:23
        e54 = (-7.3) # 27:25-27:28
        lhs35 = xfloat # 27:31-27:35
        e55 = (-7.3) # 27:37-27:40
        e56 = lhs35(e55) # 27:31-27:41
        e57 = e56.xconjugate # 27:31-27:51
        e58 = e57() # 27:31-27:53
        e59 = e53(e54, e58) # 27:8-27:54
    def xtest_complex(xself): # 29:4-43:-1
        e60 = xself.xassertFalse # 30:8-30:23
        e61 = xissubclass(xcomplex, xReal) # 30:25-30:49
        e62 = e60(e61) # 30:8-30:50
        e63 = xself.xassertTrue # 31:8-31:22
        e64 = xissubclass(xcomplex, xComplex) # 31:24-31:51
        e65 = e63(e64) # 31:8-31:52
        e66 = xcomplex(3 , 2 ) # 33:17-33:29
        e67 = xcomplex(4 , 1 ) # 33:32-33:43
        lhs42 = (e66, e67) # 33:17-33:43
        (xc1, xc2) = lhs42 # 33:8-33:43
        e68 = xself.xassertRaises # 35:8-35:24
        lhs43 = xTypeError # 35:26-35:34
        e69 = xmath.xtrunc # 35:37-35:46
        lhs44 = xc1 # 35:49-35:50
        e70 = e68(lhs43, e69, lhs44) # 35:8-35:51
        e71 = xself.xassertRaises # 36:8-36:24
        lhs46 = xTypeError # 36:26-36:34
        e72 = xoperator.xmod # 36:37-36:48
        lhs47 = xc1 # 36:51-36:52
        lhs48 = xc2 # 36:55-36:56
        e73 = e71(lhs46, e72, lhs47, lhs48) # 36:8-36:57
        e74 = xself.xassertRaises # 37:8-37:24
        lhs50 = xTypeError # 37:26-37:34
        lhs51 = xdivmod # 37:37-37:42
        lhs52 = xc1 # 37:45-37:46
        lhs53 = xc2 # 37:49-37:50
        e75 = e74(lhs50, lhs51, lhs52, lhs53) # 37:8-37:51
        e76 = xself.xassertRaises # 38:8-38:24
        lhs55 = xTypeError # 38:26-38:34
        e77 = xoperator.xfloordiv # 38:37-38:53
        lhs56 = xc1 # 38:56-38:57
        lhs57 = xc2 # 38:60-38:61
        e78 = e76(lhs55, e77, lhs56, lhs57) # 38:8-38:62
        e79 = xself.xassertRaises # 39:8-39:24
        lhs59 = xTypeError # 39:26-39:34
        lhs60 = xfloat # 39:37-39:41
        lhs61 = xc1 # 39:44-39:45
        e80 = e79(lhs59, lhs60, lhs61) # 39:8-39:46
        e81 = xself.xassertRaises # 40:8-40:24
        lhs63 = xTypeError # 40:26-40:34
        lhs64 = xint # 40:37-40:39
        lhs65 = xc1 # 40:42-40:43
        e82 = e81(lhs63, lhs64, lhs65) # 40:8-40:44
e86 = (x__name__ == "__main__") # 43:3-43:24
if (e86): # 43:26-45:-1 
    e84 = xunittest.xmain # 44:4-44:16
    e85 = e84() # 44:4-44:18
else: # 43:0-45:-1
    pass # 43:0-45:-1