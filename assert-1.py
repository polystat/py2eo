def divide(x, y): # 1:0-5:-1
    assert (y != 0  ), 'Zero division' # 2:2-2:32
    return round((x / y), 2 ) # 3:2-3:21
def check(): # 5:0-8:12
    z = divide(21 , 3 ) # 6:2-6:17
    a = divide(21 , 0 ) # 7:2-7:17
    return True # 8:2-8:12