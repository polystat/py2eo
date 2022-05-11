def myC(): # 1:0-13:35
    class c(): # 2:4-7:3
        def h(self): # 3:8-5:7
            return 1  # 4:12-4:19
        def g(self): # 5:8-7:3
            return self.h() # 6:12-6:26
    class d(c): # 7:4-12:3
        def h(self): # 8:8-10:7
            return 2  # 9:12-9:19
        def f(self): # 10:8-12:3
            return self.g() # 11:12-11:26
    o2 = d() # 12:4-12:10
    return (o.f() == 2  ) # 13:4-13:24