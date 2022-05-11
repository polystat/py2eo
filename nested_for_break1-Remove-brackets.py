def nested_for_break1: # 1:0-13:53
    break_out_flag_inner = False # 2:2-2:29
    break_out_flag_outer = False # 3:2-3:29
    for i in range(5 ): # 4:2-13:1
        for j in range(5 ): # 5:4-9:3
            if (((j == 2  ) and (i == 0  ))): # 6:27-9:3 
                break_out_flag_inner = True # 7:8-7:34
                break # 8:8-8:12


        if (break_out_flag_inner): # 9:28-13:1 
            break_out_flag_outer = True # 10:6-10:32
            break # 11:6-11:10


    return (break_out_flag_inner and break_out_flag_outer) # 13:2-13:53