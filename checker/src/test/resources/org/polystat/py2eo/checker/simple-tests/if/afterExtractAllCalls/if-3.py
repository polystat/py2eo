def xconditionalCheck3(): # 1:0-6:-1
    xa = 4  # 2:4-2:8
    xb2 = 2  # 3:4-3:9
    e0 = (xa != xb) # 4:9-4:14
    if (e0): # 4:32-6:-1 
        return True # 5:8-5:18
    else: # 4:4-6:-1
        pass # 4:4-6:-1