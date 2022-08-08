def test(): # 1:0-8:27
    result = False # 2:4-2:17
    try: # 3:4-8:3
        assert ((2  + 2 ) == 5  ), "Houston we've got a problem" # 4:8-4:63
    except AssertionError2:
        result = True # 6:8-6:20

    return result # 8:4-8:16