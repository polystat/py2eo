import builtins as builtins
from builtins import *
import sys as sys
import contextlib as contextlib
import collections as collections
import pickle as pickle
import re as re
import sys as sys
import warnings as warnings
from unittest import TestCase as TestCase
from unittest import main as main
from unittest import skipUnless as skipUnless
from unittest import skip as skip
from copy import copy as copy
from copy import deepcopy as deepcopy
from typing import Any as Any
from typing import NoReturn as NoReturn
from typing import TypeVar as TypeVar
from typing import AnyStr as AnyStr
from typing import T as T
from typing import KT as KT
from typing import VT as VT
from typing import Union as Union
from typing import Optional as Optional
from typing import Literal as Literal
from typing import Tuple as Tuple
from typing import List as List
from typing import Dict as Dict
from typing import MutableMapping as MutableMapping
from typing import Callable as Callable
from typing import Generic as Generic
from typing import ClassVar as ClassVar
from typing import Final as Final
from typing import final as final
from typing import Protocol as Protocol
from typing import cast as cast
from typing import runtime_checkable as runtime_checkable
from typing import get_type_hints as get_type_hints
from typing import get_origin as get_origin
from typing import get_args as get_args
from typing import is_typeddict as is_typeddict
from typing import no_type_check as no_type_check
from typing import no_type_check_decorator as no_type_check_decorator
from typing import Type as Type
from typing import NewType as NewType
from typing import NamedTuple as NamedTuple
from typing import TypedDict as TypedDict
from typing import IO as IO
from typing import TextIO as TextIO
from typing import BinaryIO as BinaryIO
from typing import Pattern as Pattern
from typing import Match as Match
from typing import Annotated as Annotated
from typing import ForwardRef as ForwardRef
from typing import TypeAlias as TypeAlias
from typing import ParamSpec as ParamSpec
from typing import Concatenate as Concatenate
from typing import ParamSpecArgs as ParamSpecArgs
from typing import ParamSpecKwargs as ParamSpecKwargs
from typing import TypeGuard as TypeGuard
import abc as abc
import typing as typing
import weakref as weakref
import types as types
from test.support import import_helper as import_helper
from test import mod_generics_cache as mod_generics_cache
from test import _typed_dict_helper as _typed_dict_helper
py_typing = import_helper.import_fresh_module('typing', blocked=['_typing',])
c_typing = import_helper.import_fresh_module('typing', fresh=['_typing',])
class BaseTestCase(TestCase):
    def assertIsSubclass(self, cls, class_or_tuple, msg = None):
        if (not issubclass(cls, class_or_tuple)):
            message = ('%r is not a subclass of %r' % (cls, class_or_tuple))
            if ((msg is not None )):
                message += (' : %s' % msg)
            else:
                pass
            raise self.failureException(message)
        else:
            pass
    def assertNotIsSubclass(self, cls, class_or_tuple, msg = None):
        if (issubclass(cls, class_or_tuple)):
            message = ('%r is a subclass of %r' % (cls, class_or_tuple))
            if ((msg is not None )):
                message += (' : %s' % msg)
            else:
                pass
            raise self.failureException(message)
        else:
            pass
    def clear_caches(self):
        for f in typing._cleanups:
            f()
        else:
            pass
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
        with self.assertRaises(TypeError):
            isinstance(42, Any)
    def test_any_subclass_type_error(self):
        with self.assertRaises(TypeError):
            issubclass(Employee, Any)
        with self.assertRaises(TypeError):
            issubclass(Any, Employee)
    def test_repr(self):
        self.assertEqual(repr(Any), 'typing.Any')
    def test_errors(self):
        with self.assertRaises(TypeError):
            issubclass(42, Any)
        with self.assertRaises(TypeError):
            Any[int]
    def test_cannot_subclass(self):
        with self.assertRaises(TypeError):
            class A(Any):
                pass
        with self.assertRaises(TypeError):
            class A(type(Any)):
                pass
    def test_cannot_instantiate(self):
        with self.assertRaises(TypeError):
            Any()
        with self.assertRaises(TypeError):
            type(Any)()
    def test_any_works_with_alias(self):
        typing.Match[Any]
        typing.Pattern[Any]
        typing.IO[Any]
class NoReturnTests(BaseTestCase):
    def test_noreturn_instance_type_error(self):
        with self.assertRaises(TypeError):
            isinstance(42, NoReturn)
    def test_noreturn_subclass_type_error(self):
        with self.assertRaises(TypeError):
            issubclass(Employee, NoReturn)
        with self.assertRaises(TypeError):
            issubclass(NoReturn, Employee)
    def test_repr(self):
        self.assertEqual(repr(NoReturn), 'typing.NoReturn')
    def test_not_generic(self):
        with self.assertRaises(TypeError):
            NoReturn[int]
    def test_cannot_subclass(self):
        with self.assertRaises(TypeError):
            class A(NoReturn):
                pass
        with self.assertRaises(TypeError):
            class A(type(NoReturn)):
                pass
    def test_cannot_instantiate(self):
        with self.assertRaises(TypeError):
            NoReturn()
        with self.assertRaises(TypeError):
            type(NoReturn)()
class TypeVarTests(BaseTestCase):
    def test_basic_plain(self):
        T = TypeVar('T')
        self.assertEqual(T, T)
        self.assertIsInstance(T, TypeVar)
    def test_typevar_instance_type_error(self):
        T = TypeVar('T')
        with self.assertRaises(TypeError):
            isinstance(42, T)
    def test_typevar_subclass_type_error(self):
        T = TypeVar('T')
        with self.assertRaises(TypeError):
            issubclass(int, T)
        with self.assertRaises(TypeError):
            issubclass(T, int)
    def test_constrained_error(self):
        with self.assertRaises(TypeError):
            X = TypeVar('X', int)
            X
    def test_union_unique(self):
        X = TypeVar('X')
        Y = TypeVar('Y')
        self.assertNotEqual(X, Y)
        self.assertEqual(Union[X], X)
        self.assertNotEqual(Union[X], Union[X, Y])
        self.assertEqual(Union[X, X], X)
        self.assertNotEqual(Union[X, int], Union[X])
        self.assertNotEqual(Union[X, int], Union[int])
        self.assertEqual(Union[X, int].__args__, (X, int))
        self.assertEqual(Union[X, int].__parameters__, (X,))
        self.assertIs(Union[X, int].__origin__, Union)
    def test_or(self):
        X = TypeVar('X')
        self.assertEqual((X | "x"), Union[X, "x"])
        self.assertEqual(("x" | X), Union["x", X])
        self.assertEqual(get_args((X | "x")), (X, ForwardRef("x")))
        self.assertEqual(get_args(("x" | X)), (ForwardRef("x"), X))
    def test_union_constrained(self):
        A = TypeVar('A', str, bytes)
        self.assertNotEqual(Union[A, str], Union[A])
    def test_repr(self):
        self.assertEqual(repr(T), '~T')
        self.assertEqual(repr(KT), '~KT')
        self.assertEqual(repr(VT), '~VT')
        self.assertEqual(repr(AnyStr), '~AnyStr')
        T_co = TypeVar('T_co', covariant=True)
        self.assertEqual(repr(T_co), '+T_co')
        T_contra = TypeVar('T_contra', contravariant=True)
        self.assertEqual(repr(T_contra), '-T_contra')
    def test_no_redefinition(self):
        self.assertNotEqual(TypeVar('T'), TypeVar('T'))
        self.assertNotEqual(TypeVar('T', int, str), TypeVar('T', int, str))
    def test_cannot_subclass_vars(self):
        with self.assertRaises(TypeError):
            class V(TypeVar('T')):
                pass
    def test_cannot_subclass_var_itself(self):
        with self.assertRaises(TypeError):
            class V(TypeVar):
                pass
    def test_cannot_instantiate_vars(self):
        with self.assertRaises(TypeError):
            TypeVar('A')()
    def test_bound_errors(self):
        with self.assertRaises(TypeError):
            TypeVar('X', bound=42)
        with self.assertRaises(TypeError):
            TypeVar('X', str, float, bound=Employee)
    def test_missing__name__(self):
        code = "import typing\n""T = typing.TypeVar('T')\n"
        exec(code, {})
    def test_no_bivariant(self):
        with self.assertRaises(ValueError):
            TypeVar('T', covariant=True, contravariant=True)
class UnionTests(BaseTestCase):
    def test_basics(self):
        u = Union[int, float]
        self.assertNotEqual(u, Union)
    def test_subclass_error(self):
        with self.assertRaises(TypeError):
            issubclass(int, Union)
        with self.assertRaises(TypeError):
            issubclass(Union, int)
        with self.assertRaises(TypeError):
            issubclass(Union[int, str], int)
    def test_union_any(self):
        u = Union[Any]
        self.assertEqual(u, Any)
        u1 = Union[int, Any]
        u2 = Union[Any, int]
        u3 = Union[Any, object]
        self.assertEqual(u1, u2)
        self.assertNotEqual(u1, Any)
        self.assertNotEqual(u2, Any)
        self.assertNotEqual(u3, Any)
    def test_union_object(self):
        u = Union[object]
        self.assertEqual(u, object)
        u1 = Union[int, object]
        u2 = Union[object, int]
        self.assertEqual(u1, u2)
        self.assertNotEqual(u1, object)
        self.assertNotEqual(u2, object)
    def test_unordered(self):
        u1 = Union[int, float]
        u2 = Union[float, int]
        self.assertEqual(u1, u2)
    def test_single_class_disappears(self):
        t = Union[Employee]
        self.assertIs(t, Employee)
    def test_base_class_kept(self):
        u = Union[Employee, Manager]
        self.assertNotEqual(u, Employee)
        self.assertIn(Employee, u.__args__)
        self.assertIn(Manager, u.__args__)
    def test_union_union(self):
        u = Union[int, float]
        v = Union[u, Employee]
        self.assertEqual(v, Union[int, float, Employee])
    def test_repr(self):
        self.assertEqual(repr(Union), 'typing.Union')
        u = Union[Employee, int]
        self.assertEqual(repr(u), ('typing.Union[%s.Employee, int]' % __name__))
        u = Union[int, Employee]
        self.assertEqual(repr(u), ('typing.Union[int, %s.Employee]' % __name__))
        T = TypeVar('T')
        u = Union[T, int][int]
        self.assertEqual(repr(u), repr(int))
        u = Union[List[int], int]
        self.assertEqual(repr(u), 'typing.Union[typing.List[int], int]')
        u = Union[list[int], dict[str, float]]
        self.assertEqual(repr(u), 'typing.Union[list[int], dict[str, float]]')
        u = Union[(int | float)]
        self.assertEqual(repr(u), 'typing.Union[int, float]')
    def test_cannot_subclass(self):
        with self.assertRaises(TypeError):
            class C(Union):
                pass
        with self.assertRaises(TypeError):
            class C(type(Union)):
                pass
        with self.assertRaises(TypeError):
            class C(Union[int, str]):
                pass
    def test_cannot_instantiate(self):
        with self.assertRaises(TypeError):
            Union()
        with self.assertRaises(TypeError):
            type(Union)()
        u = Union[int, float]
        with self.assertRaises(TypeError):
            u()
        with self.assertRaises(TypeError):
            type(u)()
    def test_union_generalization(self):
        self.assertFalse((Union[str, typing.Iterable[int]] == str ))
        self.assertFalse((Union[str, typing.Iterable[int]] == typing.Iterable[int] ))
        self.assertIn(str, Union[str, typing.Iterable[int]].__args__)
        self.assertIn(typing.Iterable[int], Union[str, typing.Iterable[int]].__args__)
    def test_union_compare_other(self):
        self.assertNotEqual(Union, object)
        self.assertNotEqual(Union, Any)
        self.assertNotEqual(ClassVar, Union)
        self.assertNotEqual(Optional, Union)
        self.assertNotEqual([None,], Optional)
        self.assertNotEqual(Optional, typing.Mapping)
        self.assertNotEqual(Optional[typing.MutableMapping], Union)
    def test_optional(self):
        o = Optional[int]
        u = Union[int, None]
        self.assertEqual(o, u)
    def test_empty(self):
        with self.assertRaises(TypeError):
            Union[()]
    def test_no_eval_union(self):
        u = Union[int, str]
        def f(x):
            
        self.assertIs(get_type_hints(f)['x'], u)
    def test_function_repr_union(self):
        def fun():
            
        self.assertEqual(repr(Union[fun, int]), 'typing.Union[fun, int]')
    def test_union_str_pattern(self):
        A = Union[str, Pattern]
        A
    def test_etree(self):
        from xml.etree.ElementTree import Element as Element
        Union[Element, str]
        def Elem(*args):
            return Element(*args)
        Union[Elem, str]
class TupleTests(BaseTestCase):
    def test_basics(self):
        with self.assertRaises(TypeError):
            issubclass(Tuple, Tuple[int, str])
        with self.assertRaises(TypeError):
            issubclass(tuple, Tuple[int, str])
        class TP(tuple):
            
        self.assertIsSubclass(tuple, Tuple)
        self.assertIsSubclass(TP, Tuple)
    def test_equality(self):
        self.assertEqual(Tuple[int], Tuple[int])
        self.assertEqual(Tuple[int, ], Tuple[int, ])
        self.assertNotEqual(Tuple[int], Tuple[int, int])
        self.assertNotEqual(Tuple[int], Tuple[int, ])
    def test_tuple_subclass(self):
        class MyTuple(tuple):
            pass
        self.assertIsSubclass(MyTuple, Tuple)
    def test_tuple_instance_type_error(self):
        with self.assertRaises(TypeError):
            isinstance((0, 0), Tuple[int, int])
        self.assertIsInstance((0, 0), Tuple)
    def test_repr(self):
        self.assertEqual(repr(Tuple), 'typing.Tuple')
        self.assertEqual(repr(Tuple[()]), 'typing.Tuple[()]')
        self.assertEqual(repr(Tuple[int, float]), 'typing.Tuple[int, float]')
        self.assertEqual(repr(Tuple[int, ]), 'typing.Tuple[int, ...]')
        self.assertEqual(repr(Tuple[list[int]]), 'typing.Tuple[list[int]]')
    def test_errors(self):
        with self.assertRaises(TypeError):
            issubclass(42, Tuple)
        with self.assertRaises(TypeError):
            issubclass(42, Tuple[int])
class BaseCallableTests():
    def test_self_subclass(self):
        Callable = self.Callable
        with self.assertRaises(TypeError):
            issubclass(types.FunctionType, Callable[[int,], int])
        self.assertIsSubclass(types.FunctionType, Callable)
    def test_eq_hash(self):
        Callable = self.Callable
        C = Callable[[int,], int]
        self.assertEqual(C, Callable[[int,], int])
        self.assertEqual(len({C, Callable[[int,], int]}), 1)
        self.assertNotEqual(C, Callable[[int,], str])
        self.assertNotEqual(C, Callable[[str,], int])
        self.assertNotEqual(C, Callable[[int, int], int])
        self.assertNotEqual(C, Callable[[], int])
        self.assertNotEqual(C, Callable[, int])
        self.assertNotEqual(C, Callable)
    def test_cannot_instantiate(self):
        Callable = self.Callable
        with self.assertRaises(TypeError):
            Callable()
        with self.assertRaises(TypeError):
            type(Callable)()
        c = Callable[[int,], str]
        with self.assertRaises(TypeError):
            c()
        with self.assertRaises(TypeError):
            type(c)()
    def test_callable_wrong_forms(self):
        Callable = self.Callable
        with self.assertRaises(TypeError):
            Callable[int]
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
        with self.assertRaises(TypeError):
            self.assertIsInstance(f, Callable[[], None])
        with self.assertRaises(TypeError):
            self.assertIsInstance(f, Callable[[], Any])
        with self.assertRaises(TypeError):
            self.assertNotIsInstance(None, Callable[[], None])
        with self.assertRaises(TypeError):
            self.assertNotIsInstance(None, Callable[[], Any])
    def test_repr(self):
        pass
    def test_callable_with_ellipsis(self):
        Callable = self.Callable
        def foo(a):
            pass
        self.assertEqual(get_type_hints(foo, globals(), locals()), {'a' : Callable[, T]})
    def test_ellipsis_in_generic(self):
        Callable = self.Callable
        typing.List[Callable[, str]]
    def test_basic(self):
        Callable = self.Callable
        alias = Callable[[int, str], float]
        if ((Callable is collections.abc.Callable )):
            self.assertIsInstance(alias, types.GenericAlias)
        else:
            pass
        self.assertIs(alias.__origin__, collections.abc.Callable)
        self.assertEqual(alias.__args__, (int, str, float))
        self.assertEqual(alias.__parameters__, ())
    def test_weakref(self):
        Callable = self.Callable
        alias = Callable[[int, str], float]
        self.assertEqual(weakref.ref(alias)(), alias)
    def test_pickle(self):
        Callable = self.Callable
        alias = Callable[[int, str], float]
        for proto in range((pickle.HIGHEST_PROTOCOL + 1)):
            s = pickle.dumps(alias, proto)
            loaded = pickle.loads(s)
            self.assertEqual(alias.__origin__, loaded.__origin__)
            self.assertEqual(alias.__args__, loaded.__args__)
            self.assertEqual(alias.__parameters__, loaded.__parameters__)
        else:
            pass
    def test_var_substitution(self):
        pass
    def test_type_erasure(self):
        Callable = self.Callable
        class C1(Callable):
            def __call__(self):
                return None
        a = C1[[int,], T]
        self.assertIs(a().__class__, C1)
        self.assertEqual(a().__orig_class__, C1[[int,], T])
    def test_paramspec(self):
        Callable = self.Callable
        P = ParamSpec('P')
        P2 = ParamSpec('P2')
        C1 = Callable[P, T]
        self.assertEqual(C1[[int,], str], Callable[[int,], str])
        self.assertEqual(C1[[int, str], str], Callable[[int, str], str])
        self.assertEqual(C1[[], str], Callable[[], str])
        self.assertEqual(C1[, str], Callable[, str])
        self.assertEqual(C1[P2, str], Callable[P2, str])
        self.assertEqual(C1[Concatenate[int, P2], str], Callable[Concatenate[int, P2], str])
        with self.assertRaises(TypeError):
            C1[int, str]
        C2 = Callable[P, int]
        self.assertEqual(C2[[int,]], Callable[[int,], int])
        self.assertEqual(C2[[int, str]], Callable[[int, str], int])
        self.assertEqual(C2[[]], Callable[[], int])
        self.assertEqual(C2[], Callable[, int])
        self.assertEqual(C2[P2], Callable[P2, int])
        self.assertEqual(C2[Concatenate[int, P2]], Callable[Concatenate[int, P2], int])
        self.assertEqual(C2[int], Callable[[int,], int])
        self.assertEqual(C2[int, str], Callable[[int, str], int])
    def test_concatenate(self):
        Callable = self.Callable
        P = ParamSpec('P')
        C1 = Callable[typing.Concatenate[int, P], int]
    def test_errors(self):
        Callable = self.Callable
        alias = Callable[[int, str], float]
        with self.assertRaisesRegex(TypeError, "is not a generic class"):
            alias[int]
        P = ParamSpec('P')
        C1 = Callable[P, T]
        with self.assertRaisesRegex(TypeError, "many arguments for"):
            C1[int, str, str]
        with self.assertRaisesRegex(TypeError, "few arguments for"):
            C1[int]
class TypingCallableTests(BaseCallableTests, BaseTestCase):
    Callable = typing.Callable
    def test_consistency(self):
        c1 = typing.Callable[[int, str], dict]
        c2 = collections.abc.Callable[[int, str], dict]
        self.assertEqual(c1.__args__, c2.__args__)
        self.assertEqual(hash(c1.__args__), hash(c2.__args__))
class CollectionsCallableTests(BaseCallableTests, BaseTestCase):
    Callable = collections.abc.Callable
class LiteralTests(BaseTestCase):
    def test_basics(self):
        Literal[1]
        Literal[1, 2, 3]
        Literal["x", "y", "z"]
        Literal[None]
        Literal[True]
        Literal[1, "2", False]
        Literal[Literal[1, 2], Literal[4, 5]]
    def test_illegal_parameters_do_not_raise_runtime_errors(self):
        Literal[int]
        Literal[{"foo" : 3, "bar" : 4}]
        Literal[T]
    def test_literals_inside_other_types(self):
        List[Literal[1, 2, 3]]
        List[Literal[("foo", "bar", "baz")]]
    def test_repr(self):
        self.assertEqual(repr(Literal[1]), "typing.Literal[1]")
        self.assertEqual(repr(Literal[1, True, "foo"]), "typing.Literal[1, True, 'foo']")
        self.assertEqual(repr(Literal[int]), "typing.Literal[int]")
        self.assertEqual(repr(Literal), "typing.Literal")
        self.assertEqual(repr(Literal[None]), "typing.Literal[None]")
        self.assertEqual(repr(Literal[1, 2, 3, 3]), "typing.Literal[1, 2, 3]")
    def test_cannot_init(self):
        with self.assertRaises(TypeError):
            Literal()
        with self.assertRaises(TypeError):
            Literal[1]()
        with self.assertRaises(TypeError):
            type(Literal)()
        with self.assertRaises(TypeError):
            type(Literal[1])()
    def test_no_isinstance_or_issubclass(self):
        with self.assertRaises(TypeError):
            isinstance(1, Literal[1])
        with self.assertRaises(TypeError):
            isinstance(int, Literal[1])
        with self.assertRaises(TypeError):
            issubclass(1, Literal[1])
        with self.assertRaises(TypeError):
            issubclass(int, Literal[1])
    def test_no_subclassing(self):
        with self.assertRaises(TypeError):
            class Foo(Literal[1]):
                pass
        with self.assertRaises(TypeError):
            class Bar(Literal):
                pass
    def test_no_multiple_subscripts(self):
        with self.assertRaises(TypeError):
            Literal[1][1]
    def test_equal(self):
        self.assertNotEqual(Literal[0], Literal[False])
        self.assertNotEqual(Literal[True], Literal[1])
        self.assertNotEqual(Literal[1], Literal[2])
        self.assertNotEqual(Literal[1, True], Literal[1])
        self.assertNotEqual(Literal[1, True], Literal[1, 1])
        self.assertNotEqual(Literal[1, 2], Literal[True, 2])
        self.assertEqual(Literal[1], Literal[1])
        self.assertEqual(Literal[1, 2], Literal[2, 1])
        self.assertEqual(Literal[1, 2, 3], Literal[1, 2, 3, 3])
    def test_hash(self):
        self.assertEqual(hash(Literal[1]), hash(Literal[1]))
        self.assertEqual(hash(Literal[1, 2]), hash(Literal[2, 1]))
        self.assertEqual(hash(Literal[1, 2, 3]), hash(Literal[1, 2, 3, 3]))
    def test_args(self):
        self.assertEqual(Literal[1, 2, 3].__args__, (1, 2, 3))
        self.assertEqual(Literal[1, 2, 3, 3].__args__, (1, 2, 3))
        self.assertEqual(Literal[1, Literal[2], Literal[3, 4]].__args__, (1, 2, 3, 4))
        self.assertEqual(Literal[[], []].__args__, ([], []))
    def test_flatten(self):
        l1 = Literal[Literal[1], Literal[2], Literal[3]]
        l2 = Literal[Literal[1, 2], 3]
        l3 = Literal[Literal[1, 2, 3]]
        for l in (l1, l2, l3):
            self.assertEqual(l, Literal[1, 2, 3])
            self.assertEqual(l.__args__, (1, 2, 3))
        else:
            pass
XK = TypeVar('XK', str, bytes)
XV = TypeVar('XV')