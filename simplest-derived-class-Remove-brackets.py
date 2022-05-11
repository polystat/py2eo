def myC: # 1:0-8:-1
    class c: # 2:4-5:3
        value = 11  # 3:8-3:17
        def getValue(self): # 4:8-4:49
            return self.value # 4:28-4:44
    class d(c): # 5:4-5:24
        pass # 5:16-5:19
    o = d # 6:4-6:10
    return (o.getValue == 11  ) # 7:4-7:28