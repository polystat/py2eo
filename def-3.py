def my_decorator(func): # 1:0-8:-1
    def wrapper(): # 2:2-6:1
        print("Something is happening before the function is called.") # 3:6-3:67
        func() # 4:6-4:11
        print("Something is happening after the function is called.") # 5:6-5:66
    return wrapper # 6:2-6:15
@my_decorator
def say_whee(): # 9:0-11:12
    print("Whee!") # 10:2-10:15
    return True # 11:2-11:12