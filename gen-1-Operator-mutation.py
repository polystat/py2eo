def test(): # 1:0-10:-1
    checkStr = 'The First Message' # 2:4-2:33
    def my_gen(): # 3:4-10:-1
        n = 1  # 4:8-4:12
        (yield n) # 5:8-5:16
        n-=1  # 6:8-6:11
        (yield n) # 7:8-7:16
        n-=1  # 8:8-8:11
        (yield n) # 9:8-9:16
return (next(my_gen()) == 1  ) # 10:0-10:29