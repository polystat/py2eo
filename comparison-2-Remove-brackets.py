def test: # 1:0-5:86
    x = 5.15 # 2:2-2:9
    y = 3  # 3:2-3:6
    return (((y.__class__ is int ) and (x.__class__ == float )) and (y.__class__ is not x.__class__ )) # 5:2-5:86