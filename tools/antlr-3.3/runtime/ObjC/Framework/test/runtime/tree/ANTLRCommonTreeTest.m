//
//  ANTLRCommonTreeTest.m
//  ANTLR
//
//  Created by Ian Michell on 26/05/2010.
//  Copyright 2010 Ian Michell. All rights reserved.
//

#import "ANTLRBaseTree.h"
#import "ANTLRCommonTreeTest.h"
#import "ANTLRStringStream.h"
#import "ANTLRCommonTree.h"
#import "ANTLRCommonToken.h"
#import "ANTLRError.h"

@implementation ANTLRCommonTreeTest

-(void) test01InitAndRelease
{
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTree];
	STAssertNotNil(tree, @"Tree was nil");
	// FIXME: It doesn't do anything else, perhaps initWithTree should set something somewhere, java says no though...
    return;
}

-(void) test02InitWithTree
{
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTree];
	STAssertNotNil(tree, @"Tree was nil");
    if (tree != nil)
        STAssertEquals([tree getType], (NSInteger)ANTLRTokenTypeInvalid, @"Tree should have an invalid token type, because it has no token");
    // [tree release];
    return;
}

#ifdef DONTUSENOMO
-(void) testWithToken
{
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	token.line = 1;
	token.charPositionInLine = 4;
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	STAssertNotNil(tree, @"Tree was nil");
    if (tree != nil)
        STAssertNotNil(tree.token, @"Tree with token was nil");
    if (tree != nil && tree.token != nil) {
        STAssertEquals(tree.token.line, (NSInteger)1, @"Tree should be at line 1");
        STAssertEquals(tree.token.charPositionInLine, (NSInteger)1, @"Char position should be 1");
        STAssertNotNil(tree.token.text, @"Tree with token with text was nil");
    }
    if (tree != nil && tree.token != nil && tree.token.text != nil)
        STAssertTrue([tree.token.text isEqualToString:@"||"], @"Text was not ||");
	//[tree release];
    return;
}
#endif

-(void) test03InvalidTreeNode
{
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:[ANTLRCommonToken invalidToken]];
	STAssertNotNil(tree, @"Tree was nil");
	STAssertEquals(tree.token.type, (NSInteger)ANTLRTokenTypeInvalid, @"Tree Token type was not ANTLRTokenTypeInvalid");
	//[tree release];
    return;
}

-(void) test04InitWithCommonTreeNode
{
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	STAssertNotNil(tree, @"Tree was nil");
	STAssertNotNil(tree.token, @"Tree token was nil");
	ANTLRCommonTree *newTree = [ANTLRCommonTree newANTLRCommonTreeWithTree:tree];
	STAssertNotNil(newTree, @"New tree was nil");
	STAssertNotNil(newTree.token, @"New tree token was nil");
	STAssertEquals(newTree.token, tree.token, @"Tokens did not match");
	STAssertEquals(newTree.stopIndex, tree.stopIndex, @"Token stop index did not match");
	STAssertEquals(newTree.startIndex, tree.startIndex, @"Token start index did not match");
	//[stream release];
	//[tree release];
	//[newTree release];
	//[token release];
    return;
}

-(void) test05CopyTree
{
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	STAssertNotNil(tree, @"Tree was nil");
	ANTLRCommonTree *newTree = [tree copyWithZone:nil];
	STAssertTrue([newTree isKindOfClass:[ANTLRCommonTree class]], @"Copied tree was not an ANTLRCommonTree");
	STAssertNotNil(newTree, @"New tree was nil");
	// STAssertEquals(newTree.token, tree.token, @"Tokens did not match");
	STAssertEquals(newTree.stopIndex, tree.stopIndex, @"Token stop index did not match");
	STAssertEquals(newTree.startIndex, tree.startIndex, @"Token start index did not match");
	//[stream release];
	//[tree release];
	//[newTree release];
	// [token release];
    return;
}

-(void) test06Description
{
    NSString *aString;
	ANTLRCommonTree *errorTree = [ANTLRCommonTree invalidNode];
	STAssertNotNil(errorTree, @"Error tree node is nil");
    if (errorTree != nil) {
        aString = [errorTree description];
        STAssertNotNil( aString, @"errorTree description returned nil");
        if (aString != nil)
            STAssertTrue([aString isEqualToString:@"<errornode>"], @"Not a valid error node description %@", aString);
    }
	//[errorTree release];
	
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	STAssertNotNil(tree, @"Tree is nil");
    if (tree != nil)
        STAssertNil([tree description], @"Tree description was not nil, was: %@", [tree description]);
	//[tree release];
	
	tree = [ANTLRCommonTree newANTLRCommonTree];
	STAssertNotNil(tree, @"Tree is nil");
    if (tree != nil) {
        aString = [tree description];
        STAssertNotNil(aString, @"tree description returned nil");
        if (aString != nil)
            STAssertTrue([aString isEqualToString:@"nil"], @"Tree description was not empty", [tree description]);
    }
	//[tree release];
	
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	STAssertNotNil(tree, @"Tree node is nil");
    aString = [tree description];
    STAssertNotNil(aString, @"tree description returned nil");
    if (aString != nil)
        STAssertTrue([aString isEqualToString:@"||"], @"description was not || was instead %@", [tree description]);
	//[tree release];
    return;
}

-(void) test07Text
{
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	STAssertNotNil(tree, @"Tree was nil");
	STAssertTrue([tree.token.text isEqualToString:@"||"], @"Tree text was not valid, should have been || was %@", tree.token.text);
	//[tree release];
	
	// test nil (for line coverage)
	tree = [ANTLRCommonTree newANTLRCommonTree];
	STAssertNotNil(tree, @"Tree was nil");
	STAssertNil(tree.token.text, @"Tree text was not nil: %@", tree.token.text);
    return;
}

-(void) test08AddChild
{
	// Create a new tree
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:555];
    parent.token.line = 1;
	parent.token.charPositionInLine = 1;
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	token.line = 1;
	token.charPositionInLine = 4;
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	// Add a child to the parent tree
	[parent addChild:tree];


	STAssertNotNil(parent, @"parent was nil");
    if (parent != nil)
        STAssertNotNil(parent.token, @"parent was nil");
	STAssertEquals((NSInteger)parent.token.line, (NSInteger)1, @"Tree should be at line 1 but is %d", parent.token.line);
	STAssertEquals((NSInteger)parent.token.charPositionInLine, (NSInteger)1, @"Char position should be 1 but is %d", parent.token.charPositionInLine);
	
	STAssertEquals((NSInteger)[parent getChildCount], (NSInteger)1, @"There should be 1 child but there were %d", [parent getChildCount]);
	STAssertEquals((NSInteger)[[parent getChild:0] getChildIndex], (NSInteger)0, @"Child index should be 0 was : %d", [[parent getChild:0] getChildIndex]);
	STAssertEquals([[parent getChild:0] getParent], parent, @"Parent not set for child");
	
	//[parent release];
    return;
}

-(void) test09AddChildren
{
	// Create a new tree
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	token.line = 1;
	token.charPositionInLine = 4;
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	// Add a child to the parent tree
	[parent addChild: tree];
	
	ANTLRCommonTree *newParent = [ANTLRCommonTree newANTLRCommonTree];
	[newParent addChildren:parent.children];
	
	STAssertEquals([newParent getChild:0], [parent getChild:0], @"Children did not match");
    return;
}

-(void) test10AddSelfAsChild
{
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	@try 
	{
		[parent addChild:parent];
	}
	@catch (NSException *e) 
	{
		STAssertTrue([[e name] isEqualToString:ANTLRIllegalArgumentException], @"Got wrong kind of exception! %@", [e name]);
		//[parent release];
		return;
	}
	STFail(@"Did not get an exception when adding an empty child!");
    return;
}

-(void) test11AddEmptyChildWithNoChildren
{
	ANTLRCommonTree *emptyChild = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	[parent addChild:emptyChild];
	STAssertEquals((NSInteger)[parent getChildCount], (NSInteger)0, @"There were supposed to be no children!");
	//[parent release];
	//[emptyChild release];
    return;
}

-(void) test12AddEmptyChildWithChildren
{
	// Create a new tree
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	token.line = 1;
	token.charPositionInLine = 4;
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	// Add a child to the parent tree
	[parent addChild: tree];
	
	ANTLRCommonTree *newParent = [ANTLRCommonTree newANTLRCommonTree];
	[newParent addChild:parent];
	
	STAssertEquals((NSInteger)[newParent getChildCount], (NSInteger)1, @"Parent should only have 1 child: %d", [newParent getChildCount]);
	STAssertEquals([newParent getChild:0], tree, @"Child was not the correct object.");
	//[parent release];
	//[newParent release];
	//[tree release];
    return;
}

-(void) test13ChildAtIndex
{
	// Create a new tree
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	// Add a child to the parent tree
	[parent addChild: tree];
	
	STAssertEquals((NSInteger)[parent getChildCount], (NSInteger)1, @"There were either no children or more than 1: %d", [parent getChildCount]);
	
	ANTLRCommonTree *child = [parent getChild:0];
	STAssertNotNil(child, @"Child at index 0 should not be nil");
	STAssertEquals(child, tree, @"Child and Original tree were not the same");
	//[parent release];
    return;
}

-(void) test14SetChildAtIndex
{
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	
	tree = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	tree.token.text = @"<UP>";
	[parent addChild:tree];
	
	STAssertTrue([parent getChild:0] == tree, @"Trees don't match");
	[parent setChild:0 With:tree];
	
	ANTLRCommonTree *child = [parent getChild:0];
	STAssertTrue([parent getChildCount] == 1, @"There were either no children or more than 1: %d", [parent getChildCount]);
	STAssertNotNil(child, @"Child at index 0 should not be nil");
	STAssertEquals(child, tree, @"Child and Original tree were not the same");
	//[parent release];
    return;
}

-(void) test15GetAncestor
{
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	parent.token.text = @"<UP>";
	
	ANTLRCommonTree *down = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	down.token.text = @"<DOWN>";
	
	[parent addChild:down];
	
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	[down addChild:tree];
	STAssertTrue([tree hasAncestor:ANTLRTokenTypeUP], @"Should have an ancestor of type ANTLRTokenTypeUP");
	
	ANTLRCommonTree *ancestor = [tree getAncestor:ANTLRTokenTypeUP];
	STAssertNotNil(ancestor, @"Ancestor should not be nil");
	STAssertEquals(ancestor, parent, @"Acenstors do not match");
	//[parent release];
    return;
}

-(void) test16FirstChildWithType
{
	// Create a new tree
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	
	ANTLRCommonTree *up = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	ANTLRCommonTree *down = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	
	[parent addChild:up];
	[parent addChild:down];
	
	ANTLRCommonTree *found = [parent getFirstChildWithType:ANTLRTokenTypeDOWN];
	STAssertNotNil(found, @"Child with type DOWN should not be nil");
    if (found != nil) {
        STAssertNotNil(found.token, @"Child token with type DOWN should not be nil");
        if (found.token != nil)
            STAssertEquals((NSInteger)found.token.type, (NSInteger)ANTLRTokenTypeDOWN, @"Token type was not correct, should be down!");
    }
	found = [parent getFirstChildWithType:ANTLRTokenTypeUP];
	STAssertNotNil(found, @"Child with type UP should not be nil");
    if (found != nil) {
        STAssertNotNil(found.token, @"Child token with type UP should not be nil");
        if (found.token != nil)
            STAssertEquals((NSInteger)found.token.type, (NSInteger)ANTLRTokenTypeUP, @"Token type was not correct, should be up!");
    }
	//[parent release];
    return;
}

-(void) test17SanityCheckParentAndChildIndexesForParentTree
{
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:555];
	STAssertNotNil(tree, @"tree should not be nil");
	@try 
	{
		[tree sanityCheckParentAndChildIndexes];
	}
	@catch (NSException * e) 
	{
		STFail(@"Exception was thrown and this is not what's right...");
	}
	
	BOOL passed = NO;
	@try 
	{
		[tree sanityCheckParentAndChildIndexes:parent At:0];
	}
	@catch (NSException * e) 
	{
		STAssertTrue([[e name] isEqualToString:ANTLRIllegalArgumentException], @"Exception was not an ANTLRIllegalArgumentException but was %@", [e name]);
		passed = YES;
	}
	if (!passed)
	{
		STFail(@"An exception should have been thrown");
	}
	
	STAssertNotNil(parent, @"parent should not be nil");
	[parent addChild:tree];
	@try 
	{
		[tree sanityCheckParentAndChildIndexes:parent At:0];
	}
	@catch (NSException * e) 
	{
		STFail(@"No exception should have been thrown!");
	}
    return;
}

-(void) test18DeleteChild
{
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	[parent addChild:tree];
	
	ANTLRCommonTree *deletedChild = [parent deleteChild:0];
	STAssertEquals(deletedChild, tree, @"Children do not match!");
	STAssertEquals((NSInteger)[parent getChildCount], (NSInteger)0, @"Child count should be zero!");
    return;
}

-(void) test19TreeDescriptions
{
	// Child tree
	ANTLRStringStream *stream = [ANTLRStringStream newANTLRStringStream:@"this||is||a||double||piped||separated||csv"];
	ANTLRCommonToken *token = [ANTLRCommonToken newANTLRCommonToken:stream Type:555 Channel:ANTLRTokenChannelDefault Start:4 Stop:6];
	ANTLRCommonTree *tree = [ANTLRCommonTree newANTLRCommonTreeWithToken:token];
	
	// Description for tree
	NSString *treeDesc = [tree treeDescription];
    STAssertNotNil(treeDesc, @"Tree description should not be nil");
    STAssertTrue([treeDesc isEqualToString:@"||"], @"Tree description was not || but rather %@", treeDesc);
	
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	STAssertTrue([[parent treeDescription] isEqualToString:@"nil"], @"Tree description was not nil was %@", [parent treeDescription]);
	[parent addChild:tree];
	treeDesc = [parent treeDescription];
	STAssertTrue([treeDesc isEqualToString:@"||"], @"Tree description was not || but was: %@", treeDesc);
	
	// Test non empty parent
	ANTLRCommonTree *down = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	down.token.text = @"<DOWN>";
	
	[tree addChild:down];
	treeDesc = [parent treeDescription];
	STAssertTrue([treeDesc isEqualToString:@"(|| <DOWN>)"], @"Tree description was wrong expected (|| <DOWN>) but got: %@", treeDesc);
    return;
}

-(void) test20ReplaceChildrenAtIndexWithNoChildren
{
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *parent2 = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	child.token.text = @"<DOWN>";
	[parent2 addChild:child];
	@try 
	{
		[parent replaceChildrenFrom:1 To:2 With:parent2];
	}
	@catch (NSException *ex)
	{
		STAssertTrue([[ex name] isEqualToString:ANTLRIllegalArgumentException], @"Expected an illegal argument exception... Got instead: %@", [ex name]);
		return;
	}
	STFail(@"Exception was not thrown when I tried to replace a child on a parent with no children");
    return;
}

-(void) test21ReplaceChildrenAtIndex
{
	ANTLRCommonTree *parent1 = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child1 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	[parent1 addChild:child1];
	ANTLRCommonTree *parent2 = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child2 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	child2.token.text = @"<DOWN>";
	[parent2 addChild:child2];
	
	[parent2 replaceChildrenFrom:0 To:0 With:parent1];
	
	STAssertEquals([parent2 getChild:0], child1, @"Child for parent 2 should have been from parent 1");
    return;
}

-(void) test22ReplaceChildrenAtIndexWithChild
{
	ANTLRCommonTree *replacement = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	replacement.token.text = @"<UP>";
	ANTLRCommonTree *parent = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	child.token.text = @"<DOWN>";
	[parent addChild:child];
	
	[parent replaceChildrenFrom:0 To:0 With:replacement];
	
	STAssertTrue([parent getChild:0] == replacement, @"Children do not match");
    return;
}

-(void) test23ReplacechildrenAtIndexWithLessChildren
{
	ANTLRCommonTree *parent1 = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child1 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	[parent1 addChild:child1];
	
	ANTLRCommonTree *parent2 = [ANTLRCommonTree newANTLRCommonTree];
	
	ANTLRCommonTree *child2 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeEOF];
	[parent2 addChild:child2];
	
	ANTLRCommonTree *child3 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	child2.token.text = @"<DOWN>";
	[parent2 addChild:child3];
	
	[parent2 replaceChildrenFrom:0 To:1 With:parent1];
	STAssertEquals((NSInteger)[parent2 getChildCount], (NSInteger)1, @"Should have one child but has %d", [parent2 getChildCount]);
	STAssertEquals([parent2 getChild:0], child1, @"Child for parent 2 should have been from parent 1");
    return;
}

-(void) test24ReplacechildrenAtIndexWithMoreChildren
{
	ANTLRCommonTree *parent1 = [ANTLRCommonTree newANTLRCommonTree];
	ANTLRCommonTree *child1 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeUP];
	[parent1 addChild:child1];
	ANTLRCommonTree *child2 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeEOF];
	[parent1 addChild:child2];
	
	ANTLRCommonTree *parent2 = [ANTLRCommonTree newANTLRCommonTree];
	
	ANTLRCommonTree *child3 = [ANTLRCommonTree newANTLRCommonTreeWithTokenType:ANTLRTokenTypeDOWN];
	child2.token.text = @"<DOWN>";
	[parent2 addChild:child3];
	
	[parent2 replaceChildrenFrom:0 To:0 With:parent1];
	STAssertEquals((NSInteger)[parent2 getChildCount], (NSInteger)2, @"Should have one child but has %d", [parent2 getChildCount]);
	STAssertEquals([parent2 getChild:0], child1, @"Child for parent 2 should have been from parent 1");
	STAssertEquals([parent2 getChild:1], child2, @"An extra child (child2) should be in the children collection");
    return;
}

@end
