def nonLocal(): # 1:0-8:31
    x = 0  # 2:4-2:8
    def f(z): # 3:4-6:3
        nonlocal x # 4:8-4:17
        x-=z # 5:8-5:11
    f(1 ) # 6:4-6:8
    f(2 ) # 7:4-7:8
    return (x == 3  ) # 8:4-8:20