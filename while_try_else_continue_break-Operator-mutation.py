def while_try_else_continue_break(): # 1:0-21:61
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:4-2:72
    res = [] # 3:4-3:11
    i = (-1 ) # 4:4-4:12
    while ((i <= len(items) )): # 5:4-21:3
        i-=1  # 6:8-6:11
        item = items[i] # 7:8-7:22
        try: # 8:8-19:3
            if ((item == "hello" )): # 9:49-12:11 
                continue # 10:16-10:23

            if ((item == 100  )): # 12:47-15:11 
                break # 13:16-13:20

            res.append(int(item)) # 15:12-15:32
        except :
            res.append(900 ) # 17:12-17:27

    else:
        res.append(800 ) # 20:8-20:23
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 ] ) # 21:4-21:48