def arithmetics_integer(): # 1:0-15:171
    pow = ((10  ** 2 ) == 100  ) # 2:4-2:31
    uminus2 = ((-1 ) == (0  - 1 ) ) # 3:4-3:33
    uplus = (1  == (+1 ) ) # 4:4-4:25
    mul = ((10  * 10 ) == 100  ) # 5:4-5:31
    div = ((100  // 11 ) == 9  ) # 6:4-6:31
    rem = ((100  % 11 ) == 1  ) # 7:4-7:30
    add = ((1  + 10 ) == 11  ) # 8:4-8:29
    sub = ((10  - 1 ) == 9  ) # 9:4-9:28
    shl = ((1  << 1 ) == 2  ) # 10:4-10:28
    shr = ((3  >> 1 ) == 1  ) # 11:4-11:28
    andd = ((13  & 6 ) == 4  ) # 12:4-12:29
    orr = ((10  | 12 ) == 14  ) # 13:4-13:30
    xorr = ((2  ^ 3 ) == 1  ) # 14:4-14:28
    return ((((((((((((not ((not pow) or (not uminus))) and uplus) and mul) and div) and rem) and add) and sub) and shl) and shr) and andd) and orr) and xorr) # 15:4-15:157