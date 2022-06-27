def for_try_else_finally(): # 1:0-19:60
    res = [] # 2:4-2:11
    for num in [1 , 2 , 3 ]: # 3:4-19:3
        try: # 4:8-19:3
            result = (num // 0 ) # 5:12-5:31
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:12-7:53
            res.append(num) # 8:12-8:26
            continue # 9:12-9:19
        else:
            pass # 11:12-11:15
            res.append(num) # 12:12-12:26
        finally:
            if ((len(res) == 2  )): # 14:50-17:11 
                break # 15:16-15:20

            res.append(12 ) # 17:12-17:26

    return (res == [1 , 12 , 2 , 12 , 3 , 12 ] ) # 19:4-19:47