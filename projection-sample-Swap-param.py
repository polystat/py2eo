def classCheck(): # 1:0-8:27
    class c(): # 2:4-4:3
        field = 1  # 3:8-3:16
    def test(): # 4:4-8:3
        o = c() # 5:8-5:14
        o.field = 2  # 6:8-6:18
        return (o.field == 2  ) # 7:8-7:30
    return test() # 8:4-8:16