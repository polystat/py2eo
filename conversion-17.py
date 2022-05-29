def test(): # 1:0-5:92
    x = 5  # 2:4-2:8
    y = 3.14 # 3:4-3:11
    z = (y * complex(x, y)) # 4:4-4:26
    return ((z.__class__ == complex ) and (z.imag.__class__ == z.real.__class__ )) # 5:4-5:81