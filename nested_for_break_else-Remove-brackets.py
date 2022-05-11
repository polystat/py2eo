def nested_for_break_else: # 1:0-13:25
    arr = [] # 2:2-2:9
    for i in range(5 ): # 3:2-13:1
        for j in range(5 ): # 4:4-11:3
            if (((j == 2  ) and (i == 0  ))): # 5:27-8:5 
                arr.append(11 ) # 6:8-6:21
                break # 7:8-7:12

            arr.append(j) # 8:6-8:18
        else:
            arr.append(10 ) # 10:6-10:19
        break # 11:4-11:8

    return (arr == [0 , 1 , 11 ] ) # 13:2-13:25