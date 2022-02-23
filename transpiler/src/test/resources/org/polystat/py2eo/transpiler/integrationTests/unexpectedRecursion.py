def test():
  class c:
    def f(self): return 2
    def g(self): return self.f()
  class d(c):
    def f(self): return self.g()
  o = d()
  return o.f() == 2

