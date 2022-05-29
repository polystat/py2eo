def while_break_else(): # 1:0-12:107
    n = 5  # 2:4-2:8
    res = [] # 3:4-3:11
    while ((n > 0  )): # 4:4-12:3
        n = (n - 1 ) # 5:8-5:19
        if ((n == 2  )): # 6:36-9:7 
            break # 7:12-7:16

        res.append(n) # 9:8-9:20
    else:
        res.append(6 ) # 11:8-11:21
    return (((not res.__contains__(6 )) and (res == [4 , 3 ] )) and (not res.__contains__(2 ))) # 12:4-12:94