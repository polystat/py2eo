def nfbce(): # 1:0-20:26
    res = False # 2:4-2:14
    l1 = [1 , 2 , 3 ] # 3:4-3:20
    l2 = [10 , 20 , 30 ] # 4:4-4:23
    l3 = [100 , 200 , 300 ] # 5:4-5:26
    for i in l1: # 6:4-20:3
        for j in l2: # 7:8-18:7
            for k in l3: # 8:12-15:11
                if ((((i == 2  ) and (j == 20  )) and (k == 200  ))): # 9:83-13:11 
                    res = True # 10:20-10:29
                    break # 11:20-11:24

            else:
                continue # 14:16-14:23
            break # 15:12-15:16
        else:
            continue # 17:12-17:19
        break # 18:8-18:12

    return res # 20:4-20:13