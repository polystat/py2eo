def my_decorator(func): # 1:0-7:-1
    def wrapper(): # 2:4-6:3
        print("Something is happening before the function is called.") # 3:8-3:69
        func(=abc) # 4:8-4:13
        print("Something is happening after the function is called.") # 5:8-5:68
    return wrapper # 6:4-6:17
@my_decorator
def say_whee(): # 8:0-10:27
    print("Whee!") # 9:4-9:17
    return True # 10:4-10:14