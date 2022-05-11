def lambda1(): # 1:0-4:43
    my_list = [1 , 5 , 4 , 6 , 8 , 11 , 3 , 12 ] # 2:4-2:47
    new_list2 = list(filter((lambda x : ((x % 2 ) == 0  )), my_list)) # 3:4-3:67
    return (len(new_list) == 4  ) # 4:4-4:32