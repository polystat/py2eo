def test: # 1:0-9:38
    def product(*args): # 2:2-9:1
        result = 1  # 3:4-3:13
        for i in args: # 4:4-7:3
            result*=i # 5:6-5:16

        return result # 7:4-7:16
    return (product(*[12 , 1 , 3 , 4 ]) == 144  ) # 9:2-9:38