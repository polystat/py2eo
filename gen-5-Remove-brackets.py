def test: # 1:0-5:35
    my_list = [10 , 3 , 6 , 10 ] # 2:2-2:25
    generator = ((x ** 2 ) for x in my_list) # 3:2-3:36
    return (generator.__next__ == 100  ) # 5:2-5:35