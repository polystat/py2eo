def test(): # 1:0-5:46
    x = 5  # 2:4-2:8
    y = 3  # 3:4-3:8
    z = (x - complex(x, y)) # 4:4-4:26
    return (z.__class__ == complex ) # 5:4-5:35