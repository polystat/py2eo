df while_break_else(): # 1:0-12:77
    n = 5  # 2:2-2:6
    res = [] # 3:2-3:9
    while ((n > 0  )): # 4:2-12:1
        n = (n - 1 ) # 5:4-5:12
        if ((n == 2  )): # 6:14-8:3 
            break # 7:6-7:10

        res.append(n) # 8:4-8:16
    else:
        res.append(6 ) # 10:4-10:16
    return (((not res.__contains__(6 )) and (res == [4 , 3 ] )) and (not res.__contains__(2 ))) # 12:2-12:77