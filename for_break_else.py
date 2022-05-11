def for_break_else(): # 1:0-10:24
    res = [] # 2:2-2:9
    for num in range(5 ): # 3:2-10:1
        res.append(num) # 4:4-4:18
        if ((len(res) == 3  )): # 5:21-7:1 
            break # 6:6-6:10

    else:
        res.append(6 ) # 8:4-8:16
    return (res == [0 , 1 , 2 ] ) # 10:2-10:24