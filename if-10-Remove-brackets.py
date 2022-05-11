def conditionalCheck9: # 1:0-3:15
    c = 42  if True else 43  # 2:2-2:23
    return (c == 42  ) # 3:2-3:15