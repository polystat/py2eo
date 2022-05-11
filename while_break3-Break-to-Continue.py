def while_continue3(): # 1:0-13:-1
    nums = [1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 ] # 2:2-2:35
    i = 0  # 3:2-3:6
    while ((len(nums) > 0  )): # 5:2-12:1
        if ((nums[i] == 6  )): # 6:20-10:3 
            nums.clear() # 7:6-7:17
            nums.append(42 ) # 8:6-8:20
            continue # 9:6-9:10

        i+=1  # 10:4-10:9

    return (nums == [42 ,] ) # 12:2-12:20