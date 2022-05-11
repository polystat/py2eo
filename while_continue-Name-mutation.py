def while_try_except_finally(): # 1:0-11:49
    res = [] # 2:4-2:11
    n2 = 5  # 3:4-3:8
    while ((n > 0  )): # 4:4-11:3
        n-=1  # 5:8-5:11
        if ((n == 2  )): # 6:36-9:7 
            continue # 7:12-7:19

        res.append(n) # 9:8-9:20

    return (res == [4 , 3 , 1 , 0 ] ) # 11:4-11:36