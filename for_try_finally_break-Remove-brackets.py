def for_try_finally_break: # 1:0-15:22
    res = [] # 2:2-2:9
    for num in [1 , 2 ]: # 3:2-15:1
        try: # 4:4-15:1
            result = (num // 0 ) # 5:6-5:22
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:6-7:47
            res.append(13 ) # 8:6-8:19
            break # 9:6-9:10
        else:
            print("Yeah ! Your answer is :", result) # 11:6-11:45
        finally:
            res.append(num) # 13:6-13:20

    return (res == [13 , 1 ] ) # 15:2-15:22