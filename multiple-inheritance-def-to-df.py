df multiInh(): # 1:0-8:-1
    class c(): # 2:2-4:1
        a = 1  # 3:4-3:8
    class d(): # 4:2-6:1
        b = 10  # 5:4-5:9
    class e(c, d): # 6:2-6:23
        pass # 6:17-6:20
    return ((c.a + d.b) == 11  ) # 7:2-7:23