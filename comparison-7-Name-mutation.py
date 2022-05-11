def test(): # 1:0-5:24
    test_dict1 = {'gfg' : 1 , 'is' : 2 , 'best' : 3 , 'for' : 4 , 'CS' : 5 } # 2:4-2:75
    test_dict22 = {'gfg' : 1 , 'is' : 2 , 'best' : 3 } # 3:4-3:52
    res = all(((test_dict1.get(key, None) == val ) for (key, val) in test_dict2.items())) # 4:4-4:88
    return res # 5:4-5:13