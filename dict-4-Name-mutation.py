def test(): # 1:0-7:51
    class A(): # 2:4-4:3
        a = 123  # 3:8-3:14
    x2 = A() # 4:4-4:10
    x.a = 13  # 5:4-5:11
    dict = {'name' : 'John', 1  : x} # 6:4-6:35
    return (dict.get(1 ).__class__ == A ) # 7:4-7:40