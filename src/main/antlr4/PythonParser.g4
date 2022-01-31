/*
Python grammar
The MIT License (MIT)
Copyright (c) 2021 Robert Einhorn

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

 /*
 * Project      : an ANTLR4 parser grammar by the official PEG grammar
 *
 * Developed by : Robert Einhorn, robert.einhorn.hu@gmail.com
 */

parser grammar PythonParser; // Python 3.10.2    https://docs.python.org/3.10/reference/grammar.html

@header {
  package org.polystat.py2eo;
}

options { tokenVocab=PythonLexer; superClass=PythonParserBase; }
// ANTLR4 grammar for Python


file: statements? EOF;
interactive: statement_newline;
eval: expressions NEWLINE* EOF;
func_type: '(' type_expressions? ')' '->' expression NEWLINE* EOF;
fstring: star_expressions;

// type_expressions allow */** but ignore them
type_expressions
    : expression (',' expression)* ',' '*' expression ',' '**' expression
    | expression (',' expression)* ',' '*' expression
    | expression (',' expression)* ',' '**' expression
    | '*' expression ',' '**' expression
    | '*' expression
    | '**' expression
    | expression (',' expression)*;

statements: statement+;
statement: compound_stmt  | simple_stmts;
statement_newline
    : compound_stmt NEWLINE
    | simple_stmts
    | NEWLINE
    | EOF;
simple_stmts
    : simple_stmt {is_notNextToken(';')}? NEWLINE  // Not needed, there for speedup
    | simple_stmt (';' simple_stmt)* ';'? NEWLINE;
// NOTE: assignment MUST precede expression, else parsing a simple assignment
// will throw a SyntaxError.
simple_stmt
    : assignment
    | star_expressions
    | return_stmt
    | import_stmt
    | raise_stmt
    | 'pass'
    | del_stmt
    | yield_stmt
    | assert_stmt
    | 'break'
    | 'continue'
    | global_stmt
    | nonlocal_stmt;
compound_stmt
    : function_def
    | if_stmt
    | class_def
    | with_stmt
    | for_stmt
    | try_stmt
    | while_stmt
    | match_stmt;

// NOTE: annotated_rhs may start with 'yield'; yield_expr must start with 'yield'
assignment
    : NAME ':' expression ('=' annotated_rhs )?
    | ('(' single_target ')'
         | single_subscript_attribute_target) ':' expression ('=' annotated_rhs )?
    | (star_targets '=' )+ (yield_expr | star_expressions) {is_notNextToken('=')}? TYPE_COMMENT?
    | single_target augassign (yield_expr | star_expressions);

augassign
    : '+='
    | '-='
    | '*='
    | '@='
    | '/='
    | '%='
    | '&='
    | '|='
    | '^='
    | '<<='
    | '>>='
    | '**='
    | '//=';

global_stmt: 'global' NAME (',' NAME)*;
nonlocal_stmt: 'nonlocal' NAME (',' NAME)*;

yield_stmt: yield_expr;

assert_stmt: 'assert' expression (',' expression )?;

del_stmt
    : 'del' del_targets {isNextToken(SEMI, NEWLINE)}?;

import_stmt: import_name | import_from;
import_name: 'import' dotted_as_names;
// note below: the ('.' | '...') is necessary because '...' is tokenized as ELLIPSIS
import_from
    : 'from' ('.' | '...')* dotted_name 'import' import_from_targets
    | 'from' ('.' | '...')+ 'import' import_from_targets;
import_from_targets
    : '(' import_from_as_names ','? ')'
    | import_from_as_names {is_notNextToken(',')}?
    | '*';
import_from_as_names
    : import_from_as_name (',' import_from_as_name)*;
import_from_as_name
    : NAME ('as' NAME )?;
dotted_as_names
    : dotted_as_name (',' dotted_as_name)*;
dotted_as_name
    : dotted_name ('as' NAME )?;
dotted_name
    : dotted_name '.' NAME
    | NAME;

if_stmt
    : 'if' named_expression ':' block elif_stmt
    | 'if' named_expression ':' block else_block?;
elif_stmt
    : 'elif' named_expression ':' block elif_stmt
    | 'elif' named_expression ':' block else_block?;
else_block
    : 'else' ':' block;

while_stmt
    : 'while' named_expression ':' block else_block?;

for_stmt
    : 'for' star_targets 'in' star_expressions ':' TYPE_COMMENT? block else_block?
    | ASYNC 'for' star_targets 'in' star_expressions ':' TYPE_COMMENT? block else_block?;

with_stmt
    : 'with' '(' with_item (',' with_item)* ','? ')' ':' block
    | 'with' with_item (',' with_item)* ':' TYPE_COMMENT? block
    | ASYNC 'with' '(' with_item (',' with_item)* ','? ')' ':' block
    | ASYNC 'with' with_item (',' with_item)* ':' TYPE_COMMENT? block;

with_item
    : expression 'as' star_target {isNextToken(',', ')', ':')}?
    | expression;

try_stmt
    : 'try' ':' block finally_block
    | 'try' ':' block except_block+ else_block? finally_block?;
except_block
    : 'except' expression ('as' NAME )? ':' block
    | 'except' ':' block;
finally_block
    : 'finally' ':' block;

match_stmt
    : match_skw subject_expr ':' NEWLINE INDENT case_block+ DEDENT;
subject_expr
    : star_named_expression ',' star_named_expressions?
    | named_expression;
case_block
    : case_skw patterns guard? ':' block;
guard: 'if' named_expression;

patterns
    : open_sequence_pattern
    | pattern;
pattern
    : as_pattern
    | or_pattern;
as_pattern
    : or_pattern 'as' pattern_capture_target;
or_pattern
    : closed_pattern ('|' closed_pattern)*;
closed_pattern
    : literal_pattern
    | capture_pattern
    | wildcard_pattern
    | value_pattern
    | group_pattern
    | sequence_pattern
    | mapping_pattern
    | class_pattern;

// Literal patterns are used for equality and identity constraints
literal_pattern
    : signed_number {is_notNextToken('+', '-')}?
    | complex_number
    | strings
    | 'None'
    | 'True'
    | 'False';

// Literal expressions are used to restrict permitted mapping pattern keys
literal_expr
    : signed_number {is_notNextToken('+', '-')}?
    | complex_number
    | strings
    | 'None'
    | 'True'
    | 'False';

complex_number
    : signed_real_number '+' imaginary_number
    | signed_real_number '-' imaginary_number;

signed_number
    : NUMBER
    | '-' NUMBER;

signed_real_number
    : real_number
    | '-' real_number;

real_number
    : NUMBER;

imaginary_number
    : NUMBER;

capture_pattern
    : pattern_capture_target;

pattern_capture_target
    : name_except_underscore NAME {is_notNextToken('.', '(', '=')}?;

wildcard_pattern
    : underscore_skw;

value_pattern
    : attr {is_notNextToken('.', '(', '=')}?;
attr
    : name_or_attr '.' NAME;
name_or_attr
    : name_or_attr '.' NAME
    | NAME;

group_pattern
    : '(' pattern ')';

sequence_pattern
    : '[' maybe_sequence_pattern? ']'
    | '(' open_sequence_pattern? ')';
open_sequence_pattern
    : maybe_star_pattern ',' maybe_sequence_pattern?;
maybe_sequence_pattern
    : maybe_star_pattern (',' maybe_star_pattern)* ','?;
maybe_star_pattern
    : star_pattern
    | pattern;
star_pattern
    : '*' pattern_capture_target
    | '*' wildcard_pattern;

mapping_pattern
    : '{' '}'
    | '{' double_star_pattern ','? '}'
    | '{' items_pattern ',' double_star_pattern ','? '}'
    | '{' items_pattern ','? '}';
items_pattern
    : key_value_pattern (',' key_value_pattern)*;
key_value_pattern
    : (literal_expr | attr) ':' pattern;
double_star_pattern
    : '**' pattern_capture_target;

class_pattern
    : name_or_attr '(' ')'
    | name_or_attr '(' positional_patterns ','? ')'
    | name_or_attr '(' keyword_patterns ','? ')'
    | name_or_attr '(' positional_patterns ',' keyword_patterns ','? ')';
positional_patterns
    : pattern (',' pattern)*;
keyword_patterns
    : keyword_pattern (',' keyword_pattern)*;
keyword_pattern
    : NAME '=' pattern;

return_stmt
    : 'return' star_expressions?;

raise_stmt
    : 'raise' expression ('from' expression )?
    | 'raise';

function_def
    : decorators function_def_raw
    | function_def_raw;

function_def_raw
    : 'def' NAME '(' params? ')' ('->' expression )? ':' func_type_comment? block
    | ASYNC 'def' NAME '(' params? ')' ('->' expression )? ':' func_type_comment? block;
func_type_comment
    : NEWLINE TYPE_COMMENT {areNextTokens(NEWLINE, INDENT)}?   // Must be followed by indented block
    | TYPE_COMMENT;

params
    : parameters;

parameters
    : slash_no_default param_no_default* param_with_default* star_etc?
    | slash_with_default param_with_default* star_etc?
    | param_no_default+ param_with_default* star_etc?
    | param_with_default+ star_etc?
    | star_etc;

// Some duplication here because we can't write (',' | {isNextToken(')')}?),
// which is because we don't support empty alternatives (yet).
//
slash_no_default
    : param_no_default+ '/' ','
    | param_no_default+ '/' {isNextToken(')')}?;
slash_with_default
    : param_no_default* param_with_default+ '/' ','
    | param_no_default* param_with_default+ '/' {isNextToken(')')}?;

star_etc
    : '*' param_no_default param_maybe_default* kwds?
    | '*' ',' param_maybe_default+ kwds?
    | kwds;

kwds: '**' param_no_default;

// One parameter.  This *includes* a following comma and type comment.
//
// There are three styles:
// - No default_assignment
// - With default_assignment
// - Maybe with default_assignment
//
// There are two alternative forms of each, to deal with type comments:
// - Ends in a comma followed by an optional type comment
// - No comma, optional type comment, must be followed by close paren
// The latter form is for a final parameter without trailing comma.
//
param_no_default
    : param ',' TYPE_COMMENT?
    | param TYPE_COMMENT? {isNextToken(')')}?;
param_with_default
    : param default_assignment ',' TYPE_COMMENT?
    | param default_assignment TYPE_COMMENT? {isNextToken(')')}?;
param_maybe_default
    : param default_assignment? ',' TYPE_COMMENT?
    | param default_assignment? TYPE_COMMENT? {isNextToken(')')}?;
param: NAME annotation?;

annotation: ':' expression;
default_assignment: '=' expression;

decorators: ('@' named_expression NEWLINE )+;

class_def
    : decorators class_def_raw
    | class_def_raw;
class_def_raw
    : 'class' NAME ('(' arguments? ')' )? ':' block;

block
    : NEWLINE INDENT statements DEDENT
    | simple_stmts;

star_expressions
    : l+=star_expression (',' l+=star_expression )+ ','?
    | l+=star_expression ','
    | l+=star_expression;
star_expression
    : '*' bitwise_or
    | expression;

star_named_expressions: l+=star_named_expression (',' l+=star_named_expression)* ','?;
star_named_expression
    : '*' bitwise_or
    | named_expression;


assignment_expression
    : NAME ':=' expression;

named_expression
    : assignment_expression
    | expression {is_notNextToken(":=")}?;

annotated_rhs: yield_expr | star_expressions;

expressions
    : expression (',' expression )+ ','?
    | expression ','
    | expression;
expression
    : disjunction 'if' disjunction 'else' expression
    | disjunction
    | lambdef;

lambdef
    : 'lambda' lambda_params? ':' expression;

lambda_params
    : lambda_parameters;

// lambda_parameters etc. duplicates parameters but without annotations
// or type comments, and if there's no comma after a parameter, we expect
// a colon, not a close parenthesis.  (For more, see parameters above.)
//
lambda_parameters
    : lambda_slash_no_default lambda_param_no_default* lambda_param_with_default* lambda_star_etc?
    | lambda_slash_with_default lambda_param_with_default* lambda_star_etc?
    | lambda_param_no_default+ lambda_param_with_default* lambda_star_etc?
    | lambda_param_with_default+ lambda_star_etc?
    | lambda_star_etc;

lambda_slash_no_default
    : lambda_param_no_default+ '/' ','
    | lambda_param_no_default+ '/' {isNextToken(':')}?;
lambda_slash_with_default
    : lambda_param_no_default* lambda_param_with_default+ '/' ','
    | lambda_param_no_default* lambda_param_with_default+ '/' {isNextToken(':')}?;

lambda_star_etc
    : '*' lambda_param_no_default lambda_param_maybe_default* lambda_kwds?
    | '*' ',' lambda_param_maybe_default+ lambda_kwds?
    | lambda_kwds;

lambda_kwds: '**' lambda_param_no_default;

lambda_param_no_default
    : lambda_param ','
    | lambda_param {isNextToken(':')}?;
lambda_param_with_default
    : lambda_param default_assignment ','
    | lambda_param default_assignment {isNextToken(':')}?;
lambda_param_maybe_default
    : lambda_param default_assignment? ','
    | lambda_param default_assignment? {isNextToken(':')}?;
lambda_param: NAME;

disjunction
    : conjunction ('or' conjunction )+
    | conjunction;
conjunction
    : inversion ('and' inversion )+
    | inversion;
inversion
    : 'not' inversion
    | comparison;
comparison
    : bitwise_or compare_op_bitwise_or_pair+
    | bitwise_or;
compare_op_bitwise_or_pair
    : eq_bitwise_or
    | noteq_bitwise_or
    | lte_bitwise_or
    | lt_bitwise_or
    | gte_bitwise_or
    | gt_bitwise_or
    | notin_bitwise_or
    | in_bitwise_or
    | isnot_bitwise_or
    | is_bitwise_or;
eq_bitwise_or: '==' bitwise_or;
noteq_bitwise_or
    : ('!=' ) bitwise_or;
lte_bitwise_or: '<=' bitwise_or;
lt_bitwise_or: '<' bitwise_or;
gte_bitwise_or: '>=' bitwise_or;
gt_bitwise_or: '>' bitwise_or;
notin_bitwise_or: 'not' 'in' bitwise_or;
in_bitwise_or: 'in' bitwise_or;
isnot_bitwise_or: 'is' 'not' bitwise_or;
is_bitwise_or: 'is' bitwise_or;

bitwise_or
    : bitwise_or '|' bitwise_xor
    | bitwise_xor;
bitwise_xor
    : bitwise_xor '^' bitwise_and
    | bitwise_and;
bitwise_and
    : bitwise_and '&' shift_expr
    | shift_expr;
shift_expr
    : shift_expr '<<' sum
    | shift_expr '>>' sum
    | sum;

sum
    : sum '+' term
    | sum '-' term
    | term;
term
    : term '*' factor
    | term '/' factor
    | term '//' factor
    | term '%' factor
    | term '@' factor
    | factor;
factor
    : '+' factor
    | '-' factor
    | '~' factor
    | power;
power
    : await_primary '**' factor
    | await_primary;
await_primary
    : AWAIT primary
    | primary;
primary
    : primary '.' NAME
    | primary genexp
    | primary '(' arguments? ')'
    | primary '[' slices ']'
    | atom;

slices
    : slice {is_notNextToken(',')}?
    | slice (',' slice)* ','?;
slice
    : (from=expression)? ':' (to=expression)? (':' (by=expression)? )?
    | named_expression;
atom
    : NAME
    | 'True'
    | 'False'
    | 'None'
    | strings
    | NUMBER
    | (tuple | group | genexp)
    | (list | listcomp)
    | (dict | set | dictcomp | setcomp)
    | '...';

strings: STRING+;
list
    : '[' star_named_expressions? ']';
listcomp
    : '[' named_expression for_if_clauses ']';
tuple
    : '(' (star_named_expression ',' star_named_expressions?  )? ')';
group
    : '(' (yield_expr | named_expression) ')';
genexp
    : '(' ( assignment_expression | expression {is_notNextToken(":=")}?) for_if_clauses ')';
set: '{' star_named_expressions '}';
setcomp
    : '{' named_expression for_if_clauses '}';
dict
    : '{' double_starred_kvpairs? '}';

dictcomp
    : '{' kvpair for_if_clauses '}';
double_starred_kvpairs: double_starred_kvpair (',' double_starred_kvpair)* ','?;
double_starred_kvpair
    : '**' bitwise_or
    | kvpair;
kvpair: expression ':' expression;
for_if_clauses
    : for_if_clause+;
for_if_clause
    : ASYNC 'for' star_targets 'in' disjunction ('if' disjunction )*
    | 'for' star_targets 'in' disjunction ('if' disjunction )*;

yield_expr
    : 'yield' 'from' expression
    | 'yield' star_expressions?;

arguments
    : args ','? {isNextToken(')')}?;
args
    : arg (',' arg)* (',' kwargs )?
    | kwargs;

arg: (starred_expression | ( assignment_expression | expression {is_notNextToken(":=")}?) {is_notNextToken('=')}?) ;

kwargs
    : kwarg_or_starred (',' kwarg_or_starred)* ',' kwarg_or_double_starred (',' kwarg_or_double_starred)*
    | kwarg_or_starred (',' kwarg_or_starred)*
    | kwarg_or_double_starred (',' kwarg_or_double_starred)*;
starred_expression
    : '*' expression;
kwarg_or_starred
    : NAME '=' expression
    | starred_expression;
kwarg_or_double_starred
    : NAME '=' expression
    | '**' expression;

// NOTE: star_targets may contain *bitwise_or, targets may not.
star_targets
    : l+=star_target {is_notNextToken(',')}?
    | l+=star_target (',' l+=star_target )* ','?;
star_targets_list_seq: star_target (',' star_target)* ','?;
star_targets_tuple_seq
    : l+=star_target (',' l+=star_target )+ ','?
    | l+=star_target ',';
star_target
    : '*' ({is_notNextToken('*')}? star_target)
    | target_with_star_atom;
target_with_star_atom
    : t_primary '.' NAME {is_notNextToken(t_lookahead)}?
    | t_primary '[' slices ']' {is_notNextToken(t_lookahead)}?
    | star_atom;
star_atom
    : NAME
    | '(' target_with_star_atom ')'
    | '(' star_targets_tuple_seq? ')'
    | '[' star_targets_list_seq? ']';

single_target
    : single_subscript_attribute_target
    | NAME
    | '(' single_target ')';
single_subscript_attribute_target
    : t_primary '.' NAME {is_notNextToken(t_lookahead)}?
    | t_primary '[' slices ']' {is_notNextToken(t_lookahead)}?;

del_targets: del_target (',' del_target)* ','?;
del_target
    : t_primary '.' NAME {is_notNextToken(t_lookahead)}?
    | t_primary '[' slices ']' {is_notNextToken(t_lookahead)}?
    | del_t_atom;
del_t_atom
    : NAME
    | '(' del_target ')'
    | '(' del_targets? ')'
    | '[' del_targets? ']';

t_primary
    : t_primary '.' NAME {isNextToken(t_lookahead)}?
    | t_primary '[' slices ']' {isNextToken(t_lookahead)}?
    | t_primary genexp {isNextToken(t_lookahead)}?
    | t_primary '(' arguments? ')' {isNextToken(t_lookahead)}?
    | atom {isNextToken(t_lookahead)}?;


// *** Soft Keywords:  https://docs.python.org/3/reference/lexical_analysis.html#soft-keywords
match_skw              : {isCurrentToken("match")}? NAME;
case_skw               : {isCurrentToken("case")}? NAME;
underscore_skw         : {isCurrentToken("_")}? NAME;
name_except_underscore : {is_notCurrentToken("_")}? NAME;
