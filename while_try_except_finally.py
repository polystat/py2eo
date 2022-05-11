def continue1(): # 1:0-15:-1
    i = 0  # 2:2-2:6
    res = [] # 3:2-3:9
    while ((i < 5  )): # 4:2-14:1
        try: # 5:4-14:1
            assert (i != 3  ) # 6:6-6:20
            res.append(i) # 7:6-7:18
        except :
            res.append(10 ) # 9:6-9:19
            continue # 10:6-10:13
        finally:
            i+=1  # 12:6-12:11

    return (res == [0 , 1 , 2 , 10 , 4 ] ) # 14:2-14:31