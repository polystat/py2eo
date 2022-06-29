def ifDocTwo(): # 1:0-6:31
    x = 1  # 2:4-2:8
    if (True): # 3:26-6:3 
        x = 3  # 4:8-4:12

    return (x == 2  ) # 6:4-6:20