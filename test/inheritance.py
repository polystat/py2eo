class A(object):
    x = 5

class B(A):
    pass

print(B.x)
print(getattr(B, "x"))
print(setattr(B, "x", 7))
