def test7_2_1(): # 1:0-12:82
    x = 0  # 2:4-2:8
    x-=1  # 3:4-3:7
    checkTargetIdent = (x == 1  ) # 4:4-4:32
    class c(): # 5:4-7:3
        attr = 1  # 6:8-6:15
    c.attr-=1  # 7:4-7:12
    checkTargetC = (c.attr == 1  ) # 8:4-8:33
    a = [0 , 1 ] # 9:4-9:15
    a[0 ]-=1  # 10:4-10:11
    checkTargetSubscr = (a[0 ] == 1  ) # 11:4-11:37
    return ((checkTargetIdent and checkTargetC) and checkTargetSubscr) # 12:4-12:69