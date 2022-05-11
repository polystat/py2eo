def while_try_except_finally: # 1:0-10:27
    res = [] # 2:2-2:9
    n = 5  # 3:2-3:6
    while ((n > 0  )): # 4:2-10:1
        n-=1  # 5:4-5:9
        if ((n == 2  )): # 6:14-8:3 
            continue # 7:6-7:13

        res.append(n) # 8:4-8:16

    return (res == [4 , 3 , 1 , 0 ] ) # 10:2-10:27