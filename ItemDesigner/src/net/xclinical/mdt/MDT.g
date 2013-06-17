/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
grammar MDT;

options { output = AST; }

tokens {
	RULE;
	BLOCK;
	DECL;
	INVOKE;
	XPATH;
	ASSIGN = '=';
	IMPORT = 'import';
	IF = 'if';
	VAR = 'var';
	DEF = 'def';
}

@header {
	package net.xclinical.mdt;
}

@lexer::header {
	package net.xclinical.mdt;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
start	: name_def* import_def* (rule|def)* EOF!;

name_def 	: NAME constant
			-> ^(NAME constant)
			;

import_def 	: IMPORT ID TERM
			-> ^(IMPORT ID)
			;

rule	: xpath rule_def
		-> ^(RULE xpath rule_def)
		;

rule_def : (decl|block|ID TERM!);

decl	: ID (':' expr)? block
		-> ^(DECL ID expr? block)
		;

var		: VAR ID ASSIGN rvalue
		-> ^(VAR ID rvalue)
		;

def		: DEF ID block
		-> ^(DEF ID block)
		;

assign 	: ID ASSIGN rvalue
		-> ^(ASSIGN ID rvalue)
		;

xpath	: '[' expr ']'
		-> ^(XPATH expr)
		;
		
block	: '{' stmt* '}'
		-> ^(BLOCK stmt*)
		;

conditional
		: IF '(' expr ')' block
		-> ^(IF expr block) 
		;
		
constant	: FLOAT | INT | STRING | CHAR | TRUE | FALSE | ID;

expr
	: compareExpression
	;

compareExpression
	:	additiveExpression ( ( '==' | '!=' )^ additiveExpression )*
	;
	
additiveExpression
    :   (multiplicativeExpression) (('+' | '-')^ multiplicativeExpression)*
    ;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' )^ unaryExpression )*
    ;

unaryExpression
	:	'-'	unaryExpression
	->	^('-' unaryExpression)
  	|   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    -> ^('~' unaryExpression)
    |   '!' unaryExpression
    -> ^('!' unaryExpression)
    |   atom
	;	
			 
atom 	: constant
		| func
		| xpath
		;

rvalue	: (expr TERM!) | decl;

stmt	: rule
		| decl 
		| var
		| def 
		| assign
		| conditional
		| func TERM!
		;

param	: expr | decl;

params	: param (','! param)*;

func	: ID '(' params? ')'
		-> ^(INVOKE ID params?)
		;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

NOT	: '!';

TERM	: ';';

DOT	: '.';

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

TRUE	: 'true';
FALSE	: 'false';

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')*
    ;

NAME  :	'@' ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')*
    {
		setText(getText().substring(1));
	}    
    ;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ 	{ $channel = HIDDEN; } ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    {
		setText(getText().substring(1, getText().length()-1));
	}    
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    {
		setText(getText().substring(1, getText().length()-1));
	}
    ;

fragment DIGIT	: '0'..'9' ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
