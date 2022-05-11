def test(): # 1:0-7:82
    a : int
    b : int
    c : int
    (a, b, c) = range(3 ) # 5:4-5:24
    print(a, b, c) # 6:4-6:17
    return ((len((a, b, c)) == 3  ) and ((a, b, c).__class__ is tuple )) # 7:4-7:71