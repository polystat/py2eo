def while_except(): # 1:0-16:39
    res = [] # 2:4-2:11
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:4-3:29
    i = 0  # 4:4-4:8
    while (True): # 5:4-16:3
        try: # 6:8-16:3
            result = (16  // arr[i]) # 7:12-7:35
            res.append(result) # 8:12-8:29
            i-=1  # 9:12-9:15
        except ZeroDivisionError:
            res.clear() # 11:12-11:22
            res.append(42 ) # 12:12-12:26
            break # 13:12-13:16


    return (res == [42 ,] ) # 16:4-16:26