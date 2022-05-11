df test(): # 1:0-7:-1
    a : int
    b : int
    c : int
    (a, b, c) = range(3 ) # 3:2-3:17
    print(a, b, c) # 4:2-4:13
    return ((len((a, b, c)) == 3  ) and ((a, b, c).__class__ is tuple )) # 6:2-6:60