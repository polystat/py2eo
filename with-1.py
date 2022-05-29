def t1(): # 1:0-13:39
    value = 0  # 2:4-2:12
    class CM(): # 3:4-11:3
        def __enter__(self): # 4:8-8:7
            nonlocal value # 5:12-5:25
            value+=1  # 6:12-6:19
            return 10  # 7:12-7:20
        def __exit__(self, a, b, c): # 8:8-11:3
            nonlocal value # 9:12-9:25
            value+=100  # 10:12-10:21
    with CM() as z: #11:4-13:3
        value+=z # 12:8-12:15
    return (value == 111  ) # 13:4-13:26