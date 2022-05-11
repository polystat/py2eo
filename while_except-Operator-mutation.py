def while_except(): # 1:0-16:19
    res = [] # 2:2-2:9
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:2-3:22
    i = 0  # 4:2-4:6
    while (True): # 6:2-16:1
        try: # 7:4-16:1
            result = (16  // arr[i]) # 8:6-8:26
            res.append(result) # 9:6-9:23
            i-=1  # 10:6-10:11
        except ZeroDivisionError:
            res.clear() # 12:6-12:16
            res.append(42 ) # 13:6-13:19
            break # 14:6-14:10


    return (res == [42 ,] ) # 16:2-16:19