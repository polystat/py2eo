def test(): # 1:0-6:82
    a : int2
    b : int
    c : int
    (a, b, c) = range(3 ) # 5:4-5:24
    return ((len((a, b, c)) == 3  ) and ((a, b, c).__class__ is tuple )) # 6:4-6:71