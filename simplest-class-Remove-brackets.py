def myC: # 1:0-10:-1
    class c: # 2:4-6:3
        value = None # 3:8-3:19
        def getValue(self): # 4:8-5:4
            return self.value # 4:28-4:44
    o = c # 6:4-6:10
    o.value = 11  # 7:4-7:15
    return (o.getValue == 11  ) # 9:4-9:28