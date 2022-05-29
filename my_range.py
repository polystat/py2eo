def myRange(): # 1:0-28:33
    class StopIteration(): # 2:4-4:3
        pass # 3:8-3:11
    class MyRange(): # 4:4-19:3
        i = 0  # 5:8-5:12
        last = 0  # 6:8-6:15
        def __init__(self, a, b): # 7:8-10:7
            self.i = a # 8:12-8:21
            self.last = b # 9:12-9:24
        def __iter__(self): # 10:8-12:7
            return self # 11:12-11:22
        def __next__(self): # 12:8-19:3
            if ((self.i == self.last )): # 13:54-15:11 
                raise StopIteration() # 14:16-14:36
            else: # 15:30-19:3
                ret = self.i # 16:16-16:27
                self.i = (self.i + 1 ) # 17:16-17:37
                return ret # 18:16-18:25
    r = MyRange(0 , 2 ) # 19:4-19:22
    z = 0  # 20:4-20:8
    try: # 21:4-28:3
        z = (z + r.__next__()) # 22:8-22:29
        z = (z + r.__next__()) # 23:8-23:29
        z = (z + r.__next__()) # 24:8-24:29
    except StopIteration:
        pass # 26:8-26:11

    return (z == 1  ) # 28:4-28:20