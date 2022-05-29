def nested_for_break_continue(): # 1:0-21:100
    flag = False # 2:4-2:15
    nums = [1 , 2 , 3 , 4 ] # 3:4-3:26
    nums2 = [1 , 4 , 9 , 16 ] # 4:4-4:28
    res = [] # 5:4-5:11
    for num in nums: # 6:4-21:3
        for num2 in nums2: # 7:8-17:7
            if (((num == 3  ) and (num2 == 9  ))): # 8:63-11:11 
                continue # 9:16-9:23

            res.append((num2 + num)) # 11:12-11:35
            if (((num == 7  ) and (num2 == 49  ))): # 12:65-17:7 
                flag = True # 13:16-13:26
                break # 14:16-14:20


        if (flag): # 17:32-21:3 
            break # 18:12-18:16


    return (res == [2 , 5 , 10 , 17 , 3 , 6 , 11 , 18 , 4 , 7 , 19 , 5 , 8 , 13 , 20 ] ) # 21:4-21:87