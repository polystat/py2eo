from collections import defaultdict


def merge(seqs):
    res = []
    i = 0
    while 1:
        nonemptyseqs = [seq for seq in seqs if seq]
        if not nonemptyseqs:
            return res
        i += 1
        for seq in nonemptyseqs:  # find merge candidates among seq heads
            cand = seq[0]
            nothead = [s for s in nonemptyseqs if cand in s[1:]]
            if nothead:
                cand = None  # reject candidate
            else:
                break
        if not cand:
            raise "Inconsistent hierarchy"
        res.append(cand)
        for seq in nonemptyseqs:  # remove cand
            if seq[0] == cand:
                del seq[0]


def mro(c):
    return merge([[c]] + map(mro, getattr(c, "__bases__")) + [list(getattr(c, "__bases__"))])


def findattr(obj, name):
    for pred in mro(obj):
        if name in pred.__dict__.keys():
            return pred
    raise TypeError


def eo_getattr(obj, name):
    return getattr(findattr(obj, name), name, None)


def eo_setattr(obj, name, val):
    setattr(findattr(obj, name), name, val)
