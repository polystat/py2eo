def inheritanceTesting():
    Parent = None
    Friend4 = None
    Friend3 = None
    testCheck = None
    Citizens = None
    Friend2 = None
    Friend = None
    Child = None
    bb_start0 = None
    bb_interm0 = None
    bb_interm1 = None
    bb_interm3 = None
    bb_interm6 = None
    bb_interm9 = None
    bb_interm12 = None
    bb_interm15 = None
    bb_interm17 = None
    bb_finish0 = None
    def bb_start0():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Parent():
            pName = None
            bb_start1 = None
            bb_finish1 = None
            def bb_start1():
                nonlocal pName
                pName = "Bob"
                return bb_finish1()
            def bb_finish1():
                return None
            return bb_start1()
        return bb_interm0()
    def bb_interm0():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Child():
            getParent = None
            chName = None
            bb_start2 = None
            bb_interm2 = None
            bb_finish2 = None
            def bb_start2():
                nonlocal getParent, chName
                chName = "Steve"
                return bb_interm2()
            def bb_interm2():
                nonlocal getParent, chName
                def getParent():
                    bb_start3 = None
                    bb_finish3 = None
                    def bb_start3():
                        return Parent().pName
                        return bb_finish3()
                    def bb_finish3():
                        return None
                    return bb_start3()
                return bb_finish2()
            def bb_finish2():
                return None
            return bb_start2()
        return bb_interm1()
    def bb_interm1():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Friend():
            name = None
            getPeople = None
            bb_start4 = None
            bb_interm4 = None
            bb_finish4 = None
            def bb_start4():
                nonlocal name, getPeople
                name = "Dan"
                return bb_interm4()
            def bb_interm4():
                nonlocal name, getPeople
                def getPeople(self):
                    bb_start5 = None
                    bb_interm5 = None
                    bb_finish5 = None
                    def bb_start5():
                        self.__setattr__("age", 100)
                        return bb_interm5()
                    def bb_interm5():
                        return ((self.pName.__len__() > 0 ) and ((self.chName.__len__() > 0 ) and hasattr(self, "age")))
                        return bb_finish5()
                    def bb_finish5():
                        return None
                    return bb_start5()
                return bb_finish4()
            def bb_finish4():
                return None
            return bb_start4()
        return bb_interm3()
    def bb_interm3():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Friend2():
            name = None
            getPeople = None
            bb_start6 = None
            bb_interm7 = None
            bb_finish6 = None
            def bb_start6():
                nonlocal name, getPeople
                name = "Dan"
                return bb_interm7()
            def bb_interm7():
                nonlocal name, getPeople
                def getPeople(self):
                    bb_start7 = None
                    bb_interm8 = None
                    bb_finish7 = None
                    def bb_start7():
                        self.__setattr__("age", 100)
                        return bb_interm8()
                    def bb_interm8():
                        return self.getPeople
                        return bb_finish7()
                    def bb_finish7():
                        return None
                    return bb_start7()
                return bb_finish6()
            def bb_finish6():
                return None
            return bb_start6()
        return bb_interm6()
    def bb_interm6():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Friend3():
            name = None
            getPeople = None
            bb_start8 = None
            bb_interm10 = None
            bb_finish8 = None
            def bb_start8():
                nonlocal name, getPeople
                name = "Dan"
                return bb_interm10()
            def bb_interm10():
                nonlocal name, getPeople
                def getPeople(self):
                    bb_start9 = None
                    bb_interm11 = None
                    bb_finish9 = None
                    def bb_start9():
                        self.__setattr__("age", 100)
                        return bb_interm11()
                    def bb_interm11():
                        return self.getPeople
                        return bb_finish9()
                    def bb_finish9():
                        return None
                    return bb_start9()
                return bb_finish8()
            def bb_finish8():
                return None
            return bb_start8()
        return bb_interm9()
    def bb_interm9():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Friend4():
            name = None
            getPeople = None
            bb_start10 = None
            bb_interm13 = None
            bb_finish10 = None
            def bb_start10():
                nonlocal name, getPeople
                name = "Dan"
                return bb_interm13()
            def bb_interm13():
                nonlocal name, getPeople
                def getPeople(self):
                    bb_start11 = None
                    bb_interm14 = None
                    bb_finish11 = None
                    def bb_start11():
                        self.__setattr__("age", 100)
                        return bb_interm14()
                    def bb_interm14():
                        return self.getPeople
                        return bb_finish11()
                    def bb_finish11():
                        return None
                    return bb_start11()
                return bb_finish10()
            def bb_finish10():
                return None
            return bb_start10()
        return bb_interm12()
    def bb_interm12():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def Citizens():
            count = None
            checkPeople = None
            bb_start12 = None
            bb_interm16 = None
            bb_finish12 = None
            def bb_start12():
                nonlocal count, checkPeople
                count = 100000
                return bb_interm16()
            def bb_interm16():
                nonlocal count, checkPeople
                def checkPeople(self):
                    bb_start13 = None
                    bb_finish13 = None
                    def bb_start13():
                        return ((self.count > 0 ) and self.getPeople())
                        return bb_finish13()
                    def bb_finish13():
                        return None
                    return bb_start13()
                return bb_finish12()
            def bb_finish12():
                return None
            return bb_start12()
        return bb_interm15()
    def bb_interm15():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        def testCheck():
            c = None
            bb_start14 = None
            bb_interm18 = None
            bb_interm19 = None
            bb_interm20 = None
            bb_finish14 = None
            def bb_start14():
                nonlocal c
                c = Citizens()
                return bb_interm18()
            def bb_interm18():
                nonlocal c
                c.__setattr__("mid_age", 45)
                return bb_interm19()
            def bb_interm19():
                nonlocal c
                c.__delattr__("mid_age")
                return bb_interm20()
            def bb_interm20():
                nonlocal c
                return (c.getPeople() and hasattr(c, "mid_age"))
                return bb_finish14()
            def bb_finish14():
                return None
            return bb_start14()
        return bb_interm17()
    def bb_interm17():
        nonlocal Parent, Friend4, Friend3, testCheck, Citizens, Friend2, Friend, Child
        return testCheck()
        return bb_finish0()
    def bb_finish0():
        return None
    return bb_start0()
assert inheritanceTesting()