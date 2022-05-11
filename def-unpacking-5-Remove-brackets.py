def test: # 1:0-5:27
    def mySum(*args): # 2:2-5:1
        return sum(args) # 3:4-3:19
    return (mySum(10 , 20 ) == 30  ) # 5:2-5:27