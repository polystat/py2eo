def test: # 1:0-11:29
    class emp: # 2:2-11:1
        def __init__(self): # 3:4-7:3
            self.name = 'xyz' # 4:6-4:22
            self.salary = 4000  # 5:6-5:23
        def show(self): # 7:4-11:1
            print(self.name) # 8:6-8:21
            print(self.salary) # 9:6-9:23
    return (len(vars(emp)) == 2  ) # 11:2-11:29