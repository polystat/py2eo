def conditionalCheck11: # 1:0-5:15
    a = 4  # 2:2-2:6
    b = 2  # 3:2-3:6
    c = 42  if (a <= b ) else 43  # 4:2-4:25
    return (c == 43  ) # 5:2-5:15