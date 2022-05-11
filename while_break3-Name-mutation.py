def while_break3(): # 1:0-12:40
    nums = [1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 ] # 2:4-2:46
    i2 = 0  # 3:4-3:8
    while ((len(nums) > 0  )): # 4:4-12:3
        if ((nums[i] == 6  )): # 5:43-10:7 
            nums.clear() # 6:12-6:23
            nums.append(42 ) # 7:12-7:27
            break # 8:12-8:16

        i+=1  # 10:8-10:11

    return (nums == [42 ,] ) # 12:4-12:27