def test(): # 1:0-6:30
    x = 5  # 2:2-2:6
    y = 3  # 3:2-3:6
    z = (x - complex(x, y)) # 5:2-5:22
    return (z.__class__ == complex ) # 6:2-6:30