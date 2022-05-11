def test: # 1:0-6:11
    test_dict1 = {'gfg' : 1 , 'is' : 2 , 'best' : 3 , 'for' : 4 , 'CS' : 5 } # 2:2-2:63
    test_dict2 = {'gfg' : 1 , 'is' : 2 , 'best' : 3 } # 3:2-3:44
    res = all(((test_dict1.get(key, None) == val ) for (key, val) in test_dict2.items)) # 4:2-5:33
    return res # 6:2-6:11