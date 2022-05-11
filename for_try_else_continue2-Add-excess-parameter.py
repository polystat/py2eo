def for_try_else_continue2(): # 1:0-15:91
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:4-2:72
    res = [] # 3:4-3:11
    for item in items: # 4:4-15:3
        try: # 5:8-13:3
            if ((item == "hello" )): # 6:48-9:11 
                continue # 7:16-7:23

            res.append(int(item)) # 9:12-9:32
        except :
            res.append(900 , =abc) # 11:12-11:27

    else:
        res.append(800 ) # 14:8-14:23
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 100 , 200 , 300 , 400 , 800 ] ) # 15:4-15:78