def for_try_else_continue2(): # 1:0-15:61
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:2-2:59
    res = [] # 3:2-3:9
    for item in items: # 4:2-15:1
        try: # 5:4-12:1
            if ((item == "hello" )): # 6:25-9:5 
                continue # 7:8-7:15

            res.append(int(item)) # 9:6-9:26
        except :
            res.append(900 ) # 11:6-11:20

    else:
        res.append(800 ) # 13:4-13:18
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 , 100 , 200 , 300 , 400 , 800 ] ) # 15:2-15:61