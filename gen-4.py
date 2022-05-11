def test(): # 1:0-12:50
    def fibonacci_numbers(nums): # 2:2-8:1
        (x, y) = (0 , 1 ) # 3:4-3:14
        for _ in range(nums): # 4:4-8:1
            (x, y) = (y, (x + y)) # 5:6-5:20
            (yield x) # 6:6-6:12

    def square(nums): # 8:2-12:1
        for num in nums: # 9:4-12:1
            (yield (num ** 2 )) # 10:6-10:19

    return (sum(square(fibonacci_numbers(10 ))) == 4895  ) # 12:2-12:50