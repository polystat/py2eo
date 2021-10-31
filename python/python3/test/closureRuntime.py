from collections import deque

# heap = []
def mkNew(heap, x):
    z = len(heap)
    heap.append(x)
    return z

def myPrint0(heap, closure, x):
    print(x)
    return (heap, None)

myPrint = ({}, myPrint0)