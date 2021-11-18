from collections import defaultdict


def merge(sequences):
    sequences = [list(x) for x in sequences]
    result = []

    while True:
        sequences = [x for x in sequences if x]
        if not sequences:
            return result

        for seq in sequences:
            head = seq[0]
            if not any(head in s[1:] for s in sequences):
                break
        else:
            raise TypeError

        result.append(head)
        for seq in sequences:
            if seq[0] == head:
                del seq[0]


def class_graph(obj):
    graph = {}
    _add_to_graph(obj, graph, lambda cls: cls.__bases__)
    return graph


def _add_to_graph(obj, graph, bases_func):
    if obj not in graph:
        graph[obj] = bases_func(obj)
        for x in graph[obj]:
            _add_to_graph(x, graph, bases_func)


def _linearize(head, graph, results):
    if head in results:
        return results[head]

    res = merge(
        [[head]] +
        [_linearize(x, graph, results) for x in graph[head]] +
        ([graph[head]])
    )

    results[head] = res
    return res


def linearize(obj):
    results = {}
    graph = defaultdict(list, class_graph(obj))

    for head in sorted(graph, key=lambda k: len(graph[k])):
        _linearize(head, graph, results)
    return results


def findattr(obj, name):
    for pred in linearize(obj):
        if name in pred.__dict__.keys():
            return pred
    raise TypeError


def eo_getattr(obj, name):
    return getattr(findattr(obj, name), name, None)


def eo_setattr(obj, name, val):
    setattr(findattr(obj, name), name, val)