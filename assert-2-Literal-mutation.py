def avg(marks): # 1:0-4:-1
    assert (len(marks) != 0  ), "List is empty." # 2:4-2:47
    return (sum(marks) / len(marks)) # 3:4-3:35
def check(): # 4:0-9:25
    mark2 = [55 , 88 , 78 , 90 , 79 ] # 5:4-5:36
    print("Average of mark2:", avg(mark2)) # 6:4-6:41
    mark1 = [] # 7:4-7:13
    print("Average of mark1:", avg(mark1)) # 8:4-8:41
    return True # 9:4-9:14