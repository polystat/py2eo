from result import *
def tmpFun18(heap, closure, /):
    constHeap0 = heap
    bb_interm6Ptr = nextFreePtr(constHeap0)
    constHeap1 = append2heap(constHeap0, None)
    bb_else0Ptr = nextFreePtr(constHeap1)
    constHeap2 = append2heap(constHeap1, None)
    f1Ptr = nextFreePtr(constHeap2)
    constHeap3 = append2heap(constHeap2, None)
    bb_interm2Ptr = nextFreePtr(constHeap3)
    constHeap4 = append2heap(constHeap3, None)
    bb_interm3Ptr = nextFreePtr(constHeap4)
    constHeap5 = append2heap(constHeap4, None)
    e3Ptr = nextFreePtr(constHeap5)
    constHeap6 = append2heap(constHeap5, None)
    bb_interm4Ptr = nextFreePtr(constHeap6)
    constHeap7 = append2heap(constHeap6, None)
    bb_interm11Ptr = nextFreePtr(constHeap7)
    constHeap8 = append2heap(constHeap7, None)
    bb_finish0Ptr = nextFreePtr(constHeap8)
    constHeap9 = append2heap(constHeap8, None)
    bb_interm5Ptr = nextFreePtr(constHeap9)
    constHeap10 = append2heap(constHeap9, None)
    f2Ptr = nextFreePtr(constHeap10)
    constHeap11 = append2heap(constHeap10, None)
    bb_interm0Ptr = nextFreePtr(constHeap11)
    constHeap12 = append2heap(constHeap11, None)
    e1Ptr = nextFreePtr(constHeap12)
    constHeap13 = append2heap(constHeap12, None)
    bb_yes0Ptr = nextFreePtr(constHeap13)
    constHeap14 = append2heap(constHeap13, None)
    bb_interm9Ptr = nextFreePtr(constHeap14)
    constHeap15 = append2heap(constHeap14, None)
    bb_body0Ptr = nextFreePtr(constHeap15)
    constHeap16 = append2heap(constHeap15, None)
    bb_interm7Ptr = nextFreePtr(constHeap16)
    constHeap17 = append2heap(constHeap16, None)
    e0Ptr = nextFreePtr(constHeap17)
    constHeap18 = append2heap(constHeap17, None)
    f0Ptr = nextFreePtr(constHeap18)
    constHeap19 = append2heap(constHeap18, None)
    bb_no0Ptr = nextFreePtr(constHeap19)
    constHeap20 = append2heap(constHeap19, None)
    e2Ptr = nextFreePtr(constHeap20)
    constHeap21 = append2heap(constHeap20, None)
    bb_start0Ptr = nextFreePtr(constHeap21)
    constHeap22 = append2heap(constHeap21, None)
    bb_interm1Ptr = nextFreePtr(constHeap22)
    constHeap23 = append2heap(constHeap22, None)
    bb_interm10Ptr = nextFreePtr(constHeap23)
    constHeap24 = append2heap(constHeap23, None)
    iiPtr = nextFreePtr(constHeap24)
    constHeap25 = append2heap(constHeap24, None)
    bb_interm8Ptr = nextFreePtr(constHeap25)
    constHeap26 = append2heap(constHeap25, None)
    constHeap27 = immArrChangeValue(constHeap26, e1Ptr, None)
    constHeap28 = immArrChangeValue(constHeap27, f1Ptr, None)
    constHeap29 = immArrChangeValue(constHeap28, e3Ptr, None)
    constHeap30 = immArrChangeValue(constHeap29, e2Ptr, None)
    constHeap31 = immArrChangeValue(constHeap30, iiPtr, None)
    constHeap32 = immArrChangeValue(constHeap31, f2Ptr, None)
    constHeap33 = immArrChangeValue(constHeap32, e0Ptr, None)
    constHeap34 = immArrChangeValue(constHeap33, f0Ptr, None)
    def tmpFun0(heap, closure, /):
        constHeap34 = heap
        pass
        constHeap35 = immArrChangeValue(constHeap34, closure["f0"], 0)
        return constHeap35[closure["bb_interm0"]]["callme"](constHeap35, constHeap35[closure["bb_interm0"]])
    newClosure0 = {"callme" : tmpFun0, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap36 = immArrChangeValue(constHeap34, bb_start0Ptr, newClosure0)
    def tmpFun1(heap, closure, /):
        constHeap36 = heap
        pass
        constHeap37 = immArrChangeValue(constHeap36, closure["f1"], 1)
        return constHeap37[closure["bb_interm1"]]["callme"](constHeap37, constHeap37[closure["bb_interm1"]])
    newClosure1 = {"callme" : tmpFun1, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap38 = immArrChangeValue(constHeap36, bb_interm0Ptr, newClosure1)
    def tmpFun2(heap, closure, /):
        constHeap38 = heap
        pass
        constHeap39 = immArrChangeValue(constHeap38, closure["ii"], 0)
        return constHeap39[closure["bb_interm2"]]["callme"](constHeap39, constHeap39[closure["bb_interm2"]])
    newClosure2 = {"callme" : tmpFun2, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap40 = immArrChangeValue(constHeap38, bb_interm1Ptr, newClosure2)
    def tmpFun3(heap, closure, /):
        constHeap40 = heap
        pass
        if (True):
            return constHeap40[closure["bb_body0"]]["callme"](constHeap40, constHeap40[closure["bb_body0"]])
        else:
            return constHeap40[closure["bb_else0"]]["callme"](constHeap40, constHeap40[closure["bb_else0"]])
    newClosure3 = {"callme" : tmpFun3, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap41 = immArrChangeValue(constHeap40, bb_interm2Ptr, newClosure3)
    def tmpFun4(heap, closure, /):
        constHeap41 = heap
        pass
        constHeap42 = immArrChangeValue(constHeap41, closure["e2"], (constHeap41[closure["ii"]] < 10))
        return constHeap42[closure["bb_interm4"]]["callme"](constHeap42, constHeap42[closure["bb_interm4"]])
    newClosure4 = {"callme" : tmpFun4, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap43 = immArrChangeValue(constHeap41, bb_body0Ptr, newClosure4)
    def tmpFun5(heap, closure, /):
        constHeap43 = heap
        pass
        if (not constHeap43[closure["e2"]]):
            return constHeap43[closure["bb_yes0"]]["callme"](constHeap43, constHeap43[closure["bb_yes0"]])
        else:
            return constHeap43[closure["bb_no0"]]["callme"](constHeap43, constHeap43[closure["bb_no0"]])
    newClosure5 = {"callme" : tmpFun5, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap44 = immArrChangeValue(constHeap43, bb_interm4Ptr, newClosure5)
    def tmpFun6(heap, closure, /):
        constHeap44 = heap
        pass
        return constHeap44[closure["bb_interm3"]]["callme"](constHeap44, constHeap44[closure["bb_interm3"]])
    newClosure6 = {"callme" : tmpFun6, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap45 = immArrChangeValue(constHeap44, bb_yes0Ptr, newClosure6)
    def tmpFun7(heap, closure, /):
        constHeap45 = heap
        pass
        pass
        return constHeap45[closure["bb_interm5"]]["callme"](constHeap45, constHeap45[closure["bb_interm5"]])
    newClosure7 = {"callme" : tmpFun7, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap46 = immArrChangeValue(constHeap45, bb_no0Ptr, newClosure7)
    def tmpFun8(heap, closure, /):
        constHeap46 = heap
        pass
        constHeap47 = immArrChangeValue(constHeap46, closure["e0"], (constHeap46[closure["f0"]] + constHeap46[closure["f1"]]))
        return constHeap47[closure["bb_interm7"]]["callme"](constHeap47, constHeap47[closure["bb_interm7"]])
    newClosure8 = {"callme" : tmpFun8, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap48 = immArrChangeValue(constHeap46, bb_interm5Ptr, newClosure8)
    def tmpFun9(heap, closure, /):
        constHeap48 = heap
        pass
        constHeap49 = immArrChangeValue(constHeap48, closure["f2"], constHeap48[closure["e0"]])
        return constHeap49[closure["bb_interm6"]]["callme"](constHeap49, constHeap49[closure["bb_interm6"]])
    newClosure9 = {"callme" : tmpFun9, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap50 = immArrChangeValue(constHeap48, bb_interm7Ptr, newClosure9)
    def tmpFun10(heap, closure, /):
        constHeap50 = heap
        pass
        constHeap51 = immArrChangeValue(constHeap50, closure["e1"], (constHeap50[closure["ii"]] + 1))
        return constHeap51[closure["bb_interm9"]]["callme"](constHeap51, constHeap51[closure["bb_interm9"]])
    newClosure10 = {"callme" : tmpFun10, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap52 = immArrChangeValue(constHeap50, bb_interm6Ptr, newClosure10)
    def tmpFun11(heap, closure, /):
        constHeap52 = heap
        pass
        constHeap53 = immArrChangeValue(constHeap52, closure["ii"], constHeap52[closure["e1"]])
        return constHeap53[closure["bb_interm8"]]["callme"](constHeap53, constHeap53[closure["bb_interm8"]])
    newClosure11 = {"callme" : tmpFun11, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap54 = immArrChangeValue(constHeap52, bb_interm9Ptr, newClosure11)
    def tmpFun12(heap, closure, /):
        constHeap54 = heap
        pass
        constHeap55 = immArrChangeValue(constHeap54, closure["f0"], constHeap54[closure["f1"]])
        return constHeap55[closure["bb_interm10"]]["callme"](constHeap55, constHeap55[closure["bb_interm10"]])
    newClosure12 = {"callme" : tmpFun12, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap56 = immArrChangeValue(constHeap54, bb_interm8Ptr, newClosure12)
    def tmpFun13(heap, closure, /):
        constHeap56 = heap
        pass
        constHeap57 = immArrChangeValue(constHeap56, closure["f1"], constHeap56[closure["f2"]])
        return constHeap57[closure["bb_interm2"]]["callme"](constHeap57, constHeap57[closure["bb_interm2"]])
    newClosure13 = {"callme" : tmpFun13, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap58 = immArrChangeValue(constHeap56, bb_interm10Ptr, newClosure13)
    def tmpFun14(heap, closure, /):
        constHeap58 = heap
        pass
        pass
        return constHeap58[closure["bb_interm3"]]["callme"](constHeap58, constHeap58[closure["bb_interm3"]])
    newClosure14 = {"callme" : tmpFun14, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap59 = immArrChangeValue(constHeap58, bb_else0Ptr, newClosure14)
    def tmpFun15(heap, closure, /):
        constHeap59 = heap
        pass
        constHeap60 = immArrChangeValue(constHeap59, closure["e3"], (55 == constHeap59[closure["f0"]]))
        return constHeap60[closure["bb_interm11"]]["callme"](constHeap60, constHeap60[closure["bb_interm11"]])
    newClosure15 = {"callme" : tmpFun15, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap61 = immArrChangeValue(constHeap59, bb_interm3Ptr, newClosure15)
    def tmpFun16(heap, closure, /):
        constHeap61 = heap
        pass
        return (constHeap61, constHeap61[closure["e3"]])
        return constHeap61[closure["bb_finish0"]]["callme"](constHeap61, constHeap61[closure["bb_finish0"]])
    newClosure16 = {"callme" : tmpFun16, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap62 = immArrChangeValue(constHeap61, bb_interm11Ptr, newClosure16)
    def tmpFun17(heap, closure, /):
        constHeap62 = heap
        return (constHeap62, None)
    newClosure17 = {"callme" : tmpFun17, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap63 = immArrChangeValue(constHeap62, bb_finish0Ptr, newClosure17)
    return constHeap63[bb_start0Ptr]["callme"](constHeap63, constHeap63[bb_start0Ptr])
newClosure18 = {"callme" : tmpFun18}
assert tmpFun18([], {})[1]