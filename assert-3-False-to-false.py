def check(): # 1:0-9:27
    result = false # 2:4-2:17
    try: # 3:4-9:3
        x = 0  # 4:8-4:12
        assert (x < (-1 ) ) # 5:8-5:26
    except AssertionError:
        result = True # 7:8-7:20

    return result # 9:4-9:16