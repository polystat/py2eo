from result import *
def tmpFun3(heap, closure, /):
    constHeap0 = heap
    bb_interm0Ptr = nextFreePtr(constHeap0)
    constHeap1 = append2heap(constHeap0, None)
    e0Ptr = nextFreePtr(constHeap1)
    constHeap2 = append2heap(constHeap1, None)
    bb_finish0Ptr = nextFreePtr(constHeap2)
    constHeap3 = append2heap(constHeap2, None)
    bb_start0Ptr = nextFreePtr(constHeap3)
    constHeap4 = append2heap(constHeap3, None)
    constHeap5 = immArrChangeValue(constHeap4, e0Ptr, None)
    def tmpFun0(heap, closure, /):
        constHeap5 = heap
        pass
        constHeap6 = immArrChangeValue(constHeap5, closure["e0"], (55 == 15))
        return constHeap6[closure["bb_interm0"]]["callme"](constHeap6, constHeap6[closure["bb_interm0"]])
    newClosure0 = {"callme" : tmpFun0, "bb_interm0" : bb_interm0Ptr, "e0" : e0Ptr, "bb_finish0" : bb_finish0Ptr, "bb_start0" : bb_start0Ptr}
    constHeap7 = immArrChangeValue(constHeap5, bb_start0Ptr, newClosure0)
    def tmpFun1(heap, closure, /):
        constHeap7 = heap
        pass
        return (constHeap7, constHeap7[closure["e0"]])
        return constHeap7[closure["bb_finish0"]]["callme"](constHeap7, constHeap7[closure["bb_finish0"]])
    newClosure1 = {"callme" : tmpFun1, "bb_interm0" : bb_interm0Ptr, "e0" : e0Ptr, "bb_finish0" : bb_finish0Ptr, "bb_start0" : bb_start0Ptr}
    constHeap8 = immArrChangeValue(constHeap7, bb_interm0Ptr, newClosure1)
    def tmpFun2(heap, closure, /):
        constHeap8 = heap
        return (constHeap8, None)
    newClosure2 = {"callme" : tmpFun2, "bb_interm0" : bb_interm0Ptr, "e0" : e0Ptr, "bb_finish0" : bb_finish0Ptr, "bb_start0" : bb_start0Ptr}
    constHeap9 = immArrChangeValue(constHeap8, bb_finish0Ptr, newClosure2)
    return constHeap9[bb_start0Ptr]["callme"](constHeap9, constHeap9[bb_start0Ptr])
newClosure3 = {"callme" : tmpFun3}
assert tmpFun3([], {})[1]