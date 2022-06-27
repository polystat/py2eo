def myRange(): # 1:0-26:33
    class MyRange(): # 2:4-17:3
        i = 0  # 3:8-3:12
        last = 0  # 4:8-4:15
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
    r = MyRange(2 , 0 ) # 17:4-17:22
    z = 0  # 18:4-18:8
    try: # 19:4-26:3
        z = (z + r.__next__()) # 20:8-20:29
        z = (z + r.__next__()) # 21:8-21:29
        z = (z + r.__next__()) # 22:8-22:29
    except StopIteration:
        pass # 24:8-24:11

    return (z == 1  ) # 26:4-26:20