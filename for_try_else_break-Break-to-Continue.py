def for_try_else_continue(): # 1:0-13:67
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 1 , 2 , 3 , 4 ] # 2:4-2:64
    res = [] # 3:4-3:11
    for item in items: # 4:4-13:3
        try: # 5:8-11:3
            res.append(int(item)) # 6:12-6:32
        except :
            res.append(100 ) # 8:12-8:27
            continue # 9:12-9:16

    else:
        res.remove(100 ) # 12:8-12:23
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 100 ] ) # 13:4-13:54