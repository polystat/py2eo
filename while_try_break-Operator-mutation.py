def while_try_break(): # 1:0-15:25
    res = [] # 2:2-2:9
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:2-3:22
    i = 0  # 4:2-4:6
    while (True): # 6:2-15:1
        try: # 7:4-15:1
            result = (16  // arr[i]) # 8:6-8:26
            res.append(result) # 9:6-9:23
            i-=1  # 10:6-10:11
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 12:6-12:47
            break # 13:6-13:10


    return (res == [16 , 8 , 4 ] ) # 15:2-15:25