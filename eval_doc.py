def evalDoc(): # 1:0-6:32
    def f(a, b): # 2:4-4:3
        return (a + b) # 3:8-3:21
    x = ((1  + 2 ) * f((3  + 4 ), 5 )) # 4:4-4:37
    print(x) # 5:4-5:11
    return (x == 36  ) # 6:4-6:21