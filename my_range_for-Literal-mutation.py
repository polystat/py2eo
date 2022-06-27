def myRange(): # 1:0-22:33
    class MyRange(): # 2:4-17:3
        i = 0  # 3:8-3:12
        last = 1  # 4:8-4:15
        def __init__(self, a, b): # 5:8-8:7
            self.i = a # 6:12-6:21
            self.last = b # 7:12-7:24
        def __iter__(self): # 8:8-10:7
            return self # 9:12-9:22
        def __next__(self): # 10:8-17:3
            if ((self.i == self.last )): # 11:55-13:11 
                raise StopIteration() # 12:16-12:36
            else: # 13:30-17:3
                ret = self.i # 14:16-14:27
                self.i = (self.i + 1 ) # 15:16-15:37
                return ret # 16:16-16:25
    r = MyRange(0 , 3 ) # 17:4-17:22
    z = 1  # 18:4-18:8
    for i in r: # 19:4-22:3
        z = (z + i) # 20:8-20:18

    return (z == 3  ) # 22:4-22:20