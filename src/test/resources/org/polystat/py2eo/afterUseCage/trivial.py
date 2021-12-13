def tmpFuntestFib1(closure, /):
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
    def tmpFunbb_start0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f0 = 0
        return bb_interm0["callme"](bb_interm0)
    bb_start0 = {"callme" : tmpFunbb_start0}
    def tmpFunbb_interm0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f1 = 1
        return bb_interm1["callme"](bb_interm1)
    bb_interm0 = {"callme" : tmpFunbb_interm0}
    def tmpFunbb_interm1(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        ii = 0
        return bb_interm2["callme"](bb_interm2)
    bb_interm1 = {"callme" : tmpFunbb_interm1}
    def tmpFunbb_interm2(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        if (True):
            return bb_body0["callme"](bb_body0)
        else:
            return bb_else0["callme"](bb_else0)
    bb_interm2 = {"callme" : tmpFunbb_interm2}
    def tmpFunbb_body0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e2 = (ii < 10)
        return bb_interm4["callme"](bb_interm4)
    bb_body0 = {"callme" : tmpFunbb_body0}
    def tmpFunbb_interm4(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        if (not e2):
            return bb_yes0["callme"](bb_yes0)
        else:
            return bb_no0["callme"](bb_no0)
    bb_interm4 = {"callme" : tmpFunbb_interm4}
    def tmpFunbb_yes0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        return bb_interm3["callme"](bb_interm3)
    bb_yes0 = {"callme" : tmpFunbb_yes0}
    def tmpFunbb_no0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        pass
        return bb_interm5["callme"](bb_interm5)
    bb_no0 = {"callme" : tmpFunbb_no0}
    def tmpFunbb_interm5(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e0 = (f0 + f1)
        return bb_interm7["callme"](bb_interm7)
    bb_interm5 = {"callme" : tmpFunbb_interm5}
    def tmpFunbb_interm7(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f2 = e0
        return bb_interm6["callme"](bb_interm6)
    bb_interm7 = {"callme" : tmpFunbb_interm7}
    def tmpFunbb_interm6(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e1 = (ii + 1)
        return bb_interm9["callme"](bb_interm9)
    bb_interm6 = {"callme" : tmpFunbb_interm6}
    def tmpFunbb_interm9(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        ii = e1
        return bb_interm8["callme"](bb_interm8)
    bb_interm9 = {"callme" : tmpFunbb_interm9}
    def tmpFunbb_interm8(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f0 = f1
        return bb_interm10["callme"](bb_interm10)
    bb_interm8 = {"callme" : tmpFunbb_interm8}
    def tmpFunbb_interm10(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        f1 = f2
        return bb_interm2["callme"](bb_interm2)
    bb_interm10 = {"callme" : tmpFunbb_interm10}
    def tmpFunbb_else0(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        pass
        return bb_interm3["callme"](bb_interm3)
    bb_else0 = {"callme" : tmpFunbb_else0}
    def tmpFunbb_interm3(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        e3 = (55 == f0)
        return bb_interm11["callme"](bb_interm11)
    bb_interm3 = {"callme" : tmpFunbb_interm3}
    def tmpFunbb_interm11(closure, /):
        nonlocal e1, f1, e3, e2, ii, f2, e0, f0
        return e3
        return bb_finish0["callme"](bb_finish0)
    bb_interm11 = {"callme" : tmpFunbb_interm11}
    def tmpFunbb_finish0(closure, /):
        return None
    bb_finish0 = {"callme" : tmpFunbb_finish0}
    return bb_start0["callme"](bb_start0)
testFib1 = {"callme" : tmpFuntestFib1}
assert testFib1["callme"](testFib1)