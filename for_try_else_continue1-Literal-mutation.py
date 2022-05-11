def for_try_else_continue1(): # 1:0-12:79
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:4-2:72
    res = [] # 3:4-3:11
    for item in items: # 4:4-12:3
        try: # 5:8-10:3
            res.append(int(item)) # 6:12-6:32
        except :
            continue # 8:12-8:19

    else:
        res.remove(100 ) # 11:8-11:23
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 200 , 300 , 400 ] ) # 12:4-12:66