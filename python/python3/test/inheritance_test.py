def inheritanceTesting():
    class Parent:
        pName = "Bob"


    class Child:
        chName = "Steve"

        def getParent():
            return Parent().pName


    class Friend(Parent, Child):
        name = "Dan"

        def getPeople(self):
            self.__setattr__("age", 100)
            return self.pName.__len__() > 0 and self.chName.__len__() > 0 and hasattr(self, "age")


    class Friend2(Parent, Child, Friend):
        name = "Dan"

        def getPeople(self):
            self.__setattr__("age", 100)
            return self.getPeople


    class Friend3(Friend, Child, Friend2):
        name = "Dan"

        def getPeople(self):
            self.__setattr__("age", 100)
            return self.getPeople


    class Friend4(Friend, Friend3, Friend2):
        name = "Dan"

        def getPeople(self):
            self.__setattr__("age", 100)
            return self.getPeople


    class Citizens(Friend):
        count = 100000

        def checkPeople(self):
            return self.count > 0 and self.getPeople()


    def testCheck():
        c = Citizens()
        c.__setattr__("mid_age", 45)
        c.__delattr__("mid_age")

        return c.getPeople() and hasattr(c, "mid_age")


    return testCheck()
