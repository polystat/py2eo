def for_try_else_continue_break(): # 1:0-18:36
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:2-2:59
    res = [] # 3:2-3:9
    for item in items: # 4:2-18:1
        try: # 5:4-15:1
            if ((item == "hello" )): # 6:25-9:5 
                continue # 7:8-7:15

            if ((item == 100  )): # 9:21-12:5 
                break # 10:8-10:12

            res.append(int(item)) # 12:6-12:26
        except :
            res.append(900 ) # 14:6-14:20

    else:
        res.append(800 ) # 16:4-16:18
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 ] ) # 18:2-18:36