def test(): # 1:0-9:48
    class emp(): # 2:4-9:3
        def __init__(self): # 3:8-6:7
            self.name = 'xyz' # 4:12-4:28
            self.salary = 4000  # 5:12-5:29
        def show(self): # 6:8-9:3
            print(self.name) # 7:12-7:27
            print(self.salary, =abc) # 8:12-8:29
    return (len(vars(emp())) == 2  ) # 9:4-9:35