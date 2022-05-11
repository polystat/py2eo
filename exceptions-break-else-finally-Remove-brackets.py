def tebf: # 1:0-11:-1
    finallyHappened = False # 2:2-2:24
    elseHappened = False # 3:2-3:21
    while (True): # 4:2-10:1
        try: # 5:4-9:37
            break # 6:6-6:10
        except :
            pass # 7:12-7:15
        else:
            elseHappened = True # 8:10-8:28
        finally:
            finallyHappened = True # 9:13-9:34

    return (finallyHappened and (not elseHappened)) # 10:2-10:46