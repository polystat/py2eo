def test(): # 1:0-5:50
    num_int = 123  # 2:4-2:16
    num_flo = 1.23 # 3:4-3:17
    num_new = (num_int * num_flo) # 4:4-4:32
    return (num_new.__class__ == float ) # 5:4-5:39