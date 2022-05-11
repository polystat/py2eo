def te3(): # 1:0-14:-1
    class c(BaseException): # 2:2-2:32
        pass # 2:26-2:29
    try: # 3:2-12:34
        try: # 4:4-12:1
            x = 1  # 5:6-5:10
            raise StopIteration # 6:6-6:24
        except c:
            return False # 7:14-7:25
        else:
            return False # 8:11-8:22
        finally:
            x+=10  # 10:6-10:12
            print(x) # 11:6-11:13
    except StopIteration:
        x+=100  # 12:24-12:31

    return (x == 111  ) # 13:2-13:16