df lambda1(): # 1:0-5:-1
    my_list = [1 , 5 , 4 , 6 , 8 , 11 , 3 , 12 ] # 2:4-2:39
    new_list = list(filter((lambda x : ((x % 2 ) == 0  )), my_list)) # 3:4-3:58
    return (len(new_list) == 4  ) # 4:4-4:28