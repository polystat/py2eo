def nested_for_break_else(): # 1:0-14:46
    arr = [] # 2:4-2:11
    for i2 in range(5 ): # 3:4-14:3
        for j in range(5 ): # 4:8-12:7
            if (((j == 2  ) and (i == 0  ))): # 5:57-9:11 
                arr.append(11 ) # 6:16-6:30
                break # 7:16-7:20

            arr.append(j) # 9:12-9:24
        else:
            arr.append(10 ) # 11:12-11:26
        break # 12:8-12:12

    return (arr == [0 , 1 , 11 ] ) # 14:4-14:33