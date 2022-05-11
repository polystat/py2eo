def test: # 1:0-10:34
    class A: # 2:2-5:1
        a = 123  # 3:4-3:10
    x = A # 5:2-5:8
    x.a = 13  # 6:2-6:9
    dict = {'name' : 'John', 1  : x} # 8:2-8:30
    return (dict.get(1 ).__class__ == A ) # 10:2-10:34