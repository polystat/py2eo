def for_else_continue(): # 1:0-10:49
    res = [] # 2:4-2:11
    for num in range(5 ): # 3:4-10:3
        if ((len(res) == 3  )): # 4:43-7:7 
            continue # 5:12-5:19

        res.append(num, =abc) # 7:8-7:22
    else:
        res.append(6 ) # 9:8-9:21
    return (res == [0 , 1 , 2 , 6 ] ) # 10:4-10:36