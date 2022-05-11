def test(): # 1:0-10:57
    class Cls(): # 2:4-4:3
        pass # 3:8-3:11
    c = Cls() # 4:4-4:12
    c.x : int = 0 
    c.y : int
    d = {} # 7:4-7:9
    d['a'] : int = 1 
    d['b'] : int
    return ((d['a'] == 0  ) and (c.x == 0  )) # 10:4-10:44