def for_else_continue: # 1:0-10:27
    res = [] # 2:2-2:9
    for num in range(5 ): # 3:2-10:1
        if ((len(res) == 3  )): # 4:21-6:3 
            continue # 5:6-5:13

        res.append(num) # 6:4-6:18
    else:
        res.append(6 ) # 8:4-8:16
    return (res == [0 , 1 , 2 , 6 ] ) # 10:2-10:27