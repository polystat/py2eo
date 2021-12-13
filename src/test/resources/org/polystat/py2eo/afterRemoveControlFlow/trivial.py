def testFib1():
    e1 = None
    f1 = None
    e3 = None
    e2 = None
    ii = None
    f2 = None
    e0 = None
    f0 = None
    bb_start0 = None
    bb_interm0 = None
    bb_interm1 = None
    bb_interm2 = None
    bb_body0 = None
    bb_interm4 = None
    bb_yes0 = None
    bb_no0 = None
    bb_interm5 = None
    bb_interm7 = None
    bb_interm6 = None
    bb_interm9 = None
    bb_interm8 = None
    bb_interm10 = None
    bb_else0 = None
    bb_interm3 = None
    bb_interm11 = None
    bb_finish0 = None
    def bb_start0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f0 = 0
        return bb_interm0()
    def bb_interm0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f1 = 1
        return bb_interm1()
    def bb_interm1():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        ii = 0
        return bb_interm2()
    def bb_interm2():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        if (True):
            return bb_body0()
        else:
            return bb_else0()
    def bb_body0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e2 = (ii < 10)
        return bb_interm4()
    def bb_interm4():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        if (not e2):
            return bb_yes0()
        else:
            return bb_no0()
    def bb_yes0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        return bb_interm3()
    def bb_no0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        pass
        return bb_interm5()
    def bb_interm5():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e0 = (f0 + f1)
        return bb_interm7()
    def bb_interm7():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f2 = e0
        return bb_interm6()
    def bb_interm6():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e1 = (ii + 1)
        return bb_interm9()
    def bb_interm9():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        ii = e1
        return bb_interm8()
    def bb_interm8():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f0 = f1
        return bb_interm10()
    def bb_interm10():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f1 = f2
        return bb_interm2()
    def bb_else0():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        pass
        return bb_interm3()
    def bb_interm3():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e3 = (55 == f0)
        return bb_interm11()
    def bb_interm11():
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        return e3
        return bb_finish0()
    def bb_finish0():
        return None
    return bb_start0()
assert testFib1()