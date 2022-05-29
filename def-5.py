def my_decorator(): # 1:0-7:45
    def factorial(n): # 2:4-7:3
        if ((n < 2  )): # 3:35-5:7 
            return 1  # 4:12-4:19
        else: # 5:23-7:3
            return (n * factorial((n - 1 ))) # 6:12-6:43
    return (factorial(5 ) == 120  ) # 7:4-7:34