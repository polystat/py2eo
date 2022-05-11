def arithmetics_integer: # 1:0-20:-1
    pow = ((10  ** 2 ) == 100  ) # 2:2-2:21
    uminus = ((-1 ) == (0  - 1 ) ) # 3:2-3:25
    uplus = (1  == (+1 ) ) # 4:2-4:18
    uneg = (((~1 ) & 7 ) == 6  ) # 5:2-5:21
    mul = ((10  * 10 ) == 100  ) # 6:2-6:23
    div = ((100  // 11 ) == 9  ) # 7:2-7:23
    rem = ((100  % 11 ) == 1  ) # 8:2-8:22
    add = ((1  + 10 ) == 11  ) # 9:2-9:21
    sub = ((10  - 1 ) == 9  ) # 10:2-10:20
    shl = ((1  << 1 ) == 2  ) # 11:2-11:20
    shr = ((3  >> 1 ) == 1  ) # 12:2-12:20
    andd = ((13  & 6 ) == 4  ) # 13:2-13:34
    orr = ((10  | 12 ) == 14  ) # 14:2-14:34
    xorr = ((2  ^ 3 ) == 1  ) # 15:2-15:24
    return (((((((((((((not ((not pow) or (not uminus))) and uplus) and uneg) and mul) and div) and rem) and add) and sub) and shl) and shr) and andd) and orr) and xorr) # 17:2-17:140