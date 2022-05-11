def arithmetics_integer(): # 1:0-16:182
    pow = ((10  ** 2 ) == 100  ) # 2:4-2:31
    uminus = ((-1 ) == (0  - 1 ) ) # 3:4-3:33
    uplus = (1  == (+1 ) ) # 4:4-4:25
    uneg = (((~1 ) & 7 ) == 6  ) # 5:4-5:31
    mul = ((10  * 10 ) == 100  ) # 6:4-6:31
    div = ((100  // 11 ) == 9  ) # 7:4-7:31
    rem = ((100  % 11 ) == 1  ) # 8:4-8:30
    add = ((1  + 10 ) == 11  ) # 9:4-9:29
    sub = ((10  - 1 ) == 9  ) # 10:4-10:28
    shl = ((1  << 1 ) == 2  ) # 11:4-11:28
    shr = ((3  >> 1 ) == 1  ) # 12:4-12:28
    andd = ((13  & 6 ) == 4  ) # 13:4-13:29
    orr = ((10  | 12 ) == 14  ) # 14:4-14:30
    xorr = ((2  ^ 3 ) == 1  ) # 15:4-15:28
    return (((((((((((((not ((not pow) or (not uminus))) and uplus) and uneg) and mul) and div) and rem) and add) and sub) and shl) and shr) and andd) and orr) and xorr) # 16:4-16:168