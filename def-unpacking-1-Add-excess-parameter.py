def check(): # 1:0-3:87
    (first, *unused, last) = [1 , 2 , 3 , 5 , 7 ] # 2:4-2:48
    return (((last == 7  ) and (first == 1  )) and (unused == [2 , 3 , 5 ] )) # 3:4-3:76