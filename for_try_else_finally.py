def for_try_else_finally(): # 1:0-19:36
    res = [] # 2:2-2:9
    for num in [1 , 2 , 3 ]: # 3:2-19:1
        try: # 4:4-19:1
            result = (num // 0 ) # 5:6-5:22
        except ZeroDivisionError:
            print("Sorry ! You are dividing by zero ") # 7:6-7:47
            res.append(num) # 8:6-8:20
            continue # 9:6-9:13
        else:
            print("Yeah ! Your answer is :", result) # 11:6-11:45
            res.append(num) # 12:6-12:20
        finally:
            if ((len(res) == 2  )): # 14:23-17:5 
                break # 15:8-15:12

            res.append(12 ) # 17:6-17:19

    return (res == [1 , 12 , 2 , 12 , 3 , 12 ] ) # 19:2-19:36