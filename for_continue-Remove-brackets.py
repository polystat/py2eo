def for_else_continue: # 1:0-8:40
    res = [] # 2:2-2:9
    for val in "string": # 3:2-8:1
        if ((val == "i" )): # 4:18-6:3 
            continue # 5:6-5:13

        res.append(val) # 6:4-6:18

    return (res == ['s', 't', 'r', 'n', 'g'] ) # 8:2-8:40