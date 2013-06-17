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
tree grammar MDTTreeWalker;

options {
    tokenVocab=MDT;
    ASTLabelType=CommonTree;
}

@header {
package net.xclinical.mdt;
import net.xclinical.mdt.ast.*;
}

@members {
	Block block;
}

start[Block rootBlock]
	@init {
		block = rootBlock;
	}
	@after {
		if (block != rootBlock) throw new RuntimeException("Unbalanced blocks");
	}
	: name_def* import_def* (rule|def)*;

name_def: ^(NAME constant)
	;
	
import_def: ^(IMPORT ID)
	{
		block.addStatement(new Import($ID.text));
	}
	;

rule returns [Rule value]
	@init {
		final Block prev = block;
		Rule r = new Rule();
		block.addStatement(r);
		block = r.getBlock();
	}	
	@after {
		block = prev;
	}
	: ^(RULE xpath rule_def)
	{
		r.setXPath($xpath.value);
		$value = r;
	}
	;
 
xpath returns [Reference value]
	: ^(XPATH expr)
	{
		$value = new XPathReference($expr.value);	
	}
	;
	
rule_def
	: decl|block|block_ref
	;

decl returns [Reference value]
	@init {
		final Block prev = block;
		Declaration d = new Declaration();
		block.addStatement(d);
		block = d.getBlock();
	}
	@after {
		block = prev;
	}
	: ^(DECL ID expr? block)
	{
		d.setCoalesce($expr.value);
		d.setIdentifier($ID.text);
		$value = d;
	}
	;

conditional returns [Reference value]
	@init {
		final Block prev = block;
		
		Conditional c = new Conditional();
		block.addStatement(c);
		block = c.getBlock();
	}
	@after {
		block = prev;
	}
	: ^(IF expr block)
	{
		c.setCondition($expr.value);
		$value = c;
	}
	;

block	
	: ^(BLOCK stmt*)
	;

block_ref	
	: ID
	{
		block.addStatement(new BlockReference($ID.text));
	}
	;

stmt	
	: def | decl | conditional | assign | var | rule | func[true]
	;

assign 	
	: ^(ASSIGN ID expr)
	{
		block.addStatement(new Assignment($ID.text, $expr.value));
	}
	;

expr returns [Reference value]
	: ^('+' l=expr r=expr) {$value = new AddReference($l.value, $r.value);}
	| ^('!' e=expr) {$value = new NotReference($e.value);}
	| atom {$value = $atom.value;}
	| decl {$value = $decl.value;}
	;

atom returns [Reference value]
	: constant {$value = $constant.value;}
	| func[false] {$value = $func.value;}
	| xpath {$value = $xpath.value;}	
	;

constant returns [Reference value]
	: FLOAT {$value = new ObjectReference(Float.parseFloat($FLOAT.text));} 
	| INT {$value = new ObjectReference(Long.parseLong($INT.text));} 
	| STRING {$value = new ObjectReference($STRING.text);} 
	| CHAR {$value = new ObjectReference($CHAR.text);}
	| TRUE {$value = new ObjectReference(true);}
	| FALSE {$value = new ObjectReference(false);}
	| ID {$value = new LookupReference($ID.text);}
	;

var	: 
	^(VAR ID expr)
	{
		block.addStatement(new VariableDeclaration($ID.text, $expr.value));
	}
	;

def	 
	@init {
		final Block prev = block;
		Definition d = new Definition();
		block.addStatement(d);
		block = d.getBlock();
	}
	@after {
		block = prev;
	}
	: ^(DEF ID block)
	{
		d.setIdentifier($ID.text);
	}
	;

add_param[Function f] 
	: c=expr
	{
		f.addParameter($c.value);
	}
	;
	
params[Function f]
	: add_param[f]* 
	;
	
func[boolean add] returns [Reference value]
	@init {
		Function f = new Function();
	}
	: ^(INVOKE ID params[f])
	{
		f.setSpec($ID.text);
		
		if (add) {
			block.addStatement(f);
		}
		
		$value = f;
	}
	;

