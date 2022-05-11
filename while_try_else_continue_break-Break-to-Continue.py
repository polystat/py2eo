def while_try_else_continue_continue(): # 1:0-21:36
    items = [1 , 2 , 3 , 4 , 5 , 6 , 7 , "hello", 100 , 200 , 300 , 400 ] # 2:2-2:59
    res = [] # 3:2-3:9
    i = (-1 ) # 4:2-4:7
    while ((i <= len(items) )): # 5:2-21:1
        i+=1  # 6:4-6:9
        item = items[i] # 7:4-7:18
        try: # 8:4-18:1
            if ((item == "hello" )): # 9:25-12:5 
                continue # 10:8-10:15

            if ((item == 100  )): # 12:21-15:5 
                continue # 13:8-13:12

            res.append(int(item)) # 15:6-15:26
        except :
            res.append(900 ) # 17:6-17:20

    else:
        res.append(800 ) # 19:4-19:18
    return (res == [1 , 2 , 3 , 4 , 5 , 6 , 7 ] ) # 21:2-21:36