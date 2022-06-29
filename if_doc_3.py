def ifDocThree(): # 1:0-6:31
    a = 7  # 2:4-2:8
    b = 6  # 3:4-3:8
    x = a if (a < b ) else b # 4:4-4:27
    print(x) # 5:4-5:11
    return (x == 6  ) # 6:4-6:20