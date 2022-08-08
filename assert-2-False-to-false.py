def check(): # 1:0-14:29
    def avg(marks): # 2:4-5:3
        assert (len(marks) != 0  ), "List is empty." # 3:8-3:51
        return (sum(marks) / len(marks)) # 4:8-4:39
    result = false # 5:4-5:17
    try: # 6:4-14:3
        mark2 = [55 , 88 , 78 , 90 , 79 ] # 7:8-7:40
        avg(mark2) # 8:8-8:17
        mark1 = [] # 9:8-9:17
        avg(mark1) # 10:8-10:17
    except AssertionError as e:
        result = (str(e) == "List is empty." ) # 12:8-12:45

    return result # 14:4-14:16