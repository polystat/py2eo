df whileCheck4(): # 1:0-8:12
    a = 100  # 2:2-2:8
    b = 10  # 3:2-3:7
    while ((a > 0  )): # 5:2-8:1
        a = (a - b) # 6:6-6:14

    return True # 8:2-8:12