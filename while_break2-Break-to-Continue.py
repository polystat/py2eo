def while_continue2(): # 1:0-9:31
    n = 5  # 2:4-2:8
    while ((n > 0  )): # 3:4-9:3
        n-=1  # 4:8-4:11
        if ((n == 2  )): # 5:36-9:3 
            continue # 6:12-6:16


    return (n == 2  ) # 9:4-9:20