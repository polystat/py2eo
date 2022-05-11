def myC(): # 1:0-8:43
    class c(): # 2:4-6:3
        value = None # 3:8-3:19
        def getValue(self): # 4:8-6:3
            return self.value # 5:12-5:28
    o = c() # 6:4-6:10
    o.value = 11  # 7:4-7:15
    return (o.getValue() == 11  ) # 8:4-8:32