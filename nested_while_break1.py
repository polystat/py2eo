def nested_while_break1(): # 1:0-15:45
    i = 1  # 2:4-2:8
    j = 0  # 3:4-3:8
    res = [] # 4:4-4:11
    while ((i <= 4  )): # 5:4-15:3
        while ((j <= 3  )): # 6:8-13:7
            if ((j == 3  )): # 7:40-10:11 
                break # 8:16-8:20

            res.append((i * j)) # 10:12-10:30
            j+=1  # 11:12-11:15

        i+=1  # 13:8-13:11

    return (res == [0 , 1 , 2 ] ) # 15:4-15:32