def inheritanceTesting(): # 1:0-61:-1
    class Parent(): # 2:4-6:3
        pName = "Bob" # 3:8-3:20
    class Child(): # 6:4-13:3
        chName = "Steve" # 7:8-7:23
        def getParent(): # 9:8-13:3
            return Parent().pName # 10:12-10:32
    class Friend(Parent, Child): # 13:4-21:3
        name = "Dan" # 14:8-14:19
        def getPeople(self): # 16:8-21:3
            self.__setattr__("age", 100 ) # 17:12-17:39
            return (((self.pName.__len__() > 0  ) and (self.chName.__len__() > 0  )) and hasattr(self, "age")) # 18:12-18:97
    class Friend2(Parent, Child, Friend): # 21:4-29:3
        name = "Dan" # 22:8-22:19
        def getPeople(self): # 24:8-29:3
            self.__setattr__("age", 100 ) # 25:12-25:39
            return self.getPeople # 26:12-26:32
    class Friend3(Friend, Child, Friend2): # 29:4-37:3
        name = "Dan" # 30:8-30:19
        def getPeople(self): # 32:8-37:3
            self.__setattr__("age", 100 ) # 33:12-33:39
            return self.getPeople # 34:12-34:32
    class Friend4(Friend, Friend3, Friend2): # 37:4-45:3
        name = "Dan" # 38:8-38:19
        def getPeople(self): # 40:8-45:3
            self.__setattr__("age", 100 ) # 41:12-41:39
            return self.getPeople # 42:12-42:32
    class Citizens(Friend): # 45:4-52:3
        count = 100000  # 46:8-46:21
        def checkPeople(self): # 48:8-52:3
            return ((self.count > 0  ) and self.getPeople()) # 49:12-49:53
    def testCheck(): # 52:4-60:3
        c = Citizens() # 53:8-53:21
        c.__setattr__("mid_age", 45 ) # 54:8-54:35
        c.__delattr__("mid_age") # 55:8-55:31
        return (c.getPeople() and hasattr(c, "mid_age")) # 57:8-57:53
    return testCheck() # 60:4-60:21