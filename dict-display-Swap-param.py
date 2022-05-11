def setDisplay(): # 1:0-3:57
    l = {x : (x * x) for x in range(1 , 6 ) if ((x % 2 ) == 1  )} # 2:4-2:64
    return (l == {1  : 1 , 3  : 9 , 5  : 25 } ) # 3:4-3:46