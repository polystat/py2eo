def test(): # 1:0-5:70
    bigStr = "Hello World!!!" # 2:4-2:28
    subStr2 = "loH" # 3:4-3:17
    check = ['H', 'l', 'l', 'o', 'o', 'l'] # 4:4-4:41
    return ([i for i in bigStr if (i in subStr )] == check ) # 5:4-5:59