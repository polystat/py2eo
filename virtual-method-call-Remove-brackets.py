def myC: # 1:0-10:-1
    class c: # 2:2-5:1
        def h(self): # 3:4-3:29
            return 1  # 3:17-3:24
        def g(self): # 4:4-4:34
            return self.h # 4:17-4:31
    class d(c): # 5:2-8:1
        def h(self): # 6:4-6:29
            return 2  # 6:17-6:24
        def f(self): # 7:4-7:34
            return self.g # 7:17-7:31
    o = d # 8:2-8:8
    return (o.f == 2  ) # 9:2-9:18