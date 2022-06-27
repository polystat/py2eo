def for_try_except_break_finally(): # 1:0-14:38
    res = [] # 2:4-2:11
    for num in [1 , 2 ]: # 3:4-14:3
        try: # 4:8-14:3
            result = (num // 0 ) # 5:12-5:31
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:12-7:53
            break # 8:12-8:16
        else:
            pass # 10:12-10:15
        finally:
            res.append(num) # 12:12-12:26

    return (res == [1 ,] ) # 14:4-14:25