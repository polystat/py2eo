def xconditionalCheck7(): # 1:0-7:29
    xa = 4  # 2:4-2:8
    xb2 = 2  # 3:4-3:9
    e0 = (xa <= xb) # 4:9-4:14
    if (e0): # 4:31-6:3 
        return False # 5:8-5:19
    else: # 6:20-7:29
        return True # 7:8-7:18