def te3(): # 1:0-18:35
    class c(BaseException): # 2:4-4:3
        pass # 3:8-3:11
    try: # 4:4-18:3
        try: # 5:8-15:3
            x = 1  # 6:12-6:16
            raise StopIteration # 7:12-7:30
        except c:
            return false # 9:12-9:23
        else:
            return false # 11:12-11:23
        finally:
            x+=10  # 13:12-13:16
            print(x) # 14:12-14:19
    except StopIteration:
        x+=100  # 16:8-16:13

    return (x == 111  ) # 18:4-18:22