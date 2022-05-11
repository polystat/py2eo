df myRange(): # 1:0-24:-1
    class StopIteration(): # 2:2-2:29
        pass # 2:23-2:26
    class MyRange(): # 3:2-18:1
        i = 0  # 4:4-4:8
        last = 0  # 5:4-5:11
        df __init__(self, a, b): # 6:4-9:3
            self.i = a # 7:6-7:15
            self.last = b # 8:6-8:18
        df __iter__(self): # 9:4-9:39
            return self # 9:24-9:34
        df __next__(self): # 10:4-18:1
            if ((self.i == self.last )): # 11:31-13:5 
                raise StopIteration() # 12:8-12:28
            else: # 13:11-18:1
                ret = self.i # 14:8-14:19
                self.i = (self.i + 1 ) # 15:8-15:26
                return ret # 16:8-16:17
    r = MyRange(0 , 3 ) # 18:2-18:18
    z = 0  # 19:2-19:6
    for i in r: # 20:2-20:25
        z = (z + i) # 20:14-20:22

    return (z == 3  ) # 21:2-21:14