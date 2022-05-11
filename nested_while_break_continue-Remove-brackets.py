def nested_while_break_continue: # 1:0-22:-1
    arr1 = [1 , 2 , 3 ] # 2:2-2:17
    arr2 = [4 , 5 , 6 ] # 3:2-3:17
    res = [] # 4:2-4:9
    i = (-1 ) # 5:2-5:7
    j = (-1 ) # 6:2-6:7
    while ((i <= len(arr1) )): # 8:2-21:1
        i+=1  # 9:4-9:9
        while ((j <= len(arr2) )): # 10:4-16:3
            j+=1  # 11:6-11:11
            if (((j >= len(arr2) ) or (arr2[j] == 4  ))): # 12:40-14:5 
                continue # 13:8-13:15

            res.append(arr2[j]) # 14:6-14:24

        if (((i >= len(arr1) ) or (arr1[i] == 3  ))): # 16:38-19:3 
            break # 17:6-17:10

        res.append(arr1[i]) # 19:4-19:22

    return (res == [5 , 6 , 1 , 2 ] ) # 21:2-21:27