def for_continue_nested: # 1:0-14:53
    arr1 = [1 , 2 , 3 , 100 ] # 2:2-2:22
    arr2 = [4 , 5 , 6 ] # 3:2-3:17
    res = [] # 4:2-4:9
    for i in arr1: # 6:2-14:1
        for j in arr2: # 7:4-12:3
            if ((j == 5  )): # 8:16-10:5 
                continue # 9:8-9:15

            res.append(j) # 10:6-10:18

        res.append(i) # 12:4-12:16

    return (res == [4 , 6 , 1 , 4 , 6 , 2 , 4 , 6 , 3 , 4 , 6 , 100 ] ) # 14:2-14:53