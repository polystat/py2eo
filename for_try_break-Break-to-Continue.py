def for_try_continue(): # 1:0-13:46
    res = [] # 2:4-2:11
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:4-3:29
    for num in arr: # 4:4-13:3
        try: # 5:8-13:3
            result = (16  // num) # 6:12-6:32
            res.append(result) # 7:12-7:29
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 9:12-9:53
            continue # 10:12-10:16


    return (res == [16 , 8 , 4 ] ) # 13:4-13:33