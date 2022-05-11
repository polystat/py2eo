df test(): # 1:0-7:71
    x = 5.15 # 2:2-2:9
    y = 3.14 # 3:2-3:9
    z = (x / complex(x, y)) # 5:2-5:22
    return ((z.__class__ == complex ) and (z.imag.__class__ == z.real.__class__ )) # 7:2-7:71