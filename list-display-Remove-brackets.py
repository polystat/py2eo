def listDisplay: # 1:0-4:-1
    l = [(x * x) for x in range(1 , 6 ) if ((x % 2 ) == 1  )] # 2:2-2:49
    return (l == [1 , 9 , 25 ] ) # 3:2-3:23