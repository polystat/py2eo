def test(): # 1:0-4:46
    def mySum(*args): # 2:4-4:3
        return sum(args) # 3:8-3:23
    return (mySum(10 , 20 ) == 30  ) # 4:4-4:35