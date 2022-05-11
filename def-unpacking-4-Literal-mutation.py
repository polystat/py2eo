def test(): # 1:0-4:81
    def test_kwargs(**kwargs): # 2:4-4:3
        return kwargs # 3:8-3:20
    return (test_kwargs(random=12 , parameters=21 ).__class__ == dict ) # 4:4-4:70