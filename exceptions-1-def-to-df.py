df te1(): # 1:0-9:-1
    class C(): # 2:2-2:17
        pass # 2:11-2:14
    try: # 3:2-7:24
        o = C() # 4:4-4:10
        raise o # 5:4-5:10
    except C:
        return True # 6:12-6:22
    except :
        return False # 7:10-7:21

    return False # 8:2-8:13