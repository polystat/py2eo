def while_try_except_else: # 1:0-14:26
    arr = [] # 2:2-2:9
    while (True): # 3:2-14:1
        try: # 4:4-14:1
            x = int(1 ) # 5:6-5:15
            print(x) # 6:6-6:13
        except ValueError:
            print("Oops!  That was not a valid number.  Try again...") # 8:6-8:63
        else:
            res = True # 10:6-10:15
            arr.append(1 ) # 11:6-11:18
            break # 12:6-12:10


    return (res and (arr == [1 ,] )) # 14:2-14:26