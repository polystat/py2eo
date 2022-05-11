def for_try_else_continue1: # 1:0-12:51
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:2-2:59
    res = [] # 3:2-3:9
    for item in items: # 4:2-12:1
        try: # 5:4-9:1
            res.append(int(item)) # 6:6-6:26
        except :
            continue # 8:6-8:13

    else:
        res.remove(100 ) # 10:4-10:18
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 200 , 300 , 400 ] ) # 12:2-12:51