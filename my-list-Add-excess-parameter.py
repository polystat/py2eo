def myLi(): # 1:0-13:45
    class c(): # 2:4-5:3
        head = None # 3:8-3:18
        tail = None # 4:8-4:18
    def co(head, tail): # 5:4-10:3
        o = c() # 6:8-6:14
        o.head = head # 7:8-7:20
        o.tail = tail # 8:8-8:20
        return o # 9:8-9:15
    lst = None # 10:4-10:13
    lst = co(1 , lst, =abc) # 11:4-11:20
    lst = co(2 , lst) # 12:4-12:20
    return (lst.tail.head == 1  ) # 13:4-13:32