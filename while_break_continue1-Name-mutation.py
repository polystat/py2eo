def while_break_continue1(): # 1:0-15:-1
    i = 0  # 2:4-2:8
    j2 = 0  # 3:4-3:8
    while (True): # 4:4-14:3
        i+=1  # 5:8-5:11
        if ((i == 3  )): # 6:36-9:7 
            break # 7:12-7:16

        if ((j == 2  )): # 9:37-12:7 
            continue # 10:12-10:19

        j+=1  # 12:8-12:11

    return ((i == 3  ) and (j == 2  )) # 14:4-14:37
assert whileBreakContinue() # 15:0-15:26