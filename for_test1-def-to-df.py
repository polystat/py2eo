df forTest1(): # 1:0-8:-1
    sum = 0  # 2:2-2:8
    for i in range(1 , 5 ): # 3:2-4:18
        sum+=i # 3:24-3:31
    else:
        sum+=1  # 4:8-4:15
    return (sum == 11  ) # 5:2-5:17