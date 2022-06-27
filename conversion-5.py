def test(): # 1:0-6:50
    num_int = 123  # 2:4-2:16
    num_flo = 1.23 # 3:4-3:17
    num_int_2 = 13  # 4:4-4:17
    num_new = ((num_int * num_flo) - num_int_2) # 5:4-5:46
    return (num_new.__class__ == float ) # 6:4-6:39