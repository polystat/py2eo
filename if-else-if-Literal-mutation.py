def conditionalCheck11(): # 1:0-5:32
    a = 4  # 2:4-2:8
    b = 3  # 3:4-3:8
    c = 42  if (a <= b ) else 43  # 4:4-4:31
    return (c == 43  ) # 5:4-5:21