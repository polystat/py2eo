def test(): # 1:0-10:61
    checkStr = 'Hello World!!!' # 2:4-2:30
    def rev_str(): # 3:4-8:3
        length = len(checkStr) # 4:8-4:29
        for i in range((length - 1 ), (-1 ), (-1 )): # 5:8-8:3
            (yield checkStr[i]) # 6:12-6:30

    resList = [] # 8:4-8:15
    [resList.append(i) for i in rev_str()] # 9:4-9:41
    return (len(resList) == len(list(checkStr)) ) # 10:4-10:48