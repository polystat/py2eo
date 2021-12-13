from closureRuntime import *
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
    constHeap35 = immArrChangeValue(constHeap34, bb_start0Ptr, None)
    constHeap36 = immArrChangeValue(constHeap35, bb_interm0Ptr, None)
    constHeap37 = immArrChangeValue(constHeap36, bb_interm1Ptr, None)
    constHeap38 = immArrChangeValue(constHeap37, bb_interm2Ptr, None)
    constHeap39 = immArrChangeValue(constHeap38, bb_body0Ptr, None)
    constHeap40 = immArrChangeValue(constHeap39, bb_interm4Ptr, None)
    constHeap41 = immArrChangeValue(constHeap40, bb_yes0Ptr, None)
    constHeap42 = immArrChangeValue(constHeap41, bb_no0Ptr, None)
    constHeap43 = immArrChangeValue(constHeap42, bb_interm5Ptr, None)
    constHeap44 = immArrChangeValue(constHeap43, bb_interm7Ptr, None)
    constHeap45 = immArrChangeValue(constHeap44, bb_interm6Ptr, None)
    constHeap46 = immArrChangeValue(constHeap45, bb_interm9Ptr, None)
    constHeap47 = immArrChangeValue(constHeap46, bb_interm8Ptr, None)
    constHeap48 = immArrChangeValue(constHeap47, bb_interm10Ptr, None)
    constHeap49 = immArrChangeValue(constHeap48, bb_else0Ptr, None)
    constHeap50 = immArrChangeValue(constHeap49, bb_interm3Ptr, None)
    constHeap51 = immArrChangeValue(constHeap50, bb_interm11Ptr, None)
    constHeap52 = immArrChangeValue(constHeap51, bb_finish0Ptr, None)
    def tmpFun0(heap, closure, /):
        constHeap52 = heap
        pass
        constHeap53 = immArrChangeValue(constHeap52, closure["f0"], 0)
        return constHeap53[closure["bb_interm0"]]["callme"](constHeap53, constHeap53[closure["bb_interm0"]])
    newClosure0 = {"callme" : tmpFun0, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap54 = immArrChangeValue(constHeap52, bb_start0Ptr, newClosure0)
    def tmpFun1(heap, closure, /):
        constHeap54 = heap
        pass
        constHeap55 = immArrChangeValue(constHeap54, closure["f1"], 1)
        return constHeap55[closure["bb_interm1"]]["callme"](constHeap55, constHeap55[closure["bb_interm1"]])
    newClosure1 = {"callme" : tmpFun1, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap56 = immArrChangeValue(constHeap54, bb_interm0Ptr, newClosure1)
    def tmpFun2(heap, closure, /):
        constHeap56 = heap
        pass
        constHeap57 = immArrChangeValue(constHeap56, closure["ii"], 0)
        return constHeap57[closure["bb_interm2"]]["callme"](constHeap57, constHeap57[closure["bb_interm2"]])
    newClosure2 = {"callme" : tmpFun2, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap58 = immArrChangeValue(constHeap56, bb_interm1Ptr, newClosure2)
    def tmpFun3(heap, closure, /):
        constHeap58 = heap
        pass
        if (True):
            return constHeap58[closure["bb_body0"]]["callme"](constHeap58, constHeap58[closure["bb_body0"]])
        else:
            return constHeap58[closure["bb_else0"]]["callme"](constHeap58, constHeap58[closure["bb_else0"]])
    newClosure3 = {"callme" : tmpFun3, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap59 = immArrChangeValue(constHeap58, bb_interm2Ptr, newClosure3)
    def tmpFun4(heap, closure, /):
        constHeap59 = heap
        pass
        constHeap60 = immArrChangeValue(constHeap59, closure["e2"], (constHeap59[closure["ii"]] < 10))
        return constHeap60[closure["bb_interm4"]]["callme"](constHeap60, constHeap60[closure["bb_interm4"]])
    newClosure4 = {"callme" : tmpFun4, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap61 = immArrChangeValue(constHeap59, bb_body0Ptr, newClosure4)
    def tmpFun5(heap, closure, /):
        constHeap61 = heap
        pass
        if (not constHeap61[closure["e2"]]):
            return constHeap61[closure["bb_yes0"]]["callme"](constHeap61, constHeap61[closure["bb_yes0"]])
        else:
            return constHeap61[closure["bb_no0"]]["callme"](constHeap61, constHeap61[closure["bb_no0"]])
    newClosure5 = {"callme" : tmpFun5, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap62 = immArrChangeValue(constHeap61, bb_interm4Ptr, newClosure5)
    def tmpFun6(heap, closure, /):
        constHeap62 = heap
        pass
        return constHeap62[closure["bb_interm3"]]["callme"](constHeap62, constHeap62[closure["bb_interm3"]])
    newClosure6 = {"callme" : tmpFun6, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap63 = immArrChangeValue(constHeap62, bb_yes0Ptr, newClosure6)
    def tmpFun7(heap, closure, /):
        constHeap63 = heap
        pass
        pass
        return constHeap63[closure["bb_interm5"]]["callme"](constHeap63, constHeap63[closure["bb_interm5"]])
    newClosure7 = {"callme" : tmpFun7, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap64 = immArrChangeValue(constHeap63, bb_no0Ptr, newClosure7)
    def tmpFun8(heap, closure, /):
        constHeap64 = heap
        pass
        constHeap65 = immArrChangeValue(constHeap64, closure["e0"], (constHeap64[closure["f0"]] + constHeap64[closure["f1"]]))
        return constHeap65[closure["bb_interm7"]]["callme"](constHeap65, constHeap65[closure["bb_interm7"]])
    newClosure8 = {"callme" : tmpFun8, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap66 = immArrChangeValue(constHeap64, bb_interm5Ptr, newClosure8)
    def tmpFun9(heap, closure, /):
        constHeap66 = heap
        pass
        constHeap67 = immArrChangeValue(constHeap66, closure["f2"], constHeap66[closure["e0"]])
        return constHeap67[closure["bb_interm6"]]["callme"](constHeap67, constHeap67[closure["bb_interm6"]])
    newClosure9 = {"callme" : tmpFun9, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap68 = immArrChangeValue(constHeap66, bb_interm7Ptr, newClosure9)
    def tmpFun10(heap, closure, /):
        constHeap68 = heap
        pass
        constHeap69 = immArrChangeValue(constHeap68, closure["e1"], (constHeap68[closure["ii"]] + 1))
        return constHeap69[closure["bb_interm9"]]["callme"](constHeap69, constHeap69[closure["bb_interm9"]])
    newClosure10 = {"callme" : tmpFun10, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap70 = immArrChangeValue(constHeap68, bb_interm6Ptr, newClosure10)
    def tmpFun11(heap, closure, /):
        constHeap70 = heap
        pass
        constHeap71 = immArrChangeValue(constHeap70, closure["ii"], constHeap70[closure["e1"]])
        return constHeap71[closure["bb_interm8"]]["callme"](constHeap71, constHeap71[closure["bb_interm8"]])
    newClosure11 = {"callme" : tmpFun11, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap72 = immArrChangeValue(constHeap70, bb_interm9Ptr, newClosure11)
    def tmpFun12(heap, closure, /):
        constHeap72 = heap
        pass
        constHeap73 = immArrChangeValue(constHeap72, closure["f0"], constHeap72[closure["f1"]])
        return constHeap73[closure["bb_interm10"]]["callme"](constHeap73, constHeap73[closure["bb_interm10"]])
    newClosure12 = {"callme" : tmpFun12, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap74 = immArrChangeValue(constHeap72, bb_interm8Ptr, newClosure12)
    def tmpFun13(heap, closure, /):
        constHeap74 = heap
        pass
        constHeap75 = immArrChangeValue(constHeap74, closure["f1"], constHeap74[closure["f2"]])
        return constHeap75[closure["bb_interm2"]]["callme"](constHeap75, constHeap75[closure["bb_interm2"]])
    newClosure13 = {"callme" : tmpFun13, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap76 = immArrChangeValue(constHeap74, bb_interm10Ptr, newClosure13)
    def tmpFun14(heap, closure, /):
        constHeap76 = heap
        pass
        pass
        return constHeap76[closure["bb_interm3"]]["callme"](constHeap76, constHeap76[closure["bb_interm3"]])
    newClosure14 = {"callme" : tmpFun14, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap77 = immArrChangeValue(constHeap76, bb_else0Ptr, newClosure14)
    def tmpFun15(heap, closure, /):
        constHeap77 = heap
        pass
        constHeap78 = immArrChangeValue(constHeap77, closure["e3"], (55 == constHeap77[closure["f0"]]))
        return constHeap78[closure["bb_interm11"]]["callme"](constHeap78, constHeap78[closure["bb_interm11"]])
    newClosure15 = {"callme" : tmpFun15, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap79 = immArrChangeValue(constHeap77, bb_interm3Ptr, newClosure15)
    def tmpFun16(heap, closure, /):
        constHeap79 = heap
        pass
        return (constHeap79, constHeap79[closure["e3"]])
        return constHeap79[closure["bb_finish0"]]["callme"](constHeap79, constHeap79[closure["bb_finish0"]])
    newClosure16 = {"callme" : tmpFun16, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap80 = immArrChangeValue(constHeap79, bb_interm11Ptr, newClosure16)
    def tmpFun17(heap, closure, /):
        constHeap80 = heap
        return (constHeap80, None)
    newClosure17 = {"callme" : tmpFun17, "bb_interm6" : bb_interm6Ptr, "bb_else0" : bb_else0Ptr, "f1" : f1Ptr, "bb_interm2" : bb_interm2Ptr, "bb_interm3" : bb_interm3Ptr, "e3" : e3Ptr, "bb_interm4" : bb_interm4Ptr, "bb_interm11" : bb_interm11Ptr, "bb_finish0" : bb_finish0Ptr, "bb_interm5" : bb_interm5Ptr, "f2" : f2Ptr, "bb_interm0" : bb_interm0Ptr, "e1" : e1Ptr, "bb_yes0" : bb_yes0Ptr, "bb_interm9" : bb_interm9Ptr, "bb_body0" : bb_body0Ptr, "bb_interm7" : bb_interm7Ptr, "e0" : e0Ptr, "f0" : f0Ptr, "bb_no0" : bb_no0Ptr, "e2" : e2Ptr, "bb_start0" : bb_start0Ptr, "bb_interm1" : bb_interm1Ptr, "bb_interm10" : bb_interm10Ptr, "ii" : iiPtr, "bb_interm8" : bb_interm8Ptr}
    constHeap81 = immArrChangeValue(constHeap80, bb_finish0Ptr, newClosure17)
    return constHeap81[bb_start0Ptr]["callme"](constHeap81, constHeap81[bb_start0Ptr])
newClosure18 = {"callme" : tmpFun18}
assert tmpFun18([], {})[1]