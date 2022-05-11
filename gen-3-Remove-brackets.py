def test: # 1:0-5:22
    my_list = [10 , 6 , 6 , 10 ] # 2:2-2:25
    a = ((x ** 2 ) for x in my_list) # 4:2-4:30
    return (next(a) == 100  ) # 5:2-5:22