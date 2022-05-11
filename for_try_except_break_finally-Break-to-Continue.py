def for_try_except_continue_finally(): # 1:0-14:18
    res = [] # 2:2-2:9
    for num in [1 , 2 ]: # 3:2-14:1
        try: # 4:4-14:1
            result = (num // 0 ) # 5:6-5:22
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:6-7:47
            continue # 8:6-8:10
        else:
            print("Yeah ! Your answer is :", result) # 10:6-10:45
        finally:
            res.append(num) # 12:6-12:20

    return (res == [1 ,] ) # 14:2-14:18