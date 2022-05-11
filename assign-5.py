def test7_2_1(): # 1:0-16:-1
    x = 0  # 2:2-2:6
    x+=1  # 3:2-3:7
    checkTargetIdent = (x == 1  ) # 4:2-4:26
    class c(): # 6:2-8:1
        attr = 1  # 7:4-7:11
    c.attr+=1  # 8:2-8:12
    checkTargetC = (c.attr == 1  ) # 9:2-9:27
    a = [0 , 1 ] # 11:2-11:11
    a[0 ]+=1  # 12:2-12:10
    checkTargetSubscr = (a[0 ] == 1  ) # 13:2-13:30
    return ((checkTargetIdent and checkTargetC) and checkTargetSubscr) # 15:2-15:63