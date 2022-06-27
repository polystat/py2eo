def for_try_finally_break(): # 1:0-15:42
    res = [] # 2:4-2:11
    for num2 in [1 , 2 ]: # 3:4-15:3
        try: # 4:8-15:3
            result = (num // 0 ) # 5:12-5:31
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:12-7:53
            res.append(13 ) # 8:12-8:26
            break # 9:12-9:16
        else:
            pass # 11:12-11:15
        finally:
            res.append(num) # 13:12-13:26

    return (res == [13 , 1 ] ) # 15:4-15:29