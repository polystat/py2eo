df nested_for_break_continue_else(): # 1:0-20:11
    res = False # 2:2-2:12
    l1 = [1 , 2 , 3 ] # 3:2-3:15
    l2 = [10 , 20 , 30 ] # 4:2-4:18
    l3 = [100 , 200 , 300 ] # 5:2-5:21
    for i in l1: # 7:2-20:1
        for j in l2: # 8:4-18:3
            for k in l3: # 9:6-15:5
                if ((((i == 2  ) and (j == 20  )) and (k == 200  ))): # 10:43-13:5 
                    res = True # 11:10-11:19
                    break # 12:10-12:14

            else:
                continue # 14:8-14:15
            break # 15:6-15:10
        else:
            continue # 17:6-17:13
        break # 18:4-18:8

    return res # 20:2-20:11