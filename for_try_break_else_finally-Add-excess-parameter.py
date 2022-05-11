def for_try_break_else_finally(): # 1:0-15:41
    res = [] # 2:4-2:11
    for num in [1 , 2 ]: # 3:4-15:3
        try: # 4:8-15:3
            result = (num // 0 ) # 5:12-5:31
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:12-7:53
            res.append(3 , =abc) # 8:12-8:25
            break # 9:12-9:16
        else:
            print("Yeah ! Your answer is :", result) # 11:12-11:51
        finally:
            res.append(num) # 13:12-13:26

    return (res == [3 , 1 ] ) # 15:4-15:28