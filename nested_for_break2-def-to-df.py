df nested_for_break2(): # 1:0-14:33
    arr1 = [1 , 2 , 3 ] # 2:2-2:17
    arr2 = [4 , 5 , 6 ] # 3:2-3:17
    res = [] # 4:2-4:9
    for i in arr1: # 6:2-14:1
        for j in arr2: # 7:4-12:3
            if ((j == 5  )): # 8:16-10:5 
                break # 9:8-9:12

            res.append(j) # 10:6-10:18

        res.append(i) # 12:4-12:16

    return (res == [4 , 1 , 4 , 2 , 4 , 3 ] ) # 14:2-14:33