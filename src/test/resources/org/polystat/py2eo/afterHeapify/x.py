from heapifyRuntime import *
theTest = indirHeap.new(0)
def tmpFun0(closure, /):
    return True
    return allFuns[valuesHeap.get(indirHeap.get(closure[1]))[0]](valuesHeap.get(indirHeap.get(closure[1])))
def tmpFun1(closure, /):
    return None
def tmpFun2(closure, /):
    bb_finish0 = indirHeap.new(0)
    bb_start0 = indirHeap.new(0)
    indirHeap.set(bb_start0, valuesHeap.new(None))
    indirHeap.set(bb_finish0, valuesHeap.new(None))
    nextClosure = [0, bb_finish0, bb_start0]
    indirHeap.set(bb_start0, valuesHeap.new(nextClosure))
    nextClosure = [1, bb_finish0, bb_start0]
    indirHeap.set(bb_finish0, valuesHeap.new(nextClosure))
    return allFuns[valuesHeap.get(indirHeap.get(bb_start0))[0]](valuesHeap.get(indirHeap.get(bb_start0)))
allFuns = [tmpFun0, tmpFun1, tmpFun2]
nextClosure = [2,]
indirHeap.set(theTest, valuesHeap.new(nextClosure))
assert allFuns[valuesHeap.get(indirHeap.get(theTest))[0]](None)