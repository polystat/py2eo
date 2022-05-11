def listGeneratorDisplay(): # 1:0-3:42
    l = [z for z in ((x * x) for x in range(1 , 6 ) if ((x % 2 ) == 1  ))] # 2:4-2:73
    return (l == [1 , 9 , 25 ] ) # 3:4-3:31