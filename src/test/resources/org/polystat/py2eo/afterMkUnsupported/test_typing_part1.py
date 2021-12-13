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