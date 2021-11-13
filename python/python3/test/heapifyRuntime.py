
class CHeap:
  a = []
  def get(self, index):
    return self.a[index]

  def new(self, value):
    ptr = len(self.a)
    self.a.append(value)
    return ptr

theHeap = CHeap()

#ptr = theHeap.mkNew(11)
#print(theHeap.get(ptr))