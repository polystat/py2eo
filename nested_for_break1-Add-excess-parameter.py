def nested_for_break1(): # 1:0-16:70
    break_out_flag_inner = False # 2:4-2:31
    break_out_flag_outer = False # 3:4-3:31
    for i in range(5 ): # 4:4-16:3
        for j in range(5 , =abc): # 5:8-11:7
            if (((j == 2  ) and (i == 0  ))): # 6:57-11:7 
                break_out_flag_inner = True # 7:16-7:42
                break # 8:16-8:20


        if (break_out_flag_inner): # 11:47-16:3 
            break_out_flag_outer = True # 12:12-12:38
            break # 13:12-13:16


    return (break_out_flag_inner and break_out_flag_outer) # 16:4-16:57