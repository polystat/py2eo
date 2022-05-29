def multiInh(): # 1:0-8:42
    class c(): # 2:4-4:3
        a = 1  # 3:8-3:12
    class d(): # 4:4-6:3
        b = 10  # 5:8-5:13
    class e(c, d): # 6:4-8:3
        pass # 7:8-7:11
    return ((c.a - d.b) == 11  ) # 8:4-8:31