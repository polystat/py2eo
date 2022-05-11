def test: # 1:0-6:34
    num_int = 123  # 2:2-2:14
    num_flo = 1.23 # 3:2-3:15
    num_new = (num_int - num_flo) # 5:2-5:28
    return (num_new.__class__ == float ) # 6:2-6:34