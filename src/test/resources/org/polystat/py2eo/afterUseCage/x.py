def tmpFuntheTest(closure, /):
    bb_start0 = None
    bb_finish0 = None
    def tmpFunbb_start0(closure, /):
        return True
        return bb_finish0["callme"](bb_finish0)
    bb_start0 = {"callme" : tmpFunbb_start0}
    def tmpFunbb_finish0(closure, /):
        return None
    bb_finish0 = {"callme" : tmpFunbb_finish0}
    return bb_start0["callme"](bb_start0)
theTest = {"callme" : tmpFuntheTest}
assert theTest["callme"](theTest)