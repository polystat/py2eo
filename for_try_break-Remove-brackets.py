def for_try_break: # 1:0-13:25
    res = [] # 2:2-2:9
    arr = [1 , 2 , 4 , 0 , 8 ] # 3:2-3:22
    for num in arr: # 5:2-13:1
        try: # 6:4-13:1
            result = (16  // num) # 7:6-7:23
            res.append(result) # 8:6-8:23
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 10:6-10:47
            break # 11:6-11:10


    return (res == [16 , 8 , 4 ] ) # 13:2-13:25