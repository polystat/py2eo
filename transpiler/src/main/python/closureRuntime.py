
from copy import copy

def nextFreePtr(heap):
    return len(heap)

def append2heap(heap, what):
    heap.append(what)
    return heap

def immArrChangeValue(heap, ptr, newValue):
    heap1 = copy(heap)
    heap1[ptr] = newValue
    return heap1

def myPrint0(heap, closure, x):
    print(x)
    return (heap, None)

myPrint = ({}, myPrint0)