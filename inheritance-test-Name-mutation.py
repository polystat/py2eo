def inheritanceTesting(): # 1:0-37:34
    class Parent(): # 2:4-4:3
        pName = "Bob" # 3:8-3:20
    class Child(): # 4:4-8:3
        chName2 = "Steve" # 5:8-5:23
        def getParent(): # 6:8-8:3
            return Parent().pName # 7:12-7:32
    class Friend(Parent, Child): # 8:4-13:3
        name = "Dan" # 9:8-9:19
        def getPeople(self): # 10:8-13:3
            self.__setattr__("age", 100 ) # 11:12-11:40
            return (((self.pName.__len__() > 0  ) and (self.chName.__len__() > 0  )) and hasattr(self, "age")) # 12:12-12:109
    class Friend2(Parent, Child, Friend): # 13:4-18:3
        name = "Dan" # 14:8-14:19
        def getPeople(self): # 15:8-18:3
            self.__setattr__("age", 100 ) # 16:12-16:40
            return self.getPeople # 17:12-17:32
    class Friend3(Friend, Child, Friend2): # 18:4-23:3
        name = "Dan" # 19:8-19:19
        def getPeople(self): # 20:8-23:3
            self.__setattr__("age", 100 ) # 21:12-21:40
            return self.getPeople # 22:12-22:32
    class Friend4(Friend, Friend3, Friend2): # 23:4-28:3
        name = "Dan" # 24:8-24:19
        def getPeople(self): # 25:8-28:3
            self.__setattr__("age", 100 ) # 26:12-26:40
            return self.getPeople # 27:12-27:32
    class Citizens(Friend): # 28:4-32:3
        count = 100000  # 29:8-29:21
        def checkPeople(self): # 30:8-32:3
            return ((self.count > 0  ) and self.getPeople()) # 31:12-31:59
    def testCheck(): # 32:4-37:3
        c = Citizens() # 33:8-33:21
        c.__setattr__("mid_age", 45 ) # 34:8-34:36
        c.__delattr__("mid_age") # 35:8-35:31
        return (c.getPeople() and hasattr(c, "mid_age")) # 36:8-36:55
    return testCheck() # 37:4-37:21