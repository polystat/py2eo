def for_continue_nested(): # 1:0-14:83
    arr1 = [1 , 2 , 3 , 100 ] # 2:4-2:28
    arr2 = [4 , 5 , 6 ] # 3:4-3:22
    res = [] # 4:4-4:11
    for i in arr1: # 5:4-14:3
        for j in arr2: # 6:8-12:7
            if ((j == 5  )): # 7:41-10:11 
                continue # 8:16-8:23

            res.append(j) # 10:12-10:24

        res.append(i) # 12:8-12:20

    return (res == [4 , 6 , 1 , 4 , 6 , 2 , 4 , 6 , 3 , 4 , 6 , 100 ] ) # 14:4-14:70