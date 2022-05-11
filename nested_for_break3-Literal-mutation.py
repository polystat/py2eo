def nested_for_break3(): # 1:0-17:45
    arr1 = [1 , 2 , 3 ] # 2:4-2:22
    arr2 = [4 , 5 , 6 ] # 3:4-3:22
    res = [] # 4:4-4:11
    for i in arr1: # 5:4-17:3
        for j in arr2: # 6:8-12:7
            if ((j == 5  )): # 7:41-10:11 
                break # 8:16-8:20

            res.append(j) # 10:12-10:24

        if ((i == 2  )): # 12:38-15:7 
            break # 13:12-13:16

        res.append(i) # 15:8-15:20

    return (res == [4 , 1 , 4 ] ) # 17:4-17:32