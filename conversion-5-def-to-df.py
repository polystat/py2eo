df test(): # 1:0-7:32
    num_int = 123  # 2:2-2:14
    num_flo = 1.23 # 3:2-3:15
    num_int_2 = 13  # 4:2-4:15
    num_new = ((num_int * num_flo) - num_int_2) # 6:2-6:40
    return (num_new.__class__ == int ) # 7:2-7:32