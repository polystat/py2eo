def myC(): # 1:0-9:43
    class c(): # 2:4-6:3
        value = 11  # 3:8-3:17
        def getValue(self): # 4:8-6:3
            return self.value # 5:12-5:28
    class d(c2): # 6:4-8:3
        pass # 7:8-7:11
    o = d() # 8:4-8:10
    return (o.getValue() == 11  ) # 9:4-9:32