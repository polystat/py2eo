def myListTest():
  class C:
      head = None
      tail = None
  
  def cons(head, tail):
    c = C();
    c.head = head
    c.tail = tail
    return c

  lst = None
  lst = cons(1, lst)
  lst = cons(2, lst)

  return lst.tail.head == 1

