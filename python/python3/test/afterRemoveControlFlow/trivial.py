def testFib1():
    f1 = None
    f0 = None
    ii = None
    f2 = None
    def bb_start0():
        nonlocal f1, f0, ii, f2
        f0 = 0
        return bb_interm0()
    def bb_interm0():
        nonlocal f1, f0, ii, f2
        f1 = 1
        return bb_interm1()
    def bb_interm1():
        nonlocal f1, f0, ii, f2
        ii = 0
        return bb_interm2()
    def bb_interm2():
        nonlocal f1, f0, ii, f2
        if ((ii < 10 )):
            return bb_body0()
        else:
            return bb_else0()
    def bb_body0():
        nonlocal f1, f0, ii, f2
        f2 = (f0 + f1)
        return bb_interm4()
    def bb_interm4():
        nonlocal f1, f0, ii, f2
        ii = (ii + 1)
        return bb_interm5()
    def bb_interm5():
        nonlocal f1, f0, ii, f2
        f0 = f1
        return bb_interm6()
    def bb_interm6():
        nonlocal f1, f0, ii, f2
        f1 = f2
        return bb_interm2()
    def bb_else0():
        nonlocal f1, f0, ii, f2
        pass
        return bb_interm3()
    def bb_interm3():
        nonlocal f1, f0, ii, f2
        return (55 == f0 )
        return bb_finish0()
    def bb_finish0():
        return None
    return bb_start0()
assert testFib1()