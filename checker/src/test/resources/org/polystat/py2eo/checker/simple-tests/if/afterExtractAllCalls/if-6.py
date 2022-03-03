def xconditionalCheck6(): # 1:0-7:30
    xa = 4  # 2:4-2:8
    xb2 = 2  # 3:4-3:9
    e0 = (xa >= xb) # 4:9-4:14
    if (e0): # 4:31-6:3 
        return True # 5:8-5:18
    else: # 6:20-7:30
        return False # 7:8-7:19