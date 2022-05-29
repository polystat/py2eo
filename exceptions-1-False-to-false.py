def te1(): # 1:0-12:26
    class C(): # 2:4-4:3
        pass # 3:8-3:11
    try: # 4:4-12:3
        o = C() # 5:8-5:14
        raise o # 6:8-6:14
    except C:
        return True # 8:8-8:18
    except :
        return false # 10:8-10:19

    return false # 12:4-12:15