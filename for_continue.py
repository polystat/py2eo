def for_else_continue(): # 1:0-9:56
    res = [] # 2:4-2:11
    for val in "string": # 3:4-9:3
        if ((val == "i" )): # 4:39-7:7 
            continue # 5:12-5:19

        res.append(val) # 7:8-7:22

    return (res == ['s', 't', 'r', 'n', 'g'] ) # 9:4-9:45