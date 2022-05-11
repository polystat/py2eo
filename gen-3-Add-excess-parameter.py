def test(): # 1:0-4:39
    my_list = [10 , 6 , 6 , 10 ] # 2:4-2:31
    a = ((x ** 2 ) for x in my_list) # 3:4-3:35
    return (next(a) == 100  ) # 4:4-4:28