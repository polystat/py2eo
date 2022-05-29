def for_continue_else(): # 1:0-10:45
    res = [] # 2:4-2:11
    for num in range(5 ): # 3:4-10:3
        res.append(num) # 4:8-4:22
        if ((len(res) == 3  )): # 5:43-8:3 
            continue # 6:12-6:16

    else:
        res.append(6 ) # 9:8-9:21
    return (res == [0 , 1 , 2 ] ) # 10:4-10:32