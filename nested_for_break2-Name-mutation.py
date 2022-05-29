def nested_for_break2(): # 1:0-14:57
    arr1 = [1 , 2 , 3 ] # 2:4-2:22
    arr22 = [4 , 5 , 6 ] # 3:4-3:22
    res = [] # 4:4-4:11
    for i in arr1: # 5:4-14:3
        for j in arr2: # 6:8-12:7
            if ((j == 5  )): # 7:42-10:11 
                break # 8:16-8:20

            res.append(j) # 10:12-10:24

        res.append(i) # 12:8-12:20

    return (res == [4 , 1 , 4 , 2 , 4 , 3 ] ) # 14:4-14:44