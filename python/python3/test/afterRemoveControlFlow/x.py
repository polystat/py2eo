def theTest():
    def bb_start0():
        return True
        return bb_finish0()
    def bb_finish0():
        return None
    return bb_start0()
assert theTest()