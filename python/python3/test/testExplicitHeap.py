
def theTest():

    def f(x):
        def g(y):
            return x + y
        return g

    g22 = f(22)
    g33 = f(33)
    print(g33(44))
    print(g22(44))

theTest()
