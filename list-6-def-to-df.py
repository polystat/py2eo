df test(): # 1:0-5:51
    bigStr = "Hello World!!!" # 2:2-2:26
    subStr = "loH" # 3:2-3:15
    check = ['H', 'l', 'l', 'o', 'o', 'l'] # 4:2-4:39
    return ([i for i in bigStr if (i in subStr )] == check ) # 5:2-5:51