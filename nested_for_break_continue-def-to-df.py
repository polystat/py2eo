df nested_for_break_continue(): # 1:0-22:-1
    flag = False # 2:2-2:13
    nums = [1 , 2 , 3 , 4 ] # 3:2-3:20
    nums2 = [1 , 4 , 9 , 16 ] # 4:2-4:22
    res = [] # 5:2-5:9
    for num in nums: # 7:2-21:1
        for num2 in nums2: # 8:4-18:3
            if (((num == 3  ) and (num2 == 9  ))): # 9:32-12:5 
                continue # 10:8-10:15

            res.append((num2 + num)) # 12:6-12:27
            if (((num == 7  ) and (num2 == 49  ))): # 14:33-18:3 
                flag = True # 15:8-15:18
                break # 16:8-16:12


        if (flag): # 18:12-21:1 
            break # 19:6-19:10


    return (res == [2 , 5 , 10 , 17 , 3 , 6 , 11 , 18 , 4 , 7 , 19 , 5 , 8 , 13 , 20 ] ) # 21:2-21:67