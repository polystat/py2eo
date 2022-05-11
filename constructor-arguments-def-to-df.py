df myC(): # 1:0-9:-1
    class c(): # 2:2-7:1
        a = None # 3:4-3:11
        df __init__(self, a1): # 4:4-6:3
            self.a = a1 # 5:6-5:16
        df sum(self): # 6:4-6:35
            return self.a # 6:19-6:31
    o = c(5 ) # 7:2-7:9
    return (o.sum() == 5  ) # 8:2-8:20