def evaluationOrder(): # 1:0-10:83
    xlist = [] # 2:4-2:13
    def fst(): # 3:4-6:3
        xlist.append(1 ) # 4:8-4:23
        return 1  # 5:8-5:15
    def snd(): # 6:4-9:3
        xlist.append(2 ) # 7:8-7:23
        return 3  # 8:8-8:15
    b = (fst() + snd()) # 9:4-9:22
    return (((b == 3  ) and (xlist[0 ] == 1  )) and (xlist[1 ] == 2  )) # 10:4-10:70