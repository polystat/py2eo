def xconditionalCheck9(): # 1:0-3:32
    xc = 42  if True else 43  # 2:4-2:26
    e0 = (xc == 42 ) # 3:12-3:18
    return e0 # 3:12-3:18