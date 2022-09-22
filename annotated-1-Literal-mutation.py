def test(): # 1:0-11:57
    class Cls(): # 2:4-5:3
        x = 111  # 3:8-3:14
        y = 12  # 4:8-4:13
    c = Cls() # 5:4-5:12
    c.x : int = 1 
    c.y : int
    d = {} # 8:4-8:9
    d['a'] : int = 1 
    d['b'] : int
    return ((d['a'] == 0  ) and (c.x == 0  )) # 11:4-11:44