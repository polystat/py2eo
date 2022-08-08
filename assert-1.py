def check(): # 1:0-12:26
    def divide(x, y): # 2:4-5:3
        assert (y != 0  ), 'Zero division' # 3:8-3:41
        return (x / y) # 4:8-4:21
    res = False # 5:4-5:14
    k = divide(21 , 3 ) # 6:4-6:22
    try: # 7:4-12:3
        divide(21 , 0 ) # 8:8-8:22
    except AssertionError as e:
        res = True # 10:8-10:17

    return res # 12:4-12:13