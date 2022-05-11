def test(): # 1:0-13:1
    checkStr = 'The First Message' # 2:4-2:33
    def my_gen(): # 3:4-13:1
        n = 1  # 4:6-4:10
        (yield n) # 5:6-5:12
        n+=1  # 7:6-7:11
        (yield n) # 8:6-8:12
        n+=1  # 10:6-10:11
        (yield n) # 11:6-11:12
return (next(my_gen()) == 1  ) # 13:2-13:27