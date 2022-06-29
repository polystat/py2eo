def tryDoc(): # 1:0-9:33
    def f(a, b): # 2:4-8:3
        try: # 3:8-8:3
            return a # 4:12-4:19

        finally:
            return b # 7:12-7:19
    res = f(1 , 2 ) # 8:4-8:18
    return (res == 2  ) # 9:4-9:22