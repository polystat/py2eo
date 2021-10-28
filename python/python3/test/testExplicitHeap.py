
#def myPrint(x):
#    print(x)

def theTest():

    def f(x):
        def g(y):
            def h(z):
                return x + y + z
            return h
        return g

    g2 = f(2)
    h24 = g2(4)
    h28 = g2(8)
    g3 = f(3)
    h34 = g3(4)
    h38 = g3(8)
    myPrint(h38(10))
    myPrint(h34(10))
    myPrint(h28(10))
    myPrint(h24(10))

#theTest()