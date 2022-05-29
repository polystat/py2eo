def for_try_continue_else_finally(): # 1:0-20:42
    res = [] # 2:4-2:11
    for num in [1 , 2 , 3 ]: # 3:4-20:3
        try: # 4:8-20:3
            result = (num // 0 ) # 5:12-5:31
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:12-7:53
            res.append(10 ) # 8:12-8:26
            continue # 9:12-9:19
        else:
            print("Yeah ! Your answer is :", result) # 11:12-11:51
            res.append(11 ) # 12:12-12:26
        finally:
            res.append(num) # 14:12-14:26
            if ((len(res) == 2  )): # 15:50-18:11 
                break # 16:16-16:20

            res.append(13 ) # 18:12-18:26

    return (res == [10 , 1 ] ) # 20:4-20:29