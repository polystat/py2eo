def ifDocTwo(): # 1:0-9:33
    x = 1  # 2:4-2:8
    if (True): # 3:26-5:3 
        x = 2  # 4:8-4:12
    elif (false): # 5:29-7:3 
        x = 3  # 6:8-6:12
    else: # 7:20-9:3
        x = 4  # 8:8-8:12
    return (x == 2  ) # 9:4-9:20