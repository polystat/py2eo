def test(): # 1:0-12:70
    def fibonacci_numbers(nums): # 2:4-8:3
        (x, y) = (0 , 1 ) # 3:8-3:24
        for _ in range(nums): # 4:8-8:3
            (x, y) = (y, (x + y)) # 5:12-5:32
            (yield x) # 6:12-6:20

    def square(nums): # 8:4-12:3
        for num in nums: # 9:8-12:3
            (yield (num ** 2 )) # 10:12-10:30

    return (sum(square(fibonacci_numbers(10 ))) == 4895  ) # 12:4-12:57