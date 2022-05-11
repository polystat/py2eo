def else_while(): # 1:0-10:75
    n = 5  # 2:2-2:6
    res = [] # 3:2-3:9
    while ((n > 0  )): # 4:2-10:1
        n = (n - 1 ) # 5:4-5:12
        res.append(n) # 6:4-6:16
    else:
        res.append(6 ) # 8:4-8:16
    return ((res.__contains__(6 ) and (res == [4 , 3 , 2 , 1 , 0 , 6 ] )) and (len(res) == 6  )) # 10:2-10:75