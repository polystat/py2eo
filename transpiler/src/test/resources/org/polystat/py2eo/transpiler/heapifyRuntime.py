
class CHeap:
  a = []
  def get(self, index):
    return self.a[index]

  def set(self, index, value):
    self.a[index] = value

  def new(self, value):
    ptr = len(self.a)
    self.a.append(value)
    return ptr

valuesHeap = CHeap()
indirHeap  = CHeap()

#ptr = theHeap.mkNew(11)
#print(theHeap.get(ptr))