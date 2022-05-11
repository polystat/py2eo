def for_try_else_continue_break(): # 1:0-18:61
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:4-2:72
    res = [] # 3:4-3:11
    for item in items: # 4:4-18:3
        try: # 5:8-16:3
            if ((item == "hello" )): # 6:48-9:11 
                continue # 7:16-7:23

            if ((item == 100  )): # 9:46-12:11 
                break # 10:16-10:20

            res.append(int(item)) # 12:12-12:32
        except :
            res.append(900 , =abc) # 14:12-14:27

    else:
        res.append(800 ) # 17:8-17:23
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 ] ) # 18:4-18:48