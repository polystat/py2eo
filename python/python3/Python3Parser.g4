/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : python3-parser; an ANTLR4 grammar for Python 3
 *                https://github.com/bkiers/python3-parser
 * Developed by : Bart Kiers, bart@big-o.nl
 */
parser grammar Python3Parser;

options {
    tokenVocab=Python3Lexer;
}

// All comments that start with "///" are copy-pasted from
// The Python Language Reference

single_input: NEWLINE | simple_stmt | compound_stmt NEWLINE;
file_input: (NEWLINE | stmt)* EOF;
eval_input: testlist NEWLINE* EOF;

decorator: '@' dotted_name ( '(' (arglist)? ')' )? NEWLINE;
decorators: decorator+;
decorated: decorators (classdef | funcdef | async_funcdef);

async_funcdef: ASYNC funcdef;
funcdef: 'def' NAME parameters ('->' test)? ':' suite;

parameters: '(' (typedargslist)? ')';
typedargslist: (l+=tfparg (',' l+=tfparg)* (',' (typedargslist_nopos | tfpdict)?)?
  | typedargslist_nopos
  | tfpdict);
typedargslist_nopos: tfptuple (',' l+=tfparg)* (',' (tfpdict)?)?;
tfparg: tfpdef ('=' test)?;
tfptuple: '*' (tfpdef)?;
tfpdict: '**' tfpdef (',')?;
tfpdef: NAME (':' test)?;

varargslist: (l+=vfpdef ('=' test)? (',' l+=vfpdef ('=' test)?)* (',' (
        '*' (vfpdef)? (',' vfpdef ('=' test)?)* (',' ('**' vfpdef (',')?)?)?
      | '**' vfpdef (',')?)?)?
  | '*' (vfpdef)? (',' vfpdef ('=' test)?)* (',' ('**' vfpdef (',')?)?)?
  | '**' vfpdef (',')?
);
vfpdef: NAME;

stmt:
    simple_stmt     # StmtSimple
    | compound_stmt # StmtCompound
    ;
simple_stmt: l+=small_stmt (';' l+=small_stmt)* (';')? NEWLINE;
small_stmt: expr_stmt # SmallExpr
    | del_stmt        # SmallDel
    | pass_stmt       # SmallPass
    | flow_stmt       # SmallFlow
    | import_stmt     # SmallImport
    | global_stmt     # SmallGlobal
    | nonlocal_stmt   # SmallNonLocal
    | assert_stmt     # SmallAssert
    ;
expr_stmt: testlist_star_expr expr_stmt_right;
expr_stmt_right : annassign  # AnnAssign
    | augassign (yield_expr|testlist) # AugAssign
    | ('=' l+=rhsassign)* # JustAssign
    ;
rhsassign :
    yield_expr              # RhsYield
    |testlist_star_expr     # RhsTestlist
    ;
annassign: ':' test ('=' test)?;
test_star_expr :
    test # TestNotStar
    | star_expr # StarNotTest
    ;
testlist_star_expr: (l+=test_star_expr) (',' (l+=test_star_expr))* (',')?;
augassign: ('+=' | '-=' | '*=' | '@=' | '/=' | '%=' | '&=' | '|=' | '^=' |
            '<<=' | '>>=' | '**=' | '//=');
// For normal and annotated assignments, additional restrictions enforced by the interpreter
del_stmt: 'del' exprlist;
pass_stmt: 'pass';
flow_stmt:
    break_stmt      # FlowBreak
    | continue_stmt # FlowContinue
    | return_stmt   # FlowReturn
    | raise_stmt    # FlowRaise
    | yield_stmt    # FlowYield
    ;
break_stmt: 'break';
continue_stmt: 'continue';
return_stmt: 'return' (testlist)?;
yield_stmt: yield_expr;
raise_stmt: 'raise' (test ('from' test)?)?;

import_stmt:
      import_name # ImportName
    | import_from # ImportFrom
    ;
import_name: 'import' dotted_as_names;
// note below: the ('.' | '...') is necessary because '...' is tokenized as ELLIPSIS
import_from: ('from' (('.' | '...')* dotted_name | ('.' | '...')+)
              'import' ('*' | '(' import_as_names ')' | import_as_names));
import_as_name: what=NAME ('as' aswhat=NAME)?;
dotted_as_name: dotted_name ('as' NAME)?;
import_as_names: l+=import_as_name (',' l+=import_as_name)* (',')?;
dotted_as_names: l+=dotted_as_name (',' l+=dotted_as_name)*;
dotted_name: l+=NAME ('.' l+=NAME)*;

global_stmt: 'global' l+=NAME (',' l+=NAME)*;
nonlocal_stmt: 'nonlocal' l+=NAME (',' l+=NAME)*;
assert_stmt: 'assert' l+=test (',' l+=test)?;

compound_stmt:
    if_stmt     # CompIf
    | while_stmt # CompWhile
    | for_stmt  # CompFor
    | try_stmt  # CompTry
    | with_stmt # CompWith
    | funcdef   # CompFuncDef
    | classdef  # CompClassDef
    | decorated # CompDecorated
    | async_stmt # CompAsync
    ;
async_stmt: ASYNC (funcdef | with_stmt | for_stmt);
if_stmt: 'if' conds+=test ':' bodies+=suite ('elif' conds+=test ':' bodies+=suite)* ('else' ':' eelse=suite)?;
while_stmt: 'while' cond=test ':' body=suite ('else' ':' eelse=suite)?;
for_stmt: 'for' exprlist 'in' testlist ':' body=suite ('else' ':' eelse=suite)?;

try_stmt: ('try' ':' trySuite=suite
           ((except_clause ':' exceptSuites+=suite)+
            ('else' ':' elseSuite=suite)?
            ('finally' ':' finallySuite+=suite)? |
           'finally' ':' finallySuite+=suite));
with_stmt: 'with' l+=with_item (',' l+=with_item)*  ':' suite;
with_item: test ('as' expr)?;
// NB compile.c makes sure that the default except clause is last
except_clause: 'except' (test ('as' NAME)?)?;

suite:
    simple_stmt # SuiteSimpleStmt
    | NEWLINE INDENT (l+=stmt)+ DEDENT  # SuiteBlockStmts
    ;

test:
    or_test ('if' or_test 'else' test)? # TestOrTest
    | lambdef  # TestLambdef
    ;
test_nocond: or_test | lambdef_nocond;
lambdef: 'lambda' (varargslist)? ':' test;
lambdef_nocond: 'lambda' (varargslist)? ':' test_nocond;

or_test: args+=and_test (ops+='or' args+=and_test)*;
and_test: args+=not_test (ops+='and' args+=not_test)*;
not_test:
    'not' not_test # NotNot
    | comparison #NotComparison
    ;
comparison: args+=expr (ops+=comp_op args+=expr)*;
// <> isn't actually a valid comparison operator in Python. It's here for the
// sake of a __future__ import described in PEP 401 (which really works :-)
comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not';
star_expr: '*' expr;
expr: args+=xor_expr (ops+='|' args+=xor_expr)*;
xor_expr: args+=and_expr (ops+='^' args+=and_expr)*;
and_expr: args+=shift_expr (ops+='&' args+=shift_expr)*;
shift_expr: args+=arith_expr (ops+=('<<'|'>>') args+=arith_expr)*;
arith_expr: args+=term (ops+=('+'|'-') args+=term)*;
term: args+=factor (ops+=('*'|'@'|'/'|'%'|'//') args+=factor)*;
factor:
    op=('+'|'-'|'~') factor # Unary
    | power # FactorPower ;
power: atom_expr ('**' factor)?;
atom_expr: (AWAIT)? atom trail=trailer*;
atom: ('(' (yield_expr|testlist_comp)? ')' |
       '[' (testlist_comp)? ']' |
       '{' (dictorsetmaker)? '}' |
       NAME | NUMBER | STRING+ | '...' | 'None' | 'True' | 'False');
testlist_comp: (test_star_expr) ( comp_for | (',' (test_star_expr))* (',')? );

trailer:
    '(' (arglist)? ')'   # TrailerCall
    | '[' subscriptlist ']'  # TrailerSub
    | '.' NAME              # TrailerField
    ;

subscriptlist: l+=subscript_ (',' l+=subscript_)* (',')?;
subscript_:
    test # SubIndex
    | (test)? ':' (test)? (':' test)? # SubSlice
    ;
expr_star_expr : expr | star_expr;
exprlist: l+=expr_star_expr (',' l+=expr_star_expr)* (',')?;
testlist: l+=test (',' l+=test)* (',')?;

dict_elt_double_star: (test ':' test | '**' expr);
dictorsetmaker: (dict_elt_double_star (comp_for | (',' dict_elt_double_star)* (',')?)) | testlist_comp ;

classdef: 'class' NAME ('(' (arglist)? ')')? ':' suite;

arglist: l+=argument (',' l+=argument)*  (',')?;

// The reason that keywords are test nodes instead of NAME is that using NAME
// results in an ambiguity. ast.c makes sure it's a NAME.
// "test '=' test" is really "keyword '=' test", but we have no such token.
// These need to be in a single rule to avoid grammar that is ambiguous
// to our LL(1) parser. Even though 'test' includes '*expr' in star_expr,
// we explicitly match '*' here, too, to give it proper precedence.
// Illegal combinations and orderings are blocked in ast.c:
// multiple (test comp_for) arguments are blocked; keyword unpackings
// that precede iterable unpackings are blocked; etc.
argument: ( test (comp_for)? |
            test '=' test |
            '**' test |
            '*' test );

comp_iter: comp_for | comp_if;
comp_for: (ASYNC)? 'for' exprlist 'in' or_test (comp_iter)?;
comp_if: 'if' test_nocond (comp_iter)?;

// not used in grammar, but may appear in "node" passed from Parser to Compiler
encoding_decl: NAME;

yield_expr: 'yield' (yield_arg)?;
yield_arg: 'from' test | testlist;
