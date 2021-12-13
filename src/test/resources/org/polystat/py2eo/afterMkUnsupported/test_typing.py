assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
py_typing = None
c_typing = None
class BaseTestCase(TestCase):
    def assertIsSubclass(self, cls, class_or_tuple, msg, /):
        assert(false)
    def assertNotIsSubclass(self, cls, class_or_tuple, msg, /):
        assert(false)
    def clear_caches(self):
        assert(false)
class Employee():
    pass
class Manager(Employee):
    pass
class Founder(Employee):
    pass
class ManagingFounder(Manager, Founder):
    pass
class AnyTests(BaseTestCase):
    def test_any_instance_type_error(self):
        assert(false)
    def test_any_subclass_type_error(self):
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(Any), 'typing.Any')
    def test_errors(self):
        assert(false)
        assert(false)
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_cannot_instantiate(self):
        assert(false)
        assert(false)
    def test_any_works_with_alias(self):
        None
        None
        None
class NoReturnTests(BaseTestCase):
    def test_noreturn_instance_type_error(self):
        assert(false)
    def test_noreturn_subclass_type_error(self):
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(NoReturn), 'typing.NoReturn')
    def test_not_generic(self):
        assert(false)
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_cannot_instantiate(self):
        assert(false)
        assert(false)
class TypeVarTests(BaseTestCase):
    def test_basic_plain(self):
        T = TypeVar('T')
        self.assertEqual(T, T)
        self.assertIsInstance(T, TypeVar)
    def test_typevar_instance_type_error(self):
        T = TypeVar('T')
        assert(false)
    def test_typevar_subclass_type_error(self):
        T = TypeVar('T')
        assert(false)
        assert(false)
    def test_constrained_error(self):
        assert(false)
    def test_union_unique(self):
        X = TypeVar('X')
        Y = TypeVar('Y')
        self.assertNotEqual(X, Y)
        self.assertEqual(None, X)
        self.assertNotEqual(None, None)
        self.assertEqual(None, X)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__parameters__, None)
        self.assertIs(None.__origin__, Union)
    def test_or(self):
        X = TypeVar('X')
        self.assertEqual((X | "x"), None)
        self.assertEqual(("x" | X), None)
        self.assertEqual(get_args((X | "x")), None)
        self.assertEqual(get_args(("x" | X)), None)
    def test_union_constrained(self):
        A = TypeVar('A', str, bytes)
        self.assertNotEqual(None, None)
    def test_repr(self):
        self.assertEqual(repr(T), '~T')
        self.assertEqual(repr(KT), '~KT')
        self.assertEqual(repr(VT), '~VT')
        self.assertEqual(repr(AnyStr), '~AnyStr')
        T_co = None
        self.assertEqual(repr(T_co), '+T_co')
        T_contra = None
        self.assertEqual(repr(T_contra), '-T_contra')
    def test_no_redefinition(self):
        self.assertNotEqual(TypeVar('T'), TypeVar('T'))
        self.assertNotEqual(TypeVar('T', int, str), TypeVar('T', int, str))
    def test_cannot_subclass_vars(self):
        assert(false)
    def test_cannot_subclass_var_itself(self):
        assert(false)
    def test_cannot_instantiate_vars(self):
        assert(false)
    def test_bound_errors(self):
        assert(false)
        assert(false)
    def test_missing__name__(self):
        code = "import typing\n""T = typing.TypeVar('T')\n"
        exec(code, None)
    def test_no_bivariant(self):
        assert(false)
class UnionTests(BaseTestCase):
    def test_basics(self):
        u = None
        self.assertNotEqual(u, Union)
    def test_subclass_error(self):
        assert(false)
        assert(false)
        assert(false)
    def test_union_any(self):
        u = None
        self.assertEqual(u, Any)
        u1 = None
        u2 = None
        u3 = None
        self.assertEqual(u1, u2)
        self.assertNotEqual(u1, Any)
        self.assertNotEqual(u2, Any)
        self.assertNotEqual(u3, Any)
    def test_union_object(self):
        u = None
        self.assertEqual(u, object)
        u1 = None
        u2 = None
        self.assertEqual(u1, u2)
        self.assertNotEqual(u1, object)
        self.assertNotEqual(u2, object)
    def test_unordered(self):
        u1 = None
        u2 = None
        self.assertEqual(u1, u2)
    def test_single_class_disappears(self):
        t = None
        self.assertIs(t, Employee)
    def test_base_class_kept(self):
        u = None
        self.assertNotEqual(u, Employee)
        self.assertIn(Employee, u.__args__)
        self.assertIn(Manager, u.__args__)
    def test_union_union(self):
        u = None
        v = None
        self.assertEqual(v, None)
    def test_repr(self):
        self.assertEqual(repr(Union), 'typing.Union')
        u = None
        self.assertEqual(repr(u), ('typing.Union[%s.Employee, int]' % __name__))
        u = None
        self.assertEqual(repr(u), ('typing.Union[int, %s.Employee]' % __name__))
        T = TypeVar('T')
        u = None
        self.assertEqual(repr(u), repr(int))
        u = None
        self.assertEqual(repr(u), 'typing.Union[typing.List[int], int]')
        u = None
        self.assertEqual(repr(u), 'typing.Union[list[int], dict[str, float]]')
        u = None
        self.assertEqual(repr(u), 'typing.Union[int, float]')
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
        assert(false)
    def test_cannot_instantiate(self):
        assert(false)
        assert(false)
        u = None
        assert(false)
        assert(false)
    def test_union_generalization(self):
        self.assertFalse((None == str))
        self.assertFalse((None == None))
        self.assertIn(str, None.__args__)
        self.assertIn(None, None.__args__)
    def test_union_compare_other(self):
        self.assertNotEqual(Union, object)
        self.assertNotEqual(Union, Any)
        self.assertNotEqual(ClassVar, Union)
        self.assertNotEqual(Optional, Union)
        self.assertNotEqual(None, Optional)
        self.assertNotEqual(Optional, typing.Mapping)
        self.assertNotEqual(None, Union)
    def test_optional(self):
        o = None
        u = None
        self.assertEqual(o, u)
    def test_empty(self):
        assert(false)
    def test_no_eval_union(self):
        u = None
        def f(x):
            
        self.assertIs(None, u)
    def test_function_repr_union(self):
        def fun():
            
        self.assertEqual(repr(None), 'typing.Union[fun, int]')
    def test_union_str_pattern(self):
        A = None
        A
    def test_etree(self):
        assert(false)
        None
        def Elem():
            assert(false)
        None
class TupleTests(BaseTestCase):
    def test_basics(self):
        assert(false)
        assert(false)
        class TP(tuple):
            
        self.assertIsSubclass(tuple, Tuple)
        self.assertIsSubclass(TP, Tuple)
    def test_equality(self):
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
    def test_tuple_subclass(self):
        class MyTuple(tuple):
            pass
        self.assertIsSubclass(MyTuple, Tuple)
    def test_tuple_instance_type_error(self):
        assert(false)
        self.assertIsInstance(None, Tuple)
    def test_repr(self):
        self.assertEqual(repr(Tuple), 'typing.Tuple')
        self.assertEqual(repr(None), 'typing.Tuple[()]')
        self.assertEqual(repr(None), 'typing.Tuple[int, float]')
        self.assertEqual(repr(None), 'typing.Tuple[int, ...]')
        self.assertEqual(repr(None), 'typing.Tuple[list[int]]')
    def test_errors(self):
        assert(false)
        assert(false)
class BaseCallableTests():
    def test_self_subclass(self):
        Callable = self.Callable
        assert(false)
        self.assertIsSubclass(types.FunctionType, Callable)
    def test_eq_hash(self):
        Callable = self.Callable
        C = None
        self.assertEqual(C, None)
        self.assertEqual(len(None), 1)
        self.assertNotEqual(C, None)
        self.assertNotEqual(C, None)
        self.assertNotEqual(C, None)
        self.assertNotEqual(C, None)
        self.assertNotEqual(C, None)
        self.assertNotEqual(C, Callable)
    def test_cannot_instantiate(self):
        Callable = self.Callable
        assert(false)
        assert(false)
        c = None
        assert(false)
        assert(false)
    def test_callable_wrong_forms(self):
        Callable = self.Callable
        assert(false)
    def test_callable_instance_works(self):
        Callable = self.Callable
        def f():
            pass
        self.assertIsInstance(f, Callable)
        self.assertNotIsInstance(None, Callable)
    def test_callable_instance_type_error(self):
        Callable = self.Callable
        def f():
            pass
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_repr(self):
        pass
    def test_callable_with_ellipsis(self):
        Callable = self.Callable
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_ellipsis_in_generic(self):
        Callable = self.Callable
        None
    def test_basic(self):
        Callable = self.Callable
        alias = None
        if (None):
            self.assertIsInstance(alias, types.GenericAlias)
        else:
            pass
        self.assertIs(alias.__origin__, collections.abc.Callable)
        self.assertEqual(alias.__args__, None)
        self.assertEqual(alias.__parameters__, None)
    def test_weakref(self):
        Callable = self.Callable
        alias = None
        self.assertEqual(weakref.ref(alias)(), alias)
    def test_pickle(self):
        Callable = self.Callable
        alias = None
        assert(false)
    def test_var_substitution(self):
        pass
    def test_type_erasure(self):
        Callable = self.Callable
        class C1(Callable):
            def __call__(self):
                assert(false)
        a = None
        self.assertIs(a().__class__, C1)
        self.assertEqual(a().__orig_class__, None)
    def test_paramspec(self):
        Callable = self.Callable
        P = ParamSpec('P')
        P2 = ParamSpec('P2')
        C1 = None
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        assert(false)
        C2 = None
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_concatenate(self):
        Callable = self.Callable
        P = ParamSpec('P')
        C1 = None
    def test_errors(self):
        Callable = self.Callable
        alias = None
        assert(false)
        P = ParamSpec('P')
        C1 = None
        assert(false)
        assert(false)
class TypingCallableTests(BaseCallableTests, BaseTestCase):
    Callable = typing.Callable
    def test_consistency(self):
        c1 = None
        c2 = None
        self.assertEqual(c1.__args__, c2.__args__)
        self.assertEqual(hash(c1.__args__), hash(c2.__args__))
class CollectionsCallableTests(BaseCallableTests, BaseTestCase):
    Callable = collections.abc.Callable
class LiteralTests(BaseTestCase):
    def test_basics(self):
        None
        None
        None
        None
        None
        None
        None
    def test_illegal_parameters_do_not_raise_runtime_errors(self):
        None
        None
        None
    def test_literals_inside_other_types(self):
        None
        None
    def test_repr(self):
        self.assertEqual(repr(None), "typing.Literal[1]")
        self.assertEqual(repr(None), "typing.Literal[1, True, 'foo']")
        self.assertEqual(repr(None), "typing.Literal[int]")
        self.assertEqual(repr(Literal), "typing.Literal")
        self.assertEqual(repr(None), "typing.Literal[None]")
        self.assertEqual(repr(None), "typing.Literal[1, 2, 3]")
    def test_cannot_init(self):
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_no_isinstance_or_issubclass(self):
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_no_subclassing(self):
        assert(false)
        assert(false)
    def test_no_multiple_subscripts(self):
        assert(false)
    def test_equal(self):
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_hash(self):
        self.assertEqual(hash(None), hash(None))
        self.assertEqual(hash(None), hash(None))
        self.assertEqual(hash(None), hash(None))
    def test_args(self):
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__args__, None)
    def test_flatten(self):
        l1 = None
        l2 = None
        l3 = None
        assert(false)
XK = TypeVar('XK', str, bytes)
XV = TypeVar('XV')
class SimpleMapping(None):
    def __getitem__(self, key):
        
    def __setitem__(self, key, value):
        
    def get(self, key, default, /):
        assert(false)
class MySimpleMapping(None):
    def __init__(self):
        assert(false)
    def __getitem__(self, key):
        assert(false)
    def __setitem__(self, key, value):
        assert(false)
    def get(self, key, default, /):
        assert(false)
class Coordinate(Protocol):
    assert(false)
    assert(false)
def Point():
    assert(false)
class MyPoint():
    assert(false)
    assert(false)
    assert(false)
class XAxis(Protocol):
    assert(false)
class YAxis(Protocol):
    assert(false)
def Position():
    assert(false)
def Proto():
    assert(false)
class Concrete(Proto):
    pass
class Other():
    assert(false)
    def meth(self, arg):
        if ((arg == 'this')):
            assert(false)
        else:
            pass
        assert(false)
class NT(NamedTuple):
    assert(false)
    assert(false)
def HasCallProtocol():
    assert(false)
class ProtocolTests(BaseTestCase):
    def test_basic_protocol(self):
        def P():
            assert(false)
        class C():
            pass
        class D():
            def meth(self):
                pass
        def f():
            pass
        self.assertIsSubclass(D, P)
        self.assertIsInstance(D(), P)
        self.assertNotIsSubclass(C, P)
        self.assertNotIsInstance(C(), P)
        self.assertNotIsSubclass(types.FunctionType, P)
        self.assertNotIsInstance(f, P)
    def test_everything_implements_empty_protocol(self):
        def Empty():
            assert(false)
        class C():
            pass
        def f():
            pass
        assert(false)
        assert(false)
    def test_function_implements_protocol(self):
        def f():
            pass
        self.assertIsInstance(f, HasCallProtocol)
    def test_no_inheritance_from_nominal(self):
        class C():
            pass
        class BP(Protocol):
            pass
        assert(false)
        assert(false)
        assert(false)
        class D(BP, C):
            pass
        class E(C, BP):
            pass
        self.assertNotIsInstance(D(), E)
        self.assertNotIsInstance(E(), D)
    def test_no_instantiation(self):
        class P(Protocol):
            pass
        assert(false)
        class C(P):
            pass
        self.assertIsInstance(C(), C)
        assert(false)
        T = TypeVar('T')
        class PG(None):
            pass
        assert(false)
        assert(false)
        assert(false)
        class CG(None):
            pass
        self.assertIsInstance(None(), CG)
        assert(false)
    def test_cannot_instantiate_abstract(self):
        def P():
            assert(false)
        class B(P):
            pass
        class C(B):
            def ameth(self):
                assert(false)
        assert(false)
        self.assertIsInstance(C(), P)
    def test_subprotocols_extending(self):
        class P1(Protocol):
            def meth1(self):
                pass
        def P2():
            assert(false)
        class C():
            def meth1(self):
                pass
            def meth2(self):
                pass
        class C1():
            def meth1(self):
                pass
        class C2():
            def meth2(self):
                pass
        self.assertNotIsInstance(C1(), P2)
        self.assertNotIsInstance(C2(), P2)
        self.assertNotIsSubclass(C1, P2)
        self.assertNotIsSubclass(C2, P2)
        self.assertIsInstance(C(), P2)
        self.assertIsSubclass(C, P2)
    def test_subprotocols_merging(self):
        class P1(Protocol):
            def meth1(self):
                pass
        class P2(Protocol):
            def meth2(self):
                pass
        def P():
            assert(false)
        class C():
            def meth1(self):
                pass
            def meth2(self):
                pass
        class C1():
            def meth1(self):
                pass
        class C2():
            def meth2(self):
                pass
        self.assertNotIsInstance(C1(), P)
        self.assertNotIsInstance(C2(), P)
        self.assertNotIsSubclass(C1, P)
        self.assertNotIsSubclass(C2, P)
        self.assertIsInstance(C(), P)
        self.assertIsSubclass(C, P)
    def test_protocols_issubclass(self):
        T = TypeVar('T')
        def P():
            assert(false)
        def PG():
            assert(false)
        class BadP(Protocol):
            def x(self):
                
        class BadPG(None):
            def x(self):
                
        class C():
            def x(self):
                
        self.assertIsSubclass(C, P)
        self.assertIsSubclass(C, PG)
        self.assertIsSubclass(BadP, PG)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_protocols_issubclass_non_callable(self):
        class C():
            x = 1
        def PNonCall():
            assert(false)
        assert(false)
        self.assertIsInstance(C(), PNonCall)
        PNonCall.register(C)
        assert(false)
        self.assertIsInstance(C(), PNonCall)
        class D(PNonCall):
            
        self.assertNotIsSubclass(C, D)
        self.assertNotIsInstance(C(), D)
        D.register(C)
        self.assertIsSubclass(C, D)
        self.assertIsInstance(C(), D)
        assert(false)
    def test_protocols_isinstance(self):
        T = TypeVar('T')
        def P():
            assert(false)
        def PG():
            assert(false)
        class BadP(Protocol):
            def meth(x):
                
        class BadPG(None):
            def meth(x):
                
        class C():
            def meth(x):
                
        self.assertIsInstance(C(), P)
        self.assertIsInstance(C(), PG)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_protocols_isinstance_py36(self):
        class APoint():
            def __init__(self, x, y, label):
                assert(false)
                assert(false)
                assert(false)
        class BPoint():
            label = 'B'
            def __init__(self, x, y):
                assert(false)
                assert(false)
        class C():
            def __init__(self, attr):
                assert(false)
            def meth(self, arg):
                assert(false)
        class Bad():
            pass
        self.assertIsInstance(APoint(1, 2, 'A'), Point)
        self.assertIsInstance(BPoint(1, 2), Point)
        self.assertNotIsInstance(MyPoint(), Point)
        self.assertIsInstance(BPoint(1, 2), Position)
        self.assertIsInstance(Other(), Proto)
        self.assertIsInstance(Concrete(), Proto)
        self.assertIsInstance(C(42), Proto)
        self.assertNotIsInstance(Bad(), Proto)
        self.assertNotIsInstance(Bad(), Point)
        self.assertNotIsInstance(Bad(), Position)
        self.assertNotIsInstance(Bad(), Concrete)
        self.assertNotIsInstance(Other(), Concrete)
        self.assertIsInstance(NT(1, 2), Position)
    def test_protocols_isinstance_init(self):
        T = TypeVar('T')
        def P():
            assert(false)
        def PG():
            assert(false)
        class C():
            def __init__(self, x):
                assert(false)
        self.assertIsInstance(C(1), P)
        self.assertIsInstance(C(1), PG)
    def test_protocol_checks_after_subscript(self):
        class P(None):
            pass
        class C(None):
            pass
        class Other1():
            pass
        class Other2():
            pass
        CA = None
        self.assertNotIsInstance(Other1(), C)
        self.assertNotIsSubclass(Other2, C)
        class D1(None):
            pass
        class D2(None):
            pass
        CI = None
        self.assertIsInstance(D1(), C)
        self.assertIsSubclass(D2, C)
    def test_protocols_support_register(self):
        def P():
            assert(false)
        class PM(Protocol):
            def meth(self):
                pass
        class D(PM):
            pass
        class C():
            pass
        D.register(C)
        P.register(C)
        self.assertIsInstance(C(), P)
        self.assertIsInstance(C(), D)
    def test_none_on_non_callable_doesnt_block_implementation(self):
        def P():
            assert(false)
        class A():
            x = 1
        class B(A):
            x = None
        class C():
            def __init__(self):
                assert(false)
        self.assertIsInstance(B(), P)
        self.assertIsInstance(C(), P)
    def test_none_on_callable_blocks_implementation(self):
        def P():
            assert(false)
        class A():
            def x(self):
                
        class B(A):
            x = None
        class C():
            def __init__(self):
                assert(false)
        self.assertNotIsInstance(B(), P)
        self.assertNotIsInstance(C(), P)
    def test_non_protocol_subclasses(self):
        class P(Protocol):
            x = 1
        def PR():
            assert(false)
        class NonP(P):
            x = 1
        class NonPR(PR):
            pass
        class C():
            x = 1
        class D():
            def meth(self):
                pass
        self.assertNotIsInstance(C(), NonP)
        self.assertNotIsInstance(D(), NonPR)
        self.assertNotIsSubclass(C, NonP)
        self.assertNotIsSubclass(D, NonPR)
        self.assertIsInstance(NonPR(), PR)
        self.assertIsSubclass(NonPR, PR)
    def test_custom_subclasshook(self):
        class P(Protocol):
            x = 1
        class OKClass():
            pass
        class BadClass():
            x = 1
        class C(P):
            def __subclasshook__(cls, other, /):
                assert(false)
        self.assertIsInstance(OKClass(), C)
        self.assertNotIsInstance(BadClass(), C)
        self.assertIsSubclass(OKClass, C)
        self.assertNotIsSubclass(BadClass, C)
    def test_issubclass_fails_correctly(self):
        def P():
            assert(false)
        class C():
            pass
        assert(false)
    def test_defining_generic_protocols(self):
        T = TypeVar('T')
        S = TypeVar('S')
        def PR():
            assert(false)
        class P(None, None):
            y = 1
        assert(false)
        assert(false)
        class C(None):
            pass
        self.assertIsInstance(None(), C)
    def test_defining_generic_protocols_old_style(self):
        T = TypeVar('T')
        S = TypeVar('S')
        def PR():
            assert(false)
        class P(None, Protocol):
            y = 1
        assert(false)
        self.assertIsSubclass(P, PR)
        assert(false)
        class P1(Protocol, None):
            def bar(self, x):
                
        class P2(None, Protocol):
            def bar(self, x):
                
        def PSub():
            assert(false)
        class Test():
            x = 1
            def bar(self, x):
                assert(false)
        self.assertIsInstance(Test(), PSub)
    def test_init_called(self):
        T = TypeVar('T')
        class P(None):
            pass
        class C(None):
            def __init__(self):
                assert(false)
        self.assertEqual(None().test, 'OK')
        class B():
            def __init__(self):
                assert(false)
        class D1(B, None):
            pass
        self.assertEqual(None().test, 'OK')
        class D2(None, B):
            pass
        self.assertEqual(None().test, 'OK')
    def test_new_called(self):
        T = TypeVar('T')
        class P(None):
            pass
        class C(None):
            def __new__(cls, /):
                assert(false)
        self.assertEqual(None().test, 'OK')
        assert(false)
        assert(false)
    def test_protocols_bad_subscripts(self):
        T = TypeVar('T')
        S = TypeVar('S')
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_generic_protocols_repr(self):
        T = TypeVar('T')
        S = TypeVar('S')
        class P(None):
            pass
        self.assertTrue(repr(None).endswith('P[~T, ~S]'))
        self.assertTrue(repr(None).endswith('P[int, str]'))
    def test_generic_protocols_eq(self):
        T = TypeVar('T')
        S = TypeVar('S')
        class P(None):
            pass
        self.assertEqual(P, P)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_generic_protocols_special_from_generic(self):
        T = TypeVar('T')
        class P(None):
            pass
        self.assertEqual(P.__parameters__, None)
        self.assertEqual(None.__parameters__, None)
        self.assertEqual(None.__args__, None)
        self.assertIs(None.__origin__, P)
    def test_generic_protocols_special_from_protocol(self):
        def PR():
            assert(false)
        class P(Protocol):
            def meth(self):
                pass
        T = TypeVar('T')
        class PG(None):
            x = 1
            def meth(self):
                pass
        self.assertTrue(P._is_protocol)
        self.assertTrue(PR._is_protocol)
        self.assertTrue(PG._is_protocol)
        self.assertFalse(P._is_runtime_protocol)
        self.assertTrue(PR._is_runtime_protocol)
        self.assertTrue(None._is_protocol)
        self.assertEqual(typing._get_protocol_attrs(P), None)
        self.assertEqual(typing._get_protocol_attrs(PR), None)
        self.assertEqual(frozenset(typing._get_protocol_attrs(PG)), frozenset(None))
    def test_no_runtime_deco_on_nominal(self):
        assert(false)
        class Proto(Protocol):
            x = 1
        assert(false)
    def test_none_treated_correctly(self):
        def P():
            assert(false)
        class B(object):
            pass
        self.assertNotIsInstance(B(), P)
        class C():
            x = 1
        class D():
            x = None
        self.assertIsInstance(C(), P)
        self.assertIsInstance(D(), P)
        class CI():
            def __init__(self):
                assert(false)
        class DI():
            def __init__(self):
                assert(false)
        self.assertIsInstance(C(), P)
        self.assertIsInstance(D(), P)
    def test_protocols_in_unions(self):
        class P(Protocol):
            x = None
        Alias = None
        Alias2 = None
        self.assertEqual(Alias, Alias2)
    def test_protocols_pickleable(self):
        assert(false)
        T = TypeVar('T')
        def P():
            assert(false)
        class CP(None):
            pass
        c = CP()
        assert(false)
        assert(false)
        assert(false)
    def test_supports_int(self):
        self.assertIsSubclass(int, typing.SupportsInt)
        self.assertNotIsSubclass(str, typing.SupportsInt)
    def test_supports_float(self):
        self.assertIsSubclass(float, typing.SupportsFloat)
        self.assertNotIsSubclass(str, typing.SupportsFloat)
    def test_supports_complex(self):
        class C():
            def __complex__(self):
                assert(false)
        self.assertIsSubclass(complex, typing.SupportsComplex)
        self.assertIsSubclass(C, typing.SupportsComplex)
        self.assertNotIsSubclass(str, typing.SupportsComplex)
    def test_supports_bytes(self):
        class B():
            def __bytes__(self):
                assert(false)
        self.assertIsSubclass(bytes, typing.SupportsBytes)
        self.assertIsSubclass(B, typing.SupportsBytes)
        self.assertNotIsSubclass(str, typing.SupportsBytes)
    def test_supports_abs(self):
        self.assertIsSubclass(float, typing.SupportsAbs)
        self.assertIsSubclass(int, typing.SupportsAbs)
        self.assertNotIsSubclass(str, typing.SupportsAbs)
    def test_supports_round(self):
        issubclass(float, typing.SupportsRound)
        self.assertIsSubclass(float, typing.SupportsRound)
        self.assertIsSubclass(int, typing.SupportsRound)
        self.assertNotIsSubclass(str, typing.SupportsRound)
    def test_reversible(self):
        self.assertIsSubclass(list, typing.Reversible)
        self.assertNotIsSubclass(int, typing.Reversible)
    def test_supports_index(self):
        self.assertIsSubclass(int, typing.SupportsIndex)
        self.assertNotIsSubclass(str, typing.SupportsIndex)
    def test_bundled_protocol_instance_works(self):
        self.assertIsInstance(0, typing.SupportsAbs)
        class C1(typing.SupportsInt):
            def __int__(self):
                assert(false)
        class C2(C1):
            pass
        c = C2()
        self.assertIsInstance(c, C1)
    def test_collections_protocols_allowed(self):
        def Custom():
            assert(false)
        class A():
            pass
        class B():
            def __iter__(self):
                assert(false)
            def close(self):
                assert(false)
        self.assertIsSubclass(B, Custom)
        self.assertNotIsSubclass(A, Custom)
    def test_builtin_protocol_allowlist(self):
        assert(false)
        class CustomContextManager(typing.ContextManager, Protocol):
            pass
    def test_non_runtime_protocol_isinstance_check(self):
        class P(Protocol):
            assert(false)
        assert(false)
    def test_super_call_init(self):
        class P(Protocol):
            assert(false)
        class Foo(P):
            def __init__(self):
                super().__init__()
        Foo()
class GenericTests(BaseTestCase):
    def test_basics(self):
        X = None
        self.assertEqual(X.__parameters__, None)
        assert(false)
        assert(false)
        Y = None
        self.assertEqual(Y.__parameters__, None)
        None
        assert(false)
        SM1 = None
        assert(false)
        self.assertIsInstance(SM1(), SimpleMapping)
        T = TypeVar("T")
        self.assertEqual(None.__parameters__, None)
    def test_generic_errors(self):
        T = TypeVar('T')
        S = TypeVar('S')
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_init(self):
        T = TypeVar('T')
        S = TypeVar('S')
        assert(false)
        assert(false)
    def test_init_subclass(self):
        class X(None):
            def __init_subclass__(cls, /):
                assert(false)
        class Y(X):
            pass
        self.assertEqual(Y.attr, 42)
        assert(false)
        assert(false)
        assert(false)
        class Z(Y):
            pass
        class W(None):
            pass
        self.assertEqual(Y.attr, 2)
        self.assertEqual(Z.attr, 42)
        self.assertEqual(W.attr, 42)
    def test_repr(self):
        pass
    def test_chain_repr(self):
        T = TypeVar('T')
        S = TypeVar('S')
        class C(None):
            pass
        X = None
        self.assertEqual(X, None)
        self.assertNotEqual(X, None)
        Y = None
        self.assertEqual(Y, None)
        self.assertNotEqual(Y, None)
        self.assertNotEqual(Y, None)
        Z = None
        self.assertEqual(Z, None)
        self.assertNotEqual(Z, None)
        self.assertNotEqual(Z, None)
        self.assertTrue(str(Z).endswith('.C[typing.Tuple[str, int]]'))
    def test_new_repr(self):
        T = TypeVar('T')
        U = None
        S = TypeVar('S')
        self.assertEqual(repr(List), 'typing.List')
        self.assertEqual(repr(None), 'typing.List[~T]')
        self.assertEqual(repr(None), 'typing.List[+U]')
        self.assertEqual(repr(None), 'typing.List[int]')
        self.assertEqual(repr(None), 'typing.List[int]')
    def test_new_repr_complex(self):
        T = TypeVar('T')
        TS = TypeVar('TS')
        self.assertEqual(repr(None), 'typing.Mapping[~TS, ~T]')
        self.assertEqual(repr(None), 'typing.List[typing.Tuple[int, ~T]]')
        self.assertEqual(repr(None), 'typing.List[typing.Tuple[typing.List[int], typing.List[int]]]')
    def test_new_repr_bare(self):
        T = TypeVar('T')
        self.assertEqual(repr(None), 'typing.Generic[~T]')
        self.assertEqual(repr(None), 'typing.Protocol[~T]')
        class C(None):
            
        repr(C.__mro__)
    def test_dict(self):
        T = TypeVar('T')
        class B(None):
            pass
        b = B()
        assert(false)
        self.assertEqual(b.__dict__, None)
        class C(None):
            pass
        c = C()
        assert(false)
        self.assertEqual(c.__dict__, None)
    def test_subscripted_generics_as_proxies(self):
        T = TypeVar('T')
        class C(None):
            x = 'def'
        self.assertEqual(None.x, 'def')
        self.assertEqual(None.x, 'def')
        assert(false)
        self.assertEqual(C.x, 'changed')
        self.assertEqual(None.x, 'changed')
        assert(false)
        self.assertEqual(C.z, 'new')
        self.assertEqual(None.z, 'new')
        self.assertEqual(C().x, 'changed')
        self.assertEqual(None().z, 'new')
        class D(None):
            pass
        self.assertEqual(None.x, 'changed')
        self.assertEqual(D.z, 'new')
        assert(false)
        assert(false)
        self.assertEqual(C.x, 'changed')
        self.assertEqual(None.z, 'new')
        self.assertEqual(D.x, 'from derived x')
        self.assertEqual(None.z, 'from derived z')
    def test_abc_registry_kept(self):
        T = TypeVar('T')
        class C(collections.abc.Mapping, None):
            
        C.register(int)
        self.assertIsInstance(1, C)
        None
        self.assertIsInstance(1, C)
        C._abc_registry_clear()
        C._abc_caches_clear()
    def test_false_subclasses(self):
        class MyMapping(None):
            pass
        self.assertNotIsInstance(None, MyMapping)
        self.assertNotIsSubclass(dict, MyMapping)
    def test_abc_bases(self):
        class MM(None):
            def __getitem__(self, k):
                assert(false)
            def __setitem__(self, k, v):
                pass
            def __delitem__(self, k):
                pass
            def __iter__(self):
                assert(false)
            def __len__(self):
                assert(false)
        MM().update()
        self.assertIsInstance(MM(), collections.abc.MutableMapping)
        self.assertIsInstance(MM(), MutableMapping)
        self.assertNotIsInstance(MM(), List)
        self.assertNotIsInstance(None, MM)
    def test_multiple_bases(self):
        class MM1(None, collections.abc.MutableMapping):
            pass
        class MM2(collections.abc.MutableMapping, None):
            pass
        self.assertEqual(MM2.__bases__, None)
    def test_orig_bases(self):
        T = TypeVar('T')
        class C(None):
            
        self.assertEqual(C.__orig_bases__, None)
    def test_naive_runtime_checks(self):
        def naive_dict_check(obj, tp):
            if ((len(tp.__parameters__) > 0)):
                assert(false)
            else:
                pass
            if (tp.__args__):
                assert(false)
            else:
                pass
        self.assertTrue(naive_dict_check(None, None))
        self.assertFalse(naive_dict_check(None, None))
        assert(false)
        def naive_generic_check(obj, tp):
            if (not hasattr(obj, '__orig_class__')):
                assert(false)
            else:
                pass
            assert(false)
        class Node(None):
            
        self.assertTrue(naive_generic_check(None(), None))
        self.assertFalse(naive_generic_check(None(), None))
        self.assertFalse(naive_generic_check(None(), List))
        assert(false)
        class C(None):
            
        self.assertTrue(naive_list_base_check(None, C))
        self.assertFalse(naive_list_base_check(None, C))
    def test_multi_subscr_base(self):
        T = TypeVar('T')
        U = TypeVar('U')
        V = TypeVar('V')
        class C(None):
            
        class D(C, None):
            
        self.assertEqual(C.__parameters__, None)
        self.assertEqual(D.__parameters__, None)
        self.assertEqual(None.__parameters__, None)
        self.assertEqual(None.__parameters__, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(C.__bases__, None)
        self.assertEqual(D.__bases__, None)
        self.assertEqual(C.__orig_bases__, None)
        self.assertEqual(D.__orig_bases__, None)
    def test_subscript_meta(self):
        T = TypeVar('T')
        class Meta(type):
            
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None.__args__, None)
    def test_generic_hashes(self):
        class A(None):
            
        class B(None):
            class A(None):
                
        self.assertEqual(A, A)
        self.assertEqual(None, None)
        self.assertEqual(B.A, B.A)
        self.assertEqual(None, None)
        self.assertNotEqual(A, B.A)
        self.assertNotEqual(A, mod_generics_cache.A)
        self.assertNotEqual(A, mod_generics_cache.B.A)
        self.assertNotEqual(B.A, mod_generics_cache.A)
        self.assertNotEqual(B.A, mod_generics_cache.B.A)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        if ((None > None)):
            self.assertTrue(repr(None).endswith('<locals>.A[str]]'))
            self.assertTrue(repr(None).endswith('<locals>.B.A[str]]'))
            self.assertTrue(repr(None).endswith('mod_generics_cache.A[str]]'))
            self.assertTrue(repr(None).endswith('mod_generics_cache.B.A[str]]'))
        else:
            pass
    def test_extended_generic_rules_eq(self):
        T = TypeVar('T')
        U = TypeVar('U')
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        assert(false)
        self.assertEqual(None, int)
        self.assertEqual(None, None)
        class Base():
            
        class Derived(Base):
            
        self.assertEqual(None, None)
        assert(false)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_extended_generic_rules_repr(self):
        T = TypeVar('T')
        self.assertEqual(repr(None).replace('typing.', ''), 'Union[Tuple, Callable]')
        self.assertEqual(repr(None).replace('typing.', ''), 'Union[Tuple, Tuple[int]]')
        self.assertEqual(repr(None).replace('typing.', ''), 'Callable[..., Optional[int]]')
        self.assertEqual(repr(None).replace('typing.', ''), 'Callable[[], List[int]]')
    def test_generic_forward_ref(self):
        def foobar(x):
            
        def foobar2(x):
            
        def foobar3(x):
            
        class CC():
            
        self.assertEqual(get_type_hints(foobar, globals(), locals()), None)
        self.assertEqual(get_type_hints(foobar2, globals(), locals()), None)
        self.assertEqual(get_type_hints(foobar3, globals(), locals()), None)
        T = TypeVar('T')
        AT = None
        def barfoo(x):
            
        self.assertIs(None, AT)
        CT = None
        def barfoo2(x):
            
        self.assertIs(None, CT)
    def test_extended_generic_rules_subclassing(self):
        class T1(None):
            
        class T2(None):
            
        class C1(None):
            def __contains__(self, item):
                assert(false)
        self.assertEqual(T1.__parameters__, None)
        self.assertEqual(None.__args__, None)
        self.assertEqual(None.__origin__, T1)
        self.assertEqual(T2.__parameters__, None)
        self.assertEqual(None, 'C1[int]')
        self.assertEqual(C1.__parameters__, None)
        self.assertIsInstance(C1(), collections.abc.Container)
        self.assertIsSubclass(C1, collections.abc.Container)
        self.assertIsInstance(T1(), tuple)
        self.assertIsSubclass(T2, tuple)
        assert(false)
        assert(false)
    def test_fail_with_bare_union(self):
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_fail_with_bare_generic(self):
        T = TypeVar('T')
        assert(false)
        assert(false)
        assert(false)
    def test_type_erasure_special(self):
        T = TypeVar('T')
        self.clear_caches()
        class MyTup(None):
            
        self.assertIs(None().__class__, MyTup)
        self.assertEqual(None().__orig_class__, None)
        class MyDict(None):
            
        self.assertIs(None().__class__, MyDict)
        self.assertEqual(None().__orig_class__, None)
        class MyDef(None):
            
        self.assertIs(None().__class__, MyDef)
        self.assertEqual(None().__orig_class__, None)
        if ((sys.version_info >= None)):
            class MyChain(None):
                
            self.assertIs(None().__class__, MyChain)
            self.assertEqual(None().__orig_class__, None)
        else:
            pass
    def test_all_repr_eq_any(self):
        objs = None
        assert(false)
    def test_pickle(self):
        assert(false)
        T = TypeVar('T')
        class B(None):
            pass
        class C(None):
            pass
        c = C()
        assert(false)
        assert(false)
        assert(false)
        samples = None
        assert(false)
        more_samples = None
        assert(false)
    def test_copy_and_deepcopy(self):
        T = TypeVar('T')
        class Node(None):
            
        things = None
        assert(false)
    def test_immutability_by_copy_and_pickle(self):
        assert(false)
        TP = TypeVar('TP')
        TPB = None
        TPV = TypeVar('TPV', bytes, str)
        assert(false)
        TL = TypeVar('TL')
        TLB = None
        TLV = TypeVar('TLV', bytes, str)
        assert(false)
    def test_copy_generic_instances(self):
        T = TypeVar('T')
        class C(None):
            def __init__(self, attr):
                assert(false)
        c = C(42)
        self.assertEqual(copy(c).attr, 42)
        self.assertEqual(deepcopy(c).attr, 42)
        self.assertIsNot(copy(c), c)
        self.assertIsNot(deepcopy(c), c)
        assert(false)
        self.assertEqual(copy(c).attr, 1)
        self.assertEqual(deepcopy(c).attr, 1)
        ci = None(42)
        self.assertEqual(copy(ci).attr, 42)
        self.assertEqual(deepcopy(ci).attr, 42)
        self.assertIsNot(copy(ci), ci)
        self.assertIsNot(deepcopy(ci), ci)
        assert(false)
        self.assertEqual(copy(ci).attr, 1)
        self.assertEqual(deepcopy(ci).attr, 1)
        self.assertEqual(ci.__orig_class__, None)
    def test_weakref_all(self):
        T = TypeVar('T')
        things = None
        assert(false)
    def test_parameterized_slots(self):
        T = TypeVar('T')
        class C(None):
            __slots__ = None
        c = C()
        c_int = None()
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        def foo(x):
            
        self.assertEqual(None, None)
        self.assertEqual(copy(None), deepcopy(None))
    def test_parameterized_slots_dict(self):
        T = TypeVar('T')
        class D(None):
            __slots__ = None
        d = D()
        d_int = None()
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_errors(self):
        assert(false)
    def test_repr_2(self):
        class C(None):
            pass
        self.assertEqual(C.__module__, __name__)
        self.assertEqual(C.__qualname__, 'GenericTests.test_repr_2.<locals>.C')
        X = None
        self.assertEqual(X.__module__, __name__)
        self.assertEqual(None, 'C[int]')
        class Y(None):
            pass
        self.assertEqual(Y.__module__, __name__)
        self.assertEqual(Y.__qualname__, 'GenericTests.test_repr_2.<locals>.Y')
    def test_eq_1(self):
        self.assertEqual(Generic, Generic)
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
    def test_eq_2(self):
        class A(None):
            pass
        class B(None):
            pass
        self.assertEqual(A, A)
        self.assertNotEqual(A, B)
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
    def test_multiple_inheritance(self):
        class A(None):
            pass
        class B(None):
            pass
        class C(None, None, None):
            pass
        self.assertEqual(C.__parameters__, None)
    def test_multiple_inheritance_special(self):
        S = TypeVar('S')
        class B(None):
            
        class C(None, B):
            
        self.assertEqual(C.__mro__, None)
    def test_init_subclass_super_called(self):
        class FinalException(Exception):
            pass
        class Final():
            def __init_subclass__(cls, /):
                assert(false)
        class Test(None, Final):
            pass
        assert(false)
        assert(false)
    def test_nested(self):
        G = Generic
        class Visitor(None):
            a = None
            def set(self, a):
                assert(false)
            def get(self):
                assert(false)
            def visit(self):
                assert(false)
        V = None
        class IntListVisitor(V):
            def append(self, x):
                self.a.append(x)
        a = IntListVisitor()
        a.set(None)
        a.append(1)
        a.append(42)
        self.assertEqual(a.get(), None)
    def test_type_erasure(self):
        T = TypeVar('T')
        class Node(None):
            def __init__(self, label, left, right, /):
                assert(false)
        def foo(x):
            a = Node(x)
            b = None(x)
            c = None(x)
            self.assertIs(type(a), Node)
            self.assertIs(type(b), Node)
            self.assertIs(type(c), Node)
            self.assertEqual(a.label, x)
            self.assertEqual(b.label, x)
            self.assertEqual(c.label, x)
        foo(42)
    def test_implicit_any(self):
        T = TypeVar('T')
        class C(None):
            pass
        class D(C):
            pass
        self.assertEqual(D.__parameters__, None)
        assert(false)
        assert(false)
        assert(false)
    def test_new_with_args(self):
        class A(None):
            pass
        class B():
            def __new__(cls, arg):
                obj = super().__new__(cls)
                assert(false)
                assert(false)
        class C(A, B):
            pass
        c = C('foo')
        self.assertEqual(c.arg, 'foo')
    def test_new_with_args2(self):
        class A():
            def __init__(self, arg):
                assert(false)
                super().__init__()
        class C(None, A):
            def __init__(self, arg):
                assert(false)
                super().__init__(arg)
        c = C('foo')
        self.assertEqual(c.from_a, 'foo')
        self.assertEqual(c.from_c, 'foo')
    def test_new_no_args(self):
        class A(None):
            pass
        assert(false)
        class B():
            def __new__(cls):
                obj = super().__new__(cls)
                assert(false)
                assert(false)
        class C(A, B):
            def __init__(self, arg):
                assert(false)
            def __new__(cls, arg):
                obj = super().__new__(cls)
                assert(false)
                assert(false)
        c = C('foo')
        self.assertEqual(c.arg, 'foo')
        self.assertEqual(c.from_b, 'b')
        self.assertEqual(c.from_c, 'c')
    def test_subclass_special_form(self):
        assert(false)
class ClassVarTests(BaseTestCase):
    def test_basics(self):
        assert(false)
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(ClassVar), 'typing.ClassVar')
        cv = None
        self.assertEqual(repr(cv), 'typing.ClassVar[int]')
        cv = None
        self.assertEqual(repr(cv), ('typing.ClassVar[%s.Employee]' % __name__))
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_cannot_init(self):
        assert(false)
        assert(false)
        assert(false)
    def test_no_isinstance(self):
        assert(false)
        assert(false)
class FinalTests(BaseTestCase):
    def test_basics(self):
        None
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(Final), 'typing.Final')
        cv = None
        self.assertEqual(repr(cv), 'typing.Final[int]')
        cv = None
        self.assertEqual(repr(cv), ('typing.Final[%s.Employee]' % __name__))
        cv = None
        self.assertEqual(repr(cv), 'typing.Final[tuple[int]]')
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_cannot_init(self):
        assert(false)
        assert(false)
        assert(false)
    def test_no_isinstance(self):
        assert(false)
        assert(false)
    def test_final_unmodified(self):
        def func(x):
            
        self.assertIs(func, final(func))
class CastTests(BaseTestCase):
    def test_basics(self):
        self.assertEqual(cast(int, 42), 42)
        self.assertEqual(cast(float, 42), 42)
        self.assertIs(type(cast(float, 42)), int)
        self.assertEqual(cast(Any, 42), 42)
        self.assertEqual(cast(list, 42), 42)
        self.assertEqual(cast(None, 42), 42)
        self.assertEqual(cast(AnyStr, 42), 42)
        self.assertEqual(cast(None, 42), 42)
    def test_errors(self):
        cast(42, 42)
        cast('hello', 42)
class ForwardRefTests(BaseTestCase):
    def test_basics(self):
        class Node(None):
            def __init__(self, label):
                assert(false)
                assert(false)
            def add_both(self, left, right, stuff, blah, /):
                assert(false)
            def add_left(self, node):
                self.add_both(node, None)
            def add_right(self, node, /):
                assert(false)
        t = None
        both_hints = get_type_hints(t.add_both, globals(), locals())
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertNotIn('blah', both_hints)
        left_hints = get_type_hints(t.add_left, globals(), locals())
        self.assertEqual(None, None)
        right_hints = get_type_hints(t.add_right, globals(), locals())
        self.assertEqual(None, None)
    def test_forwardref_instance_type_error(self):
        fr = typing.ForwardRef('int')
        assert(false)
    def test_forwardref_subclass_type_error(self):
        fr = typing.ForwardRef('int')
        assert(false)
    def test_forward_equality(self):
        fr = typing.ForwardRef('int')
        self.assertEqual(fr, typing.ForwardRef('int'))
        self.assertNotEqual(None, None)
    def test_forward_equality_gth(self):
        c1 = typing.ForwardRef('C')
        c1_gth = typing.ForwardRef('C')
        c2 = typing.ForwardRef('C')
        c2_gth = typing.ForwardRef('C')
        class C():
            pass
        def foo(a, b):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
        self.assertEqual(c1, c2)
        self.assertEqual(c1, c1_gth)
        self.assertEqual(c1_gth, c2_gth)
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_forward_equality_hash(self):
        c1 = typing.ForwardRef('int')
        c1_gth = typing.ForwardRef('int')
        c2 = typing.ForwardRef('int')
        c2_gth = typing.ForwardRef('int')
        def foo(a, b):
            pass
        get_type_hints(foo, globals(), locals())
        self.assertEqual(hash(c1), hash(c2))
        self.assertEqual(hash(c1_gth), hash(c2_gth))
        self.assertEqual(hash(c1), hash(c1_gth))
    def test_forward_equality_namespace(self):
        class A():
            pass
        def namespace1():
            a = typing.ForwardRef('A')
            def fun(x):
                pass
            get_type_hints(fun, globals(), locals())
            assert(false)
        def namespace2():
            a = typing.ForwardRef('A')
            class A():
                pass
            def fun(x):
                pass
            get_type_hints(fun, globals(), locals())
            assert(false)
        self.assertEqual(namespace1(), namespace1())
        self.assertNotEqual(namespace1(), namespace2())
    def test_forward_repr(self):
        self.assertEqual(repr(None), "typing.List[ForwardRef('int')]")
    def test_union_forward(self):
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_tuple_forward(self):
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_double_forward(self):
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_forward_recursion_actually(self):
        def namespace1():
            a = typing.ForwardRef('A')
            A = a
            def fun(x):
                pass
            ret = get_type_hints(fun, globals(), locals())
            assert(false)
        def namespace2():
            a = typing.ForwardRef('A')
            A = a
            def fun(x):
                pass
            ret = get_type_hints(fun, globals(), locals())
            assert(false)
        def cmp(o1, o2):
            assert(false)
        r1 = namespace1()
        r2 = namespace2()
        self.assertIsNot(r1, r2)
        self.assertRaises(RecursionError, cmp, r1, r2)
    def test_union_forward_recursion(self):
        ValueList = None
        Value = None
        class C():
            assert(false)
        class D():
            assert(false)
        class E():
            assert(false)
        class F():
            assert(false)
        self.assertEqual(get_type_hints(C, globals(), locals()), get_type_hints(C, globals(), locals()))
        self.assertEqual(get_type_hints(C, globals(), locals()), None)
        self.assertEqual(get_type_hints(D, globals(), locals()), None)
        self.assertEqual(get_type_hints(E, globals(), locals()), None)
        self.assertEqual(get_type_hints(F, globals(), locals()), None)
    def test_callable_forward(self):
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_callable_with_ellipsis_forward(self):
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), None)
    def test_syntax_error(self):
        assert(false)
    def test_delayed_syntax_error(self):
        def foo(a):
            pass
        assert(false)
    def test_type_error(self):
        def foo(a):
            pass
        assert(false)
    def test_name_error(self):
        def foo(a):
            pass
        assert(false)
    def test_no_type_check(self):
        def foo(a, /):
            assert(false)
        th = get_type_hints(foo)
        self.assertEqual(th, None)
    def test_no_type_check_class(self):
        def C():
            assert(false)
        cth = get_type_hints(C.foo)
        self.assertEqual(cth, None)
        ith = get_type_hints(C().foo)
        self.assertEqual(ith, None)
    def test_no_type_check_no_bases(self):
        class C():
            def meth(self, x):
                
        def D():
            assert(false)
        self.assertEqual(get_type_hints(C.meth), None)
    def test_no_type_check_forward_ref_as_string(self):
        class C():
            assert(false)
        class D():
            assert(false)
        class E():
            assert(false)
        class F():
            assert(false)
        expected_result = None
        assert(false)
    def test_nested_classvar_fails_forward_ref_check(self):
        class E():
            assert(false)
        class F():
            assert(false)
        assert(false)
    def test_meta_no_type_check(self):
        def magic_decorator(func, /):
            assert(false)
        self.assertEqual(magic_decorator.__name__, 'magic_decorator')
        def foo(a, /):
            assert(false)
        def C():
            assert(false)
        self.assertEqual(foo.__name__, 'foo')
        th = get_type_hints(foo)
        self.assertEqual(th, None)
        cth = get_type_hints(C.foo)
        self.assertEqual(cth, None)
        ith = get_type_hints(C().foo)
        self.assertEqual(ith, None)
    def test_default_globals(self):
        code = "class C:\n""    def foo(self, a: 'C') -> 'D': pass\n""class D:\n""    def bar(self, b: 'D') -> C: pass\n"
        ns = None
        exec(code, ns)
        hints = get_type_hints(None.foo)
        self.assertEqual(hints, None)
    def test_final_forward_ref(self):
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, Final)
    def test_or(self):
        X = ForwardRef('X')
        self.assertEqual((X | "x"), None)
        self.assertEqual(("x" | X), None)
class OverloadTests(BaseTestCase):
    def test_overload_fails(self):
        assert(false)
        assert(false)
    def test_overload_succeeds(self):
        assert(false)
        def blah():
            assert(false)
        def blah():
            pass
        blah()
ASYNCIO_TESTS = """
import asyncio

T_a = TypeVar('T_a')

class AwaitableWrapper(typing.Awaitable[T_a]):

    def __init__(self, value):
        self.value = value

    def __await__(self) -> typing.Iterator[T_a]:
        yield
        return self.value

class AsyncIteratorWrapper(typing.AsyncIterator[T_a]):

    def __init__(self, value: typing.Iterable[T_a]):
        self.value = value

    def __aiter__(self) -> typing.AsyncIterator[T_a]:
        return self

    async def __anext__(self) -> T_a:
        data = await self.value
        if data:
            return data
        else:
            raise StopAsyncIteration

class ACM:
    async def __aenter__(self) -> int:
        return 42
    async def __aexit__(self, etype, eval, tb):
        return None
"""
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
assert(false)
class A():
    assert(false)
class B(A):
    assert(false)
    assert(false)
    assert(false)
class CSub(B):
    assert(false)
class G(None):
    assert(false)
class Loop():
    assert(false)
class NoneAndForward():
    assert(false)
    assert(false)
class CoolEmployee(NamedTuple):
    assert(false)
    assert(false)
class CoolEmployeeWithDefault(NamedTuple):
    assert(false)
    assert(false)
class XMeth(NamedTuple):
    assert(false)
    def double(self):
        assert(false)
class XRepr(NamedTuple):
    assert(false)
    assert(false)
    def __str__(self):
        assert(false)
    def __add__(self, other):
        assert(false)
Label = TypedDict('Label', None)
class Point2D(TypedDict):
    assert(false)
    assert(false)
class LabelPoint2D(Point2D, Label):
    
class HasForeignBaseClass(mod_generics_cache.A):
    assert(false)
    assert(false)
assert(false)
gth = get_type_hints
class ForRefExample():
    def func(self, /):
        assert(false)
    def nested(self, /):
        assert(false)
class GetTypeHintTests(BaseTestCase):
    def test_get_type_hints_from_various_objects(self):
        assert(false)
        assert(false)
        assert(false)
    def test_get_type_hints_modules(self):
        ann_module_type_hints = None
        self.assertEqual(gth(ann_module), ann_module_type_hints)
        self.assertEqual(gth(ann_module2), None)
        self.assertEqual(gth(ann_module3), None)
    def test_get_type_hints_modules_forwardref(self, /):
        assert(false)
    def test_get_type_hints_classes(self):
        self.assertEqual(gth(ann_module.C), None)
        self.assertIsInstance(gth(ann_module.j_class), dict)
        self.assertEqual(gth(ann_module.M), None)
        self.assertEqual(gth(ann_module.D), None)
        self.assertEqual(gth(ann_module.Y), None)
        self.assertEqual(gth(ann_module.h_class), None)
        self.assertEqual(gth(ann_module.S), None)
        self.assertEqual(gth(ann_module.foo), None)
        self.assertEqual(gth(NoneAndForward), None)
        self.assertEqual(gth(HasForeignBaseClass), None)
        self.assertEqual(gth(XRepr.__new__), None)
        self.assertEqual(gth(mod_generics_cache.B), None)
    def test_respect_no_type_check(self):
        def NoTpCheck():
            assert(false)
        self.assertTrue(NoTpCheck.__no_type_check__)
        self.assertTrue(NoTpCheck.Inn.__init__.__no_type_check__)
        self.assertEqual(gth(ann_module2.NTC.meth), None)
        class ABase(None):
            def meth(x):
                
        def Der():
            assert(false)
        self.assertEqual(gth(ABase.meth), None)
    def test_get_type_hints_for_builtins(self):
        self.assertEqual(gth(int), None)
        self.assertEqual(gth(type), None)
        self.assertEqual(gth(dir), None)
        self.assertEqual(gth(len), None)
        self.assertEqual(gth(object.__str__), None)
        self.assertEqual(gth(object().__str__), None)
        self.assertEqual(gth(str.join), None)
    def test_previous_behavior(self):
        def testf(x, y):
            
        assert(false)
        self.assertEqual(gth(testf), None)
        def testg(x):
            
        self.assertEqual(gth(testg), None)
    def test_get_type_hints_for_object_with_annotations(self):
        class A():
            
        class B():
            
        b = B()
        assert(false)
        self.assertEqual(gth(b, locals()), None)
    def test_get_type_hints_ClassVar(self):
        self.assertEqual(gth(ann_module2.CV, ann_module2.__dict__), None)
        self.assertEqual(gth(B, globals()), None)
        self.assertEqual(gth(CSub, globals()), None)
        self.assertEqual(gth(G), None)
    def test_get_type_hints_wrapped_decoratored_func(self):
        expects = None
        self.assertEqual(gth(ForRefExample.func), expects)
        self.assertEqual(gth(ForRefExample.nested), expects)
    def test_get_type_hints_annotated(self):
        def foobar(x):
            
        X = None
        self.assertEqual(get_type_hints(foobar, globals(), locals()), None)
        self.assertEqual(None, None)
        def foobar(x):
            
        X = None
        self.assertEqual(get_type_hints(foobar, globals(), locals()), None)
        self.assertEqual(None, None)
        BA = None
        def barfoo(x):
            
        self.assertEqual(None, None)
        self.assertIs(None, BA)
        BA = None
        def barfoo(x):
            
        self.assertEqual(None, None)
        self.assertIs(None, BA)
        def barfoo2(x, y):
            
        self.assertEqual(get_type_hints(barfoo2, globals(), locals()), None)
        BA2 = None
        def barfoo3(x):
            
        self.assertIs(None, BA2)
        BA3 = None
        def barfoo4(x):
            
        self.assertEqual(get_type_hints(barfoo4, globals(), locals()), None)
        self.assertEqual(None, None)
    def test_get_type_hints_annotated_refs(self):
        Const = None
        class MySet(None):
            def __ior__(self, other):
                
            def __iand__(self, other):
                
        self.assertEqual(get_type_hints(MySet.__iand__, globals(), locals()), None)
        self.assertEqual(None, None)
        self.assertEqual(get_type_hints(MySet.__ior__, globals(), locals()), None)
    def test_get_type_hints_classes_str_annotations(self):
        class Foo():
            y = str
            assert(false)
        self.assertEqual(get_type_hints(Foo), None)
    def test_get_type_hints_bad_module(self):
        class BadModule():
            pass
        assert(false)
        self.assertNotIn('bad', sys.modules)
        self.assertEqual(get_type_hints(BadModule), None)
    def test_get_type_hints_annotated_bad_module(self):
        class BadBase():
            assert(false)
        class BadType(BadBase):
            assert(false)
        assert(false)
        self.assertNotIn('bad', sys.modules)
        self.assertEqual(get_type_hints(BadType), None)
    def test_forward_ref_and_final(self):
        hints = get_type_hints(ann_module5)
        self.assertEqual(hints, None)
        hints = get_type_hints(ann_module5.MyClass)
        self.assertEqual(hints, None)
    def test_top_level_class_var(self):
        assert(false)
class GetUtilitiesTestCase(TestCase):
    def test_get_origin(self):
        T = TypeVar('T')
        P = ParamSpec('P')
        class C(None):
            pass
        self.assertIs(get_origin(None), C)
        self.assertIs(get_origin(None), C)
        self.assertIs(get_origin(int), None)
        self.assertIs(get_origin(None), ClassVar)
        self.assertIs(get_origin(None), Union)
        self.assertIs(get_origin(None), Literal)
        self.assertIs(get_origin(None), Final)
        self.assertIs(get_origin(Generic), Generic)
        self.assertIs(get_origin(None), Generic)
        self.assertIs(get_origin(None), list)
        self.assertIs(get_origin(None), Annotated)
        self.assertIs(get_origin(List), list)
        self.assertIs(get_origin(Tuple), tuple)
        self.assertIs(get_origin(Callable), collections.abc.Callable)
        self.assertIs(get_origin(None), list)
        self.assertIs(get_origin(list), None)
        self.assertIs(get_origin((list | str)), types.UnionType)
        self.assertIs(get_origin(P.args), P)
        self.assertIs(get_origin(P.kwargs), P)
    def test_get_args(self):
        T = TypeVar('T')
        class C(None):
            pass
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(int), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(List), None)
        self.assertEqual(get_args(Tuple), None)
        self.assertEqual(get_args(Callable), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(list), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), get_args(None))
        P = ParamSpec('P')
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args(None), None)
        self.assertEqual(get_args((list | str)), None)
class CollectionsAbcTests(BaseTestCase):
    def test_hashable(self):
        self.assertIsInstance(42, typing.Hashable)
        self.assertNotIsInstance(None, typing.Hashable)
    def test_iterable(self):
        self.assertIsInstance(None, typing.Iterable)
        self.assertIsInstance(None, typing.Iterable)
        self.assertIsInstance(None, typing.Iterable)
        self.assertNotIsInstance(42, typing.Iterable)
        self.assertIsSubclass(list, typing.Iterable)
        self.assertIsSubclass(list, typing.Iterable)
    def test_iterator(self):
        it = iter(None)
        self.assertIsInstance(it, typing.Iterator)
        self.assertNotIsInstance(42, typing.Iterator)
    def test_awaitable(self, /):
        assert(false)
    def test_coroutine(self, /):
        assert(false)
    def test_async_iterable(self, /):
        assert(false)
    def test_async_iterator(self, /):
        assert(false)
    def test_sized(self):
        self.assertIsInstance(None, typing.Sized)
        self.assertNotIsInstance(42, typing.Sized)
    def test_container(self):
        self.assertIsInstance(None, typing.Container)
        self.assertNotIsInstance(42, typing.Container)
    def test_collection(self):
        if (hasattr(typing, 'Collection')):
            self.assertIsInstance(tuple(), typing.Collection)
            self.assertIsInstance(frozenset(), typing.Collection)
            self.assertIsSubclass(dict, typing.Collection)
            self.assertNotIsInstance(42, typing.Collection)
        else:
            pass
    def test_abstractset(self):
        self.assertIsInstance(set(), typing.AbstractSet)
        self.assertNotIsInstance(42, typing.AbstractSet)
    def test_mutableset(self):
        self.assertIsInstance(set(), typing.MutableSet)
        self.assertNotIsInstance(frozenset(), typing.MutableSet)
    def test_mapping(self):
        self.assertIsInstance(None, typing.Mapping)
        self.assertNotIsInstance(42, typing.Mapping)
    def test_mutablemapping(self):
        self.assertIsInstance(None, typing.MutableMapping)
        self.assertNotIsInstance(42, typing.MutableMapping)
    def test_sequence(self):
        self.assertIsInstance(None, typing.Sequence)
        self.assertNotIsInstance(42, typing.Sequence)
    def test_mutablesequence(self):
        self.assertIsInstance(None, typing.MutableSequence)
        self.assertNotIsInstance(None, typing.MutableSequence)
    def test_bytestring(self):
        self.assertIsInstance(typing.ByteString)
        self.assertIsInstance(typing.ByteString)
    def test_list(self):
        self.assertIsSubclass(list, typing.List)
    def test_deque(self):
        self.assertIsSubclass(collections.deque, typing.Deque)
        class MyDeque(None):
            
        self.assertIsInstance(MyDeque(), collections.deque)
    def test_counter(self):
        self.assertIsSubclass(collections.Counter, typing.Counter)
    def test_set(self):
        self.assertIsSubclass(set, typing.Set)
        self.assertNotIsSubclass(frozenset, typing.Set)
    def test_frozenset(self):
        self.assertIsSubclass(frozenset, typing.FrozenSet)
        self.assertNotIsSubclass(set, typing.FrozenSet)
    def test_dict(self):
        self.assertIsSubclass(dict, typing.Dict)
    def test_dict_subscribe(self):
        K = TypeVar('K')
        V = TypeVar('V')
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_no_list_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_list_subclass(self):
        class MyList(None):
            pass
        a = MyList()
        self.assertIsInstance(a, MyList)
        self.assertIsInstance(a, typing.Sequence)
        self.assertIsSubclass(MyList, list)
        self.assertNotIsSubclass(list, MyList)
    def test_no_dict_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_dict_subclass(self):
        class MyDict(None):
            pass
        d = MyDict()
        self.assertIsInstance(d, MyDict)
        self.assertIsInstance(d, typing.MutableMapping)
        self.assertIsSubclass(MyDict, dict)
        self.assertNotIsSubclass(dict, MyDict)
    def test_defaultdict_instantiation(self):
        self.assertIs(type(typing.DefaultDict()), collections.defaultdict)
        self.assertIs(type(None()), collections.defaultdict)
        self.assertIs(type(None()), collections.defaultdict)
    def test_defaultdict_subclass(self):
        class MyDefDict(None):
            pass
        dd = MyDefDict()
        self.assertIsInstance(dd, MyDefDict)
        self.assertIsSubclass(MyDefDict, collections.defaultdict)
        self.assertNotIsSubclass(collections.defaultdict, MyDefDict)
    def test_ordereddict_instantiation(self):
        self.assertIs(type(typing.OrderedDict()), collections.OrderedDict)
        self.assertIs(type(None()), collections.OrderedDict)
        self.assertIs(type(None()), collections.OrderedDict)
    def test_ordereddict_subclass(self):
        class MyOrdDict(None):
            pass
        od = MyOrdDict()
        self.assertIsInstance(od, MyOrdDict)
        self.assertIsSubclass(MyOrdDict, collections.OrderedDict)
        self.assertNotIsSubclass(collections.OrderedDict, MyOrdDict)
    def test_chainmap_instantiation(self, /):
        assert(false)
    def test_chainmap_subclass(self, /):
        assert(false)
    def test_deque_instantiation(self):
        self.assertIs(type(typing.Deque()), collections.deque)
        self.assertIs(type(None()), collections.deque)
        self.assertIs(type(None()), collections.deque)
        class D(None):
            
        self.assertIs(type(None()), D)
    def test_counter_instantiation(self):
        self.assertIs(type(typing.Counter()), collections.Counter)
        self.assertIs(type(None()), collections.Counter)
        self.assertIs(type(None()), collections.Counter)
        class C(None):
            
        self.assertIs(type(None()), C)
    def test_counter_subclass_instantiation(self):
        class MyCounter(None):
            pass
        d = MyCounter()
        self.assertIsInstance(d, MyCounter)
        self.assertIsInstance(d, typing.Counter)
        self.assertIsInstance(d, collections.Counter)
    def test_no_set_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_set_subclass_instantiation(self):
        class MySet(None):
            pass
        d = MySet()
        self.assertIsInstance(d, MySet)
    def test_no_frozenset_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_frozenset_subclass_instantiation(self):
        class MyFrozenSet(None):
            pass
        d = MyFrozenSet()
        self.assertIsInstance(d, MyFrozenSet)
    def test_no_tuple_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_generator(self):
        def foo():
            assert(false)
        g = foo()
        self.assertIsSubclass(type(g), typing.Generator)
    def test_no_generator_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_async_generator(self):
        ns = None
        exec("async def f():\n""    yield 42\n", globals(), ns)
        g = None()
        self.assertIsSubclass(type(g), typing.AsyncGenerator)
    def test_no_async_generator_instantiation(self):
        assert(false)
        assert(false)
        assert(false)
    def test_subclassing(self):
        class MMA(typing.MutableMapping):
            pass
        assert(false)
        class MMC(MMA):
            def __getitem__(self, k):
                assert(false)
            def __setitem__(self, k, v):
                pass
            def __delitem__(self, k):
                pass
            def __iter__(self):
                assert(false)
            def __len__(self):
                assert(false)
        self.assertEqual(len(MMC()), 0)
        assert(false)
        self.assertIsInstance(MMC(), typing.Mapping)
        class MMB(None):
            def __getitem__(self, k):
                assert(false)
            def __setitem__(self, k, v):
                pass
            def __delitem__(self, k):
                pass
            def __iter__(self):
                assert(false)
            def __len__(self):
                assert(false)
        self.assertEqual(len(MMB()), 0)
        self.assertEqual(len(None()), 0)
        self.assertEqual(len(None()), 0)
        self.assertNotIsSubclass(dict, MMA)
        self.assertNotIsSubclass(dict, MMB)
        self.assertIsSubclass(MMA, typing.Mapping)
        self.assertIsSubclass(MMB, typing.Mapping)
        self.assertIsSubclass(MMC, typing.Mapping)
        self.assertIsInstance(None(), typing.Mapping)
        self.assertIsInstance(None(), collections.abc.Mapping)
        self.assertIsSubclass(MMA, collections.abc.Mapping)
        self.assertIsSubclass(MMB, collections.abc.Mapping)
        self.assertIsSubclass(MMC, collections.abc.Mapping)
        assert(false)
        self.assertIsSubclass(MMC, MMA)
        class I(typing.Iterable):
            
        self.assertNotIsSubclass(list, I)
        class G(None):
            
        def g():
            assert(false)
        self.assertIsSubclass(G, typing.Generator)
        self.assertIsSubclass(G, typing.Iterable)
        self.assertIsSubclass(G, collections.abc.Generator)
        self.assertIsSubclass(G, collections.abc.Iterable)
        self.assertNotIsSubclass(type(g), G)
    def test_subclassing_async_generator(self):
        class G(None):
            def asend(self, value):
                pass
            def athrow(self, typ, val, tb, /):
                assert(false)
        ns = None
        exec('async def g(): yield 0', globals(), ns)
        g = None
        self.assertIsSubclass(G, typing.AsyncGenerator)
        self.assertIsSubclass(G, typing.AsyncIterable)
        self.assertIsSubclass(G, collections.abc.AsyncGenerator)
        self.assertIsSubclass(G, collections.abc.AsyncIterable)
        self.assertNotIsSubclass(type(g), G)
        instance = G()
        self.assertIsInstance(instance, typing.AsyncGenerator)
        self.assertIsInstance(instance, typing.AsyncIterable)
        self.assertIsInstance(instance, collections.abc.AsyncGenerator)
        self.assertIsInstance(instance, collections.abc.AsyncIterable)
        self.assertNotIsInstance(type(g), G)
        self.assertNotIsInstance(g, G)
    def test_subclassing_subclasshook(self):
        class Base(typing.Iterable):
            def __subclasshook__(cls, other, /):
                assert(false)
        class C(Base):
            
        class Foo():
            
        class Bar():
            
        self.assertIsSubclass(Foo, Base)
        self.assertIsSubclass(Foo, C)
        self.assertNotIsSubclass(Bar, C)
    def test_subclassing_register(self):
        class A(typing.Container):
            
        class B(A):
            
        class C():
            
        A.register(C)
        self.assertIsSubclass(C, A)
        self.assertNotIsSubclass(C, B)
        class D():
            
        B.register(D)
        self.assertIsSubclass(D, A)
        self.assertIsSubclass(D, B)
        class M():
            
        collections.abc.MutableMapping.register(M)
        self.assertIsSubclass(M, typing.Mapping)
    def test_collections_as_base(self):
        class M(collections.abc.Mapping):
            
        self.assertIsSubclass(M, typing.Mapping)
        self.assertIsSubclass(M, typing.Iterable)
        class S(collections.abc.MutableSequence):
            
        self.assertIsSubclass(S, typing.MutableSequence)
        self.assertIsSubclass(S, typing.Iterable)
        class I(collections.abc.Iterable):
            
        self.assertIsSubclass(I, typing.Iterable)
        class B():
            
        A.register(B)
        self.assertIsSubclass(B, typing.Mapping)
class OtherABCTests(BaseTestCase):
    def test_contextmanager(self):
        def manager():
            assert(false)
        cm = manager()
        self.assertIsInstance(cm, typing.ContextManager)
        self.assertNotIsInstance(42, typing.ContextManager)
    def test_async_contextmanager(self, /):
        assert(false)
class TypeTests(BaseTestCase):
    def test_type_basic(self):
        class User():
            pass
        class BasicUser(User):
            pass
        class ProUser(User):
            pass
        def new_user(user_class):
            assert(false)
        new_user(BasicUser)
    def test_type_typevar(self):
        class User():
            pass
        class BasicUser(User):
            pass
        class ProUser(User):
            pass
        U = None
        def new_user(user_class):
            assert(false)
        new_user(BasicUser)
    def test_type_optional(self):
        A = None
        def foo(a):
            if (None):
                assert(false)
            else:
                assert(false)
        assert(false)
        assert(false)
class TestModules(TestCase):
    func_names = None
    def test_py_functions(self):
        assert(false)
    def test_c_functions(self, /):
        assert(false)
class NewTypeTests():
    def cleanup(self):
        assert(false)
    def setUpClass(cls, /):
        assert(false)
    def tearDownClass(cls, /):
        assert(false)
    def tearDown(self):
        self.cleanup()
    def test_basic(self):
        self.assertIsInstance(UserId(5), int)
        self.assertIsInstance(self.UserName('Joe'), str)
        self.assertEqual((UserId(5) + 1), 6)
    def test_errors(self):
        assert(false)
        assert(false)
    def test_or(self):
        assert(false)
    def test_special_attrs(self):
        self.assertEqual(UserId.__name__, 'UserId')
        self.assertEqual(UserId.__qualname__, 'UserId')
        self.assertEqual(UserId.__module__, __name__)
        self.assertEqual(UserId.__supertype__, int)
        UserName = self.UserName
        self.assertEqual(UserName.__name__, 'UserName')
        self.assertEqual(UserName.__qualname__, (self.__class__.__qualname__ + '.UserName'))
        self.assertEqual(UserName.__module__, __name__)
        self.assertEqual(UserName.__supertype__, str)
    def test_repr(self):
        self.assertEqual(repr(UserId))
        self.assertEqual(repr(self.UserName))
    def test_pickle(self):
        UserAge = self.module.NewType('UserAge', float)
        assert(false)
    def test_missing__name__(self):
        code = "import typing\n""NT = typing.NewType('NT', int)\n"
        exec(code, None)
class NewTypePythonTests(NewTypeTests, BaseTestCase):
    module = py_typing
def NewTypeCTests():
    assert(false)
class NamedTupleTests(BaseTestCase):
    class NestedEmployee(NamedTuple):
        assert(false)
        assert(false)
    def test_basics(self):
        Emp = NamedTuple('Emp', None)
        self.assertIsSubclass(Emp, tuple)
        joe = Emp('Joe', 42)
        jim = None
        self.assertIsInstance(joe, Emp)
        self.assertIsInstance(joe, tuple)
        self.assertEqual(joe.name, 'Joe')
        self.assertEqual(joe.id, 42)
        self.assertEqual(jim.name, 'Jim')
        self.assertEqual(jim.id, 1)
        self.assertEqual(Emp.__name__, 'Emp')
        self.assertEqual(Emp._fields, None)
        self.assertEqual(Emp.__annotations__, collections.OrderedDict(None))
    def test_namedtuple_pyversion(self):
        if ((None < None)):
            assert(false)
            assert(false)
        else:
            pass
    def test_annotation_usage(self):
        tim = CoolEmployee('Tim', 9000)
        self.assertIsInstance(tim, CoolEmployee)
        self.assertIsInstance(tim, tuple)
        self.assertEqual(tim.name, 'Tim')
        self.assertEqual(tim.cool, 9000)
        self.assertEqual(CoolEmployee.__name__, 'CoolEmployee')
        self.assertEqual(CoolEmployee._fields, None)
        self.assertEqual(CoolEmployee.__annotations__, None)
    def test_annotation_usage_with_default(self):
        jelle = CoolEmployeeWithDefault('Jelle')
        self.assertIsInstance(jelle, CoolEmployeeWithDefault)
        self.assertIsInstance(jelle, tuple)
        self.assertEqual(jelle.name, 'Jelle')
        self.assertEqual(jelle.cool, 0)
        cooler_employee = CoolEmployeeWithDefault('Sjoerd', 1)
        self.assertEqual(cooler_employee.cool, 1)
        self.assertEqual(CoolEmployeeWithDefault.__name__, 'CoolEmployeeWithDefault')
        self.assertEqual(CoolEmployeeWithDefault._fields, None)
        self.assertEqual(CoolEmployeeWithDefault.__annotations__, None)
        self.assertEqual(CoolEmployeeWithDefault._field_defaults, None)
        assert(false)
    def test_annotation_usage_with_methods(self):
        self.assertEqual(XMeth(1).double(), 2)
        self.assertEqual(XMeth(42).x, None)
        self.assertEqual(str(XRepr(42)), '42 -> 1')
        self.assertEqual((XRepr(1, 2) + XRepr(3)), 0)
        assert(false)
        assert(false)
    def test_multiple_inheritance(self):
        class A():
            pass
        assert(false)
    def test_namedtuple_keyword_usage(self):
        LocalEmployee = None
        nick = LocalEmployee('Nick', 25)
        self.assertIsInstance(nick, tuple)
        self.assertEqual(nick.name, 'Nick')
        self.assertEqual(LocalEmployee.__name__, 'LocalEmployee')
        self.assertEqual(LocalEmployee._fields, None)
        self.assertEqual(LocalEmployee.__annotations__, None)
        assert(false)
        assert(false)
    def test_namedtuple_special_keyword_names(self):
        NT = None
        self.assertEqual(NT.__name__, 'NT')
        self.assertEqual(NT._fields, None)
        a = None
        self.assertEqual(a.cls, str)
        self.assertEqual(a.self, 42)
        self.assertEqual(a.typename, 'foo')
        self.assertEqual(a.fields, None)
    def test_empty_namedtuple(self):
        NT = NamedTuple('NT')
        class CNT(NamedTuple):
            pass
        assert(false)
    def test_namedtuple_errors(self):
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_copy_and_pickle(self):
        assert(false)
        Emp = NamedTuple('Emp', None)
        assert(false)
class TypedDictTests(BaseTestCase):
    def test_basics_functional_syntax(self):
        Emp = TypedDict('Emp', None)
        self.assertIsSubclass(Emp, dict)
        self.assertIsSubclass(Emp, typing.MutableMapping)
        self.assertNotIsSubclass(Emp, collections.abc.Sequence)
        jim = None
        self.assertIs(type(jim), dict)
        self.assertEqual(None, 'Jim')
        self.assertEqual(None, 1)
        self.assertEqual(Emp.__name__, 'Emp')
        self.assertEqual(Emp.__module__, __name__)
        self.assertEqual(Emp.__bases__, None)
        self.assertEqual(Emp.__annotations__, None)
        self.assertEqual(Emp.__total__, True)
    def test_basics_keywords_syntax(self):
        Emp = None
        self.assertIsSubclass(Emp, dict)
        self.assertIsSubclass(Emp, typing.MutableMapping)
        self.assertNotIsSubclass(Emp, collections.abc.Sequence)
        jim = None
        self.assertIs(type(jim), dict)
        self.assertEqual(None, 'Jim')
        self.assertEqual(None, 1)
        self.assertEqual(Emp.__name__, 'Emp')
        self.assertEqual(Emp.__module__, __name__)
        self.assertEqual(Emp.__bases__, None)
        self.assertEqual(Emp.__annotations__, None)
        self.assertEqual(Emp.__total__, True)
    def test_typeddict_special_keyword_names(self):
        TD = None
        self.assertEqual(TD.__name__, 'TD')
        self.assertEqual(TD.__annotations__, None)
        a = None
        self.assertEqual(None, str)
        self.assertEqual(None, 42)
        self.assertEqual(None, 'foo')
        self.assertEqual(None, 53)
        self.assertEqual(None, None)
        self.assertEqual(None, None)
    def test_typeddict_create_errors(self):
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_typeddict_errors(self):
        Emp = TypedDict('Emp', None)
        self.assertEqual(TypedDict.__module__, 'typing')
        jim = None
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_py36_class_syntax_usage(self):
        self.assertEqual(LabelPoint2D.__name__, 'LabelPoint2D')
        self.assertEqual(LabelPoint2D.__module__, __name__)
        self.assertEqual(LabelPoint2D.__annotations__, None)
        self.assertEqual(LabelPoint2D.__bases__, None)
        self.assertEqual(LabelPoint2D.__total__, True)
        self.assertNotIsSubclass(LabelPoint2D, typing.Sequence)
        not_origin = None
        self.assertEqual(None, 0)
        self.assertEqual(None, 1)
        other = None
        self.assertEqual(None, 'hi')
    def test_pickle(self):
        assert(false)
        EmpD = None
        jane = EmpD(None)
        assert(false)
    def test_optional(self):
        EmpD = None
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
    def test_total(self):
        self.assertEqual(D(), None)
        self.assertEqual(None, None)
        self.assertEqual(D.__total__, False)
        self.assertEqual(D.__required_keys__, frozenset())
        self.assertEqual(D.__optional_keys__, None)
        self.assertEqual(Options(), None)
        self.assertEqual(None, None)
        self.assertEqual(Options.__total__, False)
        self.assertEqual(Options.__required_keys__, frozenset())
        self.assertEqual(Options.__optional_keys__, None)
        assert(false)
        assert(false)
    def test_keys_inheritance(self):
        class BaseAnimal(TypedDict):
            assert(false)
        class Cat(Animal):
            assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
        assert(false)
    def test_is_typeddict(self):
        assert(false)
        assert(false)
        assert(false)
    def test_get_type_hints(self):
        self.assertEqual(get_type_hints(Bar), None)
class IOTests(BaseTestCase):
    def test_io(self):
        def stuff(a):
            assert(false)
        a = None
        self.assertEqual(a.__parameters__, None)
    def test_textio(self):
        def stuff(a):
            assert(false)
        a = None
        self.assertEqual(a.__parameters__, None)
    def test_binaryio(self):
        def stuff(a):
            assert(false)
        a = None
        self.assertEqual(a.__parameters__, None)
    def test_io_submodule(self):
        assert(false)
class RETests(BaseTestCase):
    def test_basics(self):
        pat = re.compile('[a-z]+', re.I)
        self.assertIsSubclass(pat.__class__, Pattern)
        self.assertIsSubclass(type(pat), Pattern)
        self.assertIsInstance(pat, Pattern)
        mat = pat.search('12345abcde.....')
        self.assertIsSubclass(mat.__class__, Match)
        self.assertIsSubclass(type(mat), Match)
        self.assertIsInstance(mat, Match)
        None
        None
    def test_alias_equality(self):
        self.assertEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, str)
    def test_errors(self):
        m = None
        assert(false)
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(Pattern), 'typing.Pattern')
        self.assertEqual(repr(None), 'typing.Pattern[str]')
        self.assertEqual(repr(None), 'typing.Pattern[bytes]')
        self.assertEqual(repr(Match), 'typing.Match')
        self.assertEqual(repr(None), 'typing.Match[str]')
        self.assertEqual(repr(None), 'typing.Match[bytes]')
    def test_re_submodule(self):
        assert(false)
    def test_cannot_subclass(self):
        assert(false)
        self.assertEqual(str(ex.exception), "type 're.Match' is not an acceptable base type")
class AnnotatedTests(BaseTestCase):
    def test_repr(self):
        self.assertEqual(repr(None), "typing.Annotated[int, 4, 5]")
        self.assertEqual(repr(None), "typing.Annotated[typing.List[int], 4, 5]")
    def test_flatten(self):
        A = None
        self.assertEqual(A, None)
        self.assertEqual(A.__metadata__, None)
        self.assertEqual(A.__origin__, int)
    def test_specialize(self):
        L = None
        LI = None
        self.assertEqual(None, None)
        self.assertEqual(None.__metadata__, None)
        self.assertEqual(None.__origin__, None)
        assert(false)
        assert(false)
    def test_hash_eq(self):
        self.assertEqual(len(None), 1)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertEqual(None, None)
    def test_instantiate(self):
        class C():
            classvar = 4
            def __init__(self, x):
                assert(false)
            def __eq__(self, other):
                if (not isinstance(other, C)):
                    assert(false)
                else:
                    pass
                assert(false)
        A = None
        a = A(5)
        c = C(5)
        self.assertEqual(a, c)
        self.assertEqual(a.x, c.x)
        self.assertEqual(a.classvar, c.classvar)
    def test_instantiate_generic(self):
        MyCount = None
        self.assertEqual(MyCount(None), None)
        self.assertEqual(None(None), None)
    def test_cannot_instantiate_forward(self):
        A = None
        assert(false)
    def test_cannot_instantiate_type_var(self):
        A = None
        assert(false)
    def test_cannot_getattr_typevar(self):
        assert(false)
    def test_attr_passthrough(self):
        class C():
            classvar = 4
        A = None
        self.assertEqual(A.classvar, 4)
        assert(false)
        self.assertEqual(C.x, 5)
    def test_hash_eq(self):
        self.assertEqual(len(None), 1)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertNotEqual(None, None)
        self.assertEqual(None, None)
    def test_cannot_subclass(self):
        assert(false)
    def test_cannot_check_instance(self):
        assert(false)
    def test_cannot_check_subclass(self):
        assert(false)
    def test_pickle(self):
        samples = None
        assert(false)
        assert(false)
        class _Annotated_test_G(None):
            x = 1
        G = None
        assert(false)
        assert(false)
        assert(false)
    def test_subst(self):
        dec = "a decoration"
        dec2 = "another decoration"
        S = None
        self.assertEqual(None, None)
        self.assertEqual(None, None)
        L = None
        self.assertEqual(None, None)
        assert(false)
        self.assertEqual(None, None)
        D = None
        self.assertEqual(None, None)
        assert(false)
        It = None
        assert(false)
        LI = None
        assert(false)
    def test_annotated_in_other_types(self):
        X = None
        self.assertEqual(None, None)
    def test_annotated_mro(self):
        class X(None):
            
        self.assertEqual(X.__mro__, None, "Annotated should be transparent.")
class TypeAliasTests(BaseTestCase):
    def test_canonical_usage_with_variable_annotation(self):
        assert(false)
    def test_canonical_usage_with_type_comment(self):
        Alias = Employee
    def test_cannot_instantiate(self):
        assert(false)
    def test_no_isinstance(self):
        assert(false)
    def test_no_issubclass(self):
        assert(false)
        assert(false)
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_repr(self):
        self.assertEqual(repr(TypeAlias), 'typing.TypeAlias')
    def test_cannot_subscript(self):
        assert(false)
class ParamSpecTests(BaseTestCase):
    def test_basic_plain(self):
        P = ParamSpec('P')
        self.assertEqual(P, P)
        self.assertIsInstance(P, ParamSpec)
    def test_valid_uses(self):
        P = ParamSpec('P')
        T = TypeVar('T')
        C1 = None
        self.assertEqual(C1.__args__, None)
        self.assertEqual(C1.__parameters__, None)
        C2 = None
        self.assertEqual(C2.__args__, None)
        self.assertEqual(C2.__parameters__, None)
        C3 = None
        self.assertEqual(C3.__args__, None)
        self.assertEqual(C3.__parameters__, None)
        C4 = None
        self.assertEqual(C4.__args__, None)
        self.assertEqual(C4.__parameters__, None)
    def test_args_kwargs(self):
        P = ParamSpec('P')
        self.assertIn('args', dir(P))
        self.assertIn('kwargs', dir(P))
        self.assertIsInstance(P.args, ParamSpecArgs)
        self.assertIsInstance(P.kwargs, ParamSpecKwargs)
        self.assertIs(P.args.__origin__, P)
        self.assertIs(P.kwargs.__origin__, P)
        self.assertEqual(repr(P.args), "P.args")
        self.assertEqual(repr(P.kwargs), "P.kwargs")
    def test_user_generics(self):
        T = TypeVar("T")
        P = ParamSpec("P")
        P_2 = ParamSpec("P_2")
        class X(None):
            assert(false)
            assert(false)
        G1 = None
        self.assertEqual(G1.__args__, None)
        self.assertEqual(G1.__parameters__, None)
        assert(false)
        assert(false)
        G2 = None
        self.assertEqual(G2.__args__, None)
        self.assertEqual(G2.__parameters__, None)
        G3 = None
        self.assertEqual(G3.__args__, None)
        self.assertEqual(G3.__parameters__, None)
        G4 = None
        self.assertEqual(G4.__args__, None)
        self.assertEqual(G4.__parameters__, None)
        class Z(None):
            assert(false)
        G5 = None
        self.assertEqual(G5.__args__, None)
        self.assertEqual(G5.__parameters__, None)
        G6 = None
        self.assertEqual(G6.__args__, None)
        self.assertEqual(G6.__parameters__, None)
        self.assertEqual(G5.__args__, G6.__args__)
        self.assertEqual(G5.__origin__, G6.__origin__)
        self.assertEqual(G5.__parameters__, G6.__parameters__)
        self.assertEqual(G5, G6)
        G7 = None
        self.assertEqual(G7.__args__, None)
        self.assertEqual(G7.__parameters__, None)
        assert(false)
        assert(false)
    def test_multiple_paramspecs_in_user_generics(self):
        P = ParamSpec("P")
        P2 = ParamSpec("P2")
        class X(None):
            assert(false)
            assert(false)
        G1 = None
        G2 = None
        self.assertNotEqual(G1, G2)
        self.assertEqual(G1.__args__, None)
        self.assertEqual(G2.__args__, None)
    def test_no_paramspec_in__parameters__(self):
        T = TypeVar("T")
        P = ParamSpec("P")
        self.assertNotIn(P, None.__parameters__)
        self.assertIn(T, None.__parameters__)
        self.assertNotIn(P, None.__parameters__)
        self.assertIn(T, None.__parameters__)
        self.assertNotIn(P, (None | int).__parameters__)
        self.assertIn(T, (None | int).__parameters__)
    def test_paramspec_in_nested_generics(self):
        T = TypeVar("T")
        P = ParamSpec("P")
        C1 = None
        G1 = None
        G2 = None
        G3 = (None | int)
        self.assertEqual(G1.__parameters__, None)
        self.assertEqual(G2.__parameters__, None)
        self.assertEqual(G3.__parameters__, None)
class ConcatenateTests(BaseTestCase):
    def test_basics(self):
        P = ParamSpec('P')
        class MyClass():
            
        c = None
        self.assertNotEqual(c, Concatenate)
    def test_valid_uses(self):
        P = ParamSpec('P')
        T = TypeVar('T')
        C1 = None
        self.assertEqual(C1.__args__, None)
        self.assertEqual(C1.__parameters__, None)
        C2 = None
        self.assertEqual(C2.__args__, None)
        self.assertEqual(C2.__parameters__, None)
        C3 = None
        self.assertEqual(C3.__args__, None)
        self.assertEqual(C3.__parameters__, None)
        C4 = None
        self.assertEqual(C4.__args__, None)
        self.assertEqual(C4.__parameters__, None)
class TypeGuardTests(BaseTestCase):
    def test_basics(self):
        None
        def foo(arg):
            
        self.assertEqual(gth(foo), None)
    def test_repr(self):
        self.assertEqual(repr(TypeGuard), 'typing.TypeGuard')
        cv = None
        self.assertEqual(repr(cv), 'typing.TypeGuard[int]')
        cv = None
        self.assertEqual(repr(cv), ('typing.TypeGuard[%s.Employee]' % __name__))
        cv = None
        self.assertEqual(repr(cv), 'typing.TypeGuard[tuple[int]]')
    def test_cannot_subclass(self):
        assert(false)
        assert(false)
    def test_cannot_init(self):
        assert(false)
        assert(false)
        assert(false)
    def test_no_isinstance(self):
        assert(false)
        assert(false)
SpecialAttrsP = typing.ParamSpec('SpecialAttrsP')
SpecialAttrsT = typing.TypeVar('SpecialAttrsT', int, float, complex)
class SpecialAttrsTests(BaseTestCase):
    def test_special_attrs(self):
        cls_to_check = None
        assert(false)
    TypeName = typing.NewType('SpecialAttrsTests.TypeName', Any)
    def test_special_attrs2(self):
        fr = typing.ForwardRef('set[Any]')
        self.assertFalse(hasattr(fr, '__name__'))
        self.assertFalse(hasattr(fr, '__qualname__'))
        self.assertEqual(fr.__module__, 'typing')
        assert(false)
        self.assertEqual(SpecialAttrsTests.TypeName.__name__, 'TypeName')
        self.assertEqual(SpecialAttrsTests.TypeName.__qualname__, 'SpecialAttrsTests.TypeName')
        self.assertEqual(SpecialAttrsTests.TypeName.__module__, 'test.test_typing')
        assert(false)
        self.assertEqual(SpecialAttrsT.__name__, 'SpecialAttrsT')
        self.assertFalse(hasattr(SpecialAttrsT, '__qualname__'))
        self.assertEqual(SpecialAttrsT.__module__, 'test.test_typing')
        assert(false)
        self.assertEqual(SpecialAttrsP.__name__, 'SpecialAttrsP')
        self.assertFalse(hasattr(SpecialAttrsP, '__qualname__'))
        self.assertEqual(SpecialAttrsP.__module__, 'test.test_typing')
        assert(false)
class AllTests(BaseTestCase):
    """Tests for __all__."""
    def test_all(self):
        assert(false)
        self.assertIn('AbstractSet', a)
        self.assertIn('ValuesView', a)
        self.assertIn('cast', a)
        self.assertIn('overload', a)
        if (hasattr(contextlib, 'AbstractContextManager')):
            self.assertIn('ContextManager', a)
        else:
            pass
        self.assertNotIn('io', a)
        self.assertNotIn('re', a)
        self.assertNotIn('os', a)
        self.assertNotIn('sys', a)
        self.assertIn('Text', a)
        self.assertIn('SupportsBytes', a)
        self.assertIn('SupportsComplex', a)
    def test_all_exported_names(self):
        assert(false)
        actual_all = set(typing.__all__)
        computed_all = None
        self.assertSetEqual(computed_all, actual_all)
if ((__name__ == '__main__')):
    main()
else:
    pass