def test(): # 1:0-5:44
    class A(): # 2:4-4:3
        a = 123  # 3:8-3:14
    x = A() # 4:4-4:10
    return (x.a.__class__ == int ) # 5:4-5:33