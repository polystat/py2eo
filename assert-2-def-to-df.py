df avg(marks): # 1:0-5:-1
    assert (len(marks) != 0  ), "List is empty." # 2:2-2:40
    return (sum(marks) / len(marks)) # 3:2-3:29
df check(): # 5:0-11:12
    mark2 = [55 , 88 , 78 , 90 , 79 ] # 6:2-6:25
    print("Average of mark2:", avg(mark2)) # 7:2-7:38
    mark1 = [] # 9:2-9:11
    print("Average of mark1:", avg(mark1)) # 10:2-10:38
    return True # 11:2-11:12