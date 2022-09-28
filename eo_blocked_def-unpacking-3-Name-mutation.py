def test(): # 1:0-8:59
    def product(*args): # 2:4-8:3
        result = 1  # 3:8-3:17
        for i2 in args: # 4:8-7:7
            result*=i # 5:12-5:20

        return result # 7:8-7:20
    return (product(*[12 , 1 , 3 , 4 ]) == 144  ) # 8:4-8:48