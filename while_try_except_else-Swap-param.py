def while_try_except_else(): # 1:0-15:48
    arr = [] # 2:4-2:11
    while (True): # 3:4-15:3
        try: # 4:8-15:3
            x = int(1 ) # 5:12-5:22
            print(x) # 6:12-6:19
        except ValueError:
            print("Oops!  That was not a valid number.  Try again...") # 8:12-8:69
        else:
            res = True # 10:12-10:21
            arr.append(1 ) # 11:12-11:25
            break # 12:12-12:16


    return (res and (arr == [1 ,] )) # 15:4-15:35