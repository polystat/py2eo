def check(): # 1:0-3:56
    (first, *unused, last) = [1 , 2 , 3 , 5 , 7 ] # 2:2-2:39
    return (((last == 7  ) and (first == 1  )) and (unused == [2 , 3 , 5 ] )) # 3:2-3:56