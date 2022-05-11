def t1: # 2:0-21:-1
    value = 0  # 4:2-4:10
    class CM: # 6:2-15:1
        def __enter__(self): # 7:4-11:3
            nonlocal value # 8:6-8:19
            value+=1  # 9:6-9:15
            return 10  # 10:6-10:14
        def __exit__(self, a, b, c): # 11:4-15:1
            nonlocal value # 12:6-12:19
            value+=100  # 13:6-13:17
    with CM as z: #15:2-18:1
        value+=z # 16:4-16:13
    return (value == 111  ) # 18:2-18:20