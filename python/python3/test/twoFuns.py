def twoFuns():
  def f(): return 1
  def f(): return 2
  return 2 == f()
