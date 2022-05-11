def tebf(): # 1:0-14:63
    finallyHappened = False # 2:4-2:26
    elseHappened2 = False # 3:4-3:23
    while (True): # 4:4-14:3
        try: # 5:8-14:3
            break # 6:12-6:16
        except :
            pass # 8:12-8:15
        else:
            elseHappened = True # 10:12-10:30
        finally:
            finallyHappened = True # 12:12-12:33

    return (finallyHappened and (not elseHappened)) # 14:4-14:50