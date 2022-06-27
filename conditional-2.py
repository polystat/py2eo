def cond2(): # 1:0-4:31
    b = 2  # 2:4-2:8
    a = (1  == b ) if "asd" else 1  # 3:4-3:33
    return (a == 1  ) # 4:4-4:20