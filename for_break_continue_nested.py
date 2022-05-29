def for_break_continue_nested(): # 1:0-17:65
    arr1 = [1 , 2 , 3 ] # 2:4-2:22
    arr2 = [4 , 5 , 6 ] # 3:4-3:22
    res = [] # 4:4-4:11
    for i in arr1: # 5:4-17:3
        for j in arr2: # 6:8-12:7
            if ((j == 5  )): # 7:41-10:11 
                continue # 8:16-8:23

            res.append(j) # 10:12-10:24

        if ((i == 2  )): # 12:38-15:7 
            continue # 13:12-13:19

        res.append(i) # 15:8-15:20

    return (res == [4 , 6 , 1 , 4 , 6 , 4 , 6 , 3 ] ) # 17:4-17:52