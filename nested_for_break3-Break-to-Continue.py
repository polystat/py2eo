def nested_for_continue3(): # 1:0-17:24
    arr1 = [1 , 2 , 3 ] # 2:2-2:17
    arr2 = [4 , 5 , 6 ] # 3:2-3:17
    res = [] # 4:2-4:9
    for i in arr1: # 6:2-17:1
        for j in arr2: # 7:4-12:3
            if ((j == 5  )): # 8:16-10:5 
                continue # 9:8-9:12

            res.append(j) # 10:6-10:18

        if ((i == 2  )): # 12:14-15:3 
            continue # 13:6-13:10

        res.append(i) # 15:4-15:16

    return (res == [4 , 1 , 4 ] ) # 17:2-17:24