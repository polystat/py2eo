def myLi: # 1:0-17:-1
    class c: # 2:2-6:1
        head = None # 3:6-3:16
        tail = None # 4:6-4:16
    def co(head, tail): # 6:2-12:1
        o = c # 7:4-7:10
        o.head = head # 8:4-8:16
        o.tail = tail # 9:4-9:16
        return o # 10:4-10:11
    lst = None # 12:2-12:11
    lst = co(1 , lst) # 13:2-13:17
    lst = co(2 , lst) # 14:2-14:17
    return (lst.tail.head == 1  ) # 16:2-16:26