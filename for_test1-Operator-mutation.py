def forTest1(): # 1:0-7:34
    sum = 0  # 2:4-2:10
    for i in range(1 , 5 ): # 3:4-7:3
        sum-=i # 4:8-4:13
    else:
        sum-=1  # 6:8-6:13
    return (sum == 11  ) # 7:4-7:23