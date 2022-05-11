def forElse1: # 1:0-8:33
    res = [] # 2:2-2:9
    for num in range(5 ): # 3:2-8:1
        res.append(num) # 4:4-4:18
    else:
        res.append(6 ) # 6:4-6:16
    return (res == [0 , 1 , 2 , 3 , 4 , 6 ] ) # 8:2-8:33