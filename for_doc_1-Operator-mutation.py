def forDocOne(): # 1:0-7:31
    x = 0  # 2:4-2:8
    for i in range(4 ): # 3:4-6:3
        x = (x - i) # 4:8-4:18

    print(x) # 6:4-6:11
    return (x == 6  ) # 7:4-7:20