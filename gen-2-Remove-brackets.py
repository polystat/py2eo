def test: # 1:0-11:43
    checkStr = 'Hello World!!!' # 2:2-2:28
    def rev_str: # 4:2-9:1
        length = len(checkStr) # 5:4-5:25
        for i in range((length - 1 ), (-1 ), (-1 )): # 6:4-9:1
            (yield checkStr[i]) # 7:6-7:22

    resList = [] # 9:2-9:13
    [resList.append(i) for i in rev_str] # 10:2-10:39
    return (len(resList) == len(list(checkStr)) ) # 11:2-11:43