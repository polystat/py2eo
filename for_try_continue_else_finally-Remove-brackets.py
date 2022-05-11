def for_try_continue_else_finally: # 1:0-19:22
    res = [] # 2:2-2:9
    for num in [1 , 2 , 3 ]: # 3:2-19:1
        try: # 4:4-19:1
            result = (num // 0 ) # 5:6-5:22
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:6-7:47
            res.append(10 ) # 8:6-8:19
            continue # 9:6-9:13
        else:
            print("Yeah ! Your answer is :", result) # 11:6-11:45
            res.append(11 ) # 12:6-12:19
        finally:
            res.append(num) # 14:6-14:20
            if ((len(res) == 2  )): # 15:23-17:5 
                break # 16:8-16:12

            res.append(13 ) # 17:6-17:19

    return (res == [10 , 1 ] ) # 19:2-19:22