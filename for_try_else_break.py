def for_try_else_break(): # 1:0-13:41
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 1 , 2 , 3 , 4 ] # 2:2-2:51
    res = [] # 3:2-3:9
    for item in items: # 4:2-13:1
        try: # 5:4-10:1
            res.append(int(item)) # 6:6-6:26
        except :
            res.append(100 ) # 8:6-8:20
            break # 9:6-9:10

    else:
        res.remove(100 ) # 11:4-11:18
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 100 ] ) # 13:2-13:41