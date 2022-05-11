def test(): # 1:0-4:52
    my_list = [10 , 3 , 6 , 10 ] # 2:4-2:31
    generator2 = ((x ** 2 ) for x in my_list) # 3:4-3:43
    return (generator.__next__() == 100  ) # 4:4-4:41