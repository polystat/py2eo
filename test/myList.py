def myLi():
  class C:
      head = None
      tail = None
  
  def co(head, tail):
    c = C()
    c.head = head
    c.tail = tail
    return c

  lst = None
  lst = co(1, lst)
  lst = co(2, lst)

  return lst.tail.head == 1

