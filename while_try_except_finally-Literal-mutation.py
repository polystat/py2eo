def continue1(): # 1:0-14:54
    i = 0  # 2:4-2:8
    res = [] # 3:4-3:11
    while ((i < 5  )): # 4:4-14:3
        try: # 5:8-14:3
            assert (i != 3  ) # 6:12-6:28
            res.append(i) # 7:12-7:24
        except :
            res.append(10 ) # 9:12-9:26
            continue # 10:12-10:19
        finally:
            i+=2  # 12:12-12:15

    return (res == [0 , 1 , 2 , 10 , 4 ] ) # 14:4-14:41