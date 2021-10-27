
def testFib1():
    f0 = 0
    f1 = 1
    i = 0
    while (i < 10):
        f2 = f0 + f1
        i = i + 1
        f0 = f1
        f1 = f2
        if i == 9: break
    print(f0)

testFib1()

