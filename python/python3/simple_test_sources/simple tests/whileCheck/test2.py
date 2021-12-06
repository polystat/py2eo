def whileCheck2():
    n = 236
    length = 0
    while True:
        length += 1
        n //= 10
        if n == 0:
            break

    return length