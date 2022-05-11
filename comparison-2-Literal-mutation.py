def test(): # 1:0-4:112
    x = 5.15 # 2:4-2:11
    y = 3  # 3:4-3:8
    return (((y.__class__ is int ) and (x.__class__ == float )) and (y.__class__ is not x.__class__ )) # 4:4-4:101