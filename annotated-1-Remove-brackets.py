def test: # 1:0-14:-1
    class Cls: # 2:2-5:1
        pass # 3:4-3:7
    c = Cls # 5:2-5:10
    c.x : int = 0 
    c.y : int
    d = {} # 9:2-9:7
    d['a'] : int = 0 
    d['b'] : int
    return ((d['a'] == 0  ) and (c.x == 0  )) # 13:2-13:32