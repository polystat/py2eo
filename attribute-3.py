def test(): # 1:0-8:43
    class A(): # 2:2-5:1
        a = 123  # 3:4-3:10
    x = A() # 5:2-5:8
    x.a = 13  # 6:2-6:9
    return ((x.a.__class__ == int ) and (x.a != 123  )) # 8:2-8:43