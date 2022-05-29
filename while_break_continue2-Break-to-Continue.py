def continue_continue6(): # 1:0-13:57
    res = [] # 2:4-2:11
    n = 10  # 3:4-3:9
    while ((n > 0  )): # 4:4-13:3
        n-=1  # 5:8-5:11
        if ((n == 5  )): # 6:36-8:7 
            continue # 7:12-7:19
        elif ((n == 2  )): # 8:39-11:7 
            continue # 9:12-9:16

        res.append(n) # 11:8-11:20

    return (res == [9 , 8 , 7 , 6 , 4 , 3 ] ) # 13:4-13:44