disabled: True
python: |
  def lambda1():
      f = lambda x: x * 10
      return f(5) == 50

  #assert lambda1()
