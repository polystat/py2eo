def while_break_continue1(): # 1:0-11:-1
    i = 0  # 2:2-2:6
    j = 0  # 3:2-3:6
    while (True): # 4:2-9:1
        i-=1  # 5:4-5:9
        if ((i == 3  )): # 6:15-6:24 
            break # 6:15-6:19

        if ((j == 2  )): # 7:15-7:27 
            continue # 7:15-7:22

        j-=1  # 8:4-8:9

    return ((i == 3  ) and (j == 2  )) # 9:2-9:29
assert whileBreakContinue() # 11:0-11:26