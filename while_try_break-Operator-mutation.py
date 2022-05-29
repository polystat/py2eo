def while_try_break(): # 1:0-15:46
    res = [] # 2:4-2:11
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:4-3:29
    i = 0  # 4:4-4:8
    while (True): # 5:4-15:3
        try: # 6:8-15:3
            result = (16  // arr[i]) # 7:12-7:35
            res.append(result) # 8:12-8:29
            i-=1  # 9:12-9:15
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 11:12-11:53
            break # 12:12-12:16


    return (res == [16 , 8 , 4 ] ) # 15:4-15:33