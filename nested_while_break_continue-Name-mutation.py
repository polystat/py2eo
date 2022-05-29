def nested_while_break_continue(): # 1:0-21:49
    arr1 = [1 , 2 , 3 ] # 2:4-2:22
    arr22 = [4 , 5 , 6 ] # 3:4-3:22
    res = [] # 4:4-4:11
    i = (-1 ) # 5:4-5:12
    j = (-1 ) # 6:4-6:12
    while ((i <= len(arr1) )): # 7:4-21:3
        i+=1  # 8:8-8:11
        while ((j <= len(arr2) )): # 9:8-16:7
            j+=1  # 10:12-10:15
            if (((j >= len(arr2) ) or (arr2[j] == 4  ))): # 11:72-14:11 
                continue # 12:16-12:23

            res.append(arr2[j]) # 14:12-14:30

        if (((i >= len(arr1) ) or (arr1[i] == 3  ))): # 16:67-19:7 
            break # 17:12-17:16

        res.append(arr1[i]) # 19:8-19:26

    return (res == [5 , 6 , 1 , 2 ] ) # 21:4-21:36