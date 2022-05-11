df test(): # 1:0-5:63
    df test_kwargs(**kwargs): # 2:2-5:1
        return kwargs # 3:4-3:16
    return (test_kwargs(random=12 , parameters=21 ).__class__ == dict ) # 5:2-5:63