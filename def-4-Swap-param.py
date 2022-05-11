def A(p1, p2): # 1:0-3:-1
    return (p1 < p2 ) # 2:4-2:20
def test(): # 3:0-4:58
    return (A(p1=1 , p2=3 ) != A(p2=1 , p1=3 ) ) # 4:4-4:47