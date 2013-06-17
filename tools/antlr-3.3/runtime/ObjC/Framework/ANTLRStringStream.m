// [The "BSD licence"]
// Copyright (c) 2006-2007 Kay Roepke 2010 Alan Condit
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products
//    derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


#import "ANTLRStringStream.h"

@implementation ANTLRStringStream

@synthesize data;
@synthesize n;
@synthesize p;
@synthesize line;
@synthesize charPositionInLine;
@synthesize markDepth;
@synthesize lastMarker;
@synthesize markers;
@synthesize name;
@synthesize charState;

+ newANTLRStringStream
{
    return [[ANTLRStringStream alloc] init];
}

+ newANTLRStringStream:(NSString *)aString;
{
    return [[ANTLRStringStream alloc] initWithString:aString];
}


+ newANTLRStringStream:(char *)myData Count:(NSInteger)numBytes;
{
    return [[ANTLRStringStream alloc] initWithData:myData Count:numBytes];
}


- (id) init
{
	if ((self = [super init]) != nil) {
        n = 0;
        p = 0;
        line = 1;
        charPositionInLine = 0;
        markDepth = 0;
		markers = [ANTLRPtrBuffer newANTLRPtrBufferWithLen:10];
        [markers retain];
        [markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
        charState = [[ANTLRCharStreamState newANTLRCharStreamState] retain];
	}
	return self;
}

- (id) initWithString:(NSString *) theString
{
	if ((self = [super init]) != nil) {
		//[self setData:[NSString stringWithString:theString]];
        data = [theString retain];
        n = [data length];
        p = 0;
        line = 1;
        charPositionInLine = 0;
        markDepth = 0;
		markers = [[ANTLRPtrBuffer newANTLRPtrBufferWithLen:10] retain];
        [markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
        charState = [[ANTLRCharStreamState newANTLRCharStreamState] retain];
	}
	return self;
}

- (id) initWithStringNoCopy:(NSString *) theString
{
	if ((self = [super init]) != nil) {
		//[self setData:theString];
        data = [theString retain];
        n = [data length];
        p = 0;
        line = 1;
        charPositionInLine = 0;
        markDepth = 0;
		markers = [ANTLRPtrBuffer newANTLRPtrBufferWithLen:100];
        [markers retain];
        [markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
        charState = [[ANTLRCharStreamState newANTLRCharStreamState] retain];
	}
	return self;
}

- (id) initWithData:(char *)myData Count:(NSInteger)numBytes
{
    if ((self = [super init]) != nil) {
        data = [NSString stringWithCString:myData encoding:NSASCIIStringEncoding];
        n = numBytes;
        p = 0;
        line = 1;
        charPositionInLine = 0;
        markDepth = 0;
		markers = [ANTLRPtrBuffer newANTLRPtrBufferWithLen:100];
        [markers retain];
        [markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
        charState = [[ANTLRCharStreamState newANTLRCharStreamState] retain];
    }
    return( self );
}

- (void) dealloc
{
    if (markers != nil) {
        [markers removeAllObjects];
        [markers release];
    }
    if (data != nil) [data release];
	markers = nil;
    data = nil;
	[super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    ANTLRStringStream *copy;
	
    copy = [[[self class] allocWithZone:aZone] init];
    //    copy = [super copyWithZone:aZone]; // allocation occurs here
    if ( data != nil )
        copy.data = [self.data copyWithZone:aZone];
    copy.n = n;
    copy.p = p;
    copy.line = line;
    copy.charPositionInLine = charPositionInLine;
    copy.markDepth = markDepth;
    if ( markers != nil )
        copy.markers = [markers copyWithZone:nil];
    copy.lastMarker = lastMarker;
    if ( name != nil )
        copy.name = [self.name copyWithZone:aZone];
    return copy;
}

// reset the streams charState
// the streams content is not reset!
- (void) reset
{
	p = 0;
	line = 1;
	charPositionInLine = 0;
	markDepth = 0;
	[markers removeAllObjects];
    [markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
											// thus the initial null in the array!
}

// read one character off the stream, tracking line numbers and character positions
// automatically.
// Override this in subclasses if you want to avoid the overhead of automatic line/pos
// handling. Do not call super in that case.
- (void) consume 
{
	if ( p < n ) {
		charPositionInLine++;
		if ( [data characterAtIndex:p] == '\n' ) {
			line++;
			charPositionInLine=0;
		}
		p++;
	}
}

// implement the lookahead method used in lexers
- (NSInteger) LA:(NSInteger) i 
{
    NSInteger c;
    if ( i == 0 )
        return 0; // undefined
    if ( i < 0 ) {
        i++;
        if ( p+i-1 < 0 ) {
		    return ANTLRCharStreamEOF;
		}
	}
    if ( (p+i-1) >= n ) {
		return ANTLRCharStreamEOF;
	}
    c = [data characterAtIndex:p+i-1];
	return (NSInteger)c;
}

- (NSInteger) LT:(NSInteger)i
{
    return [self LA:i];
}

// current input position
- (NSInteger) getIndex 
{
	return p;
}

- (NSInteger) size 
{
	return n;
}

// push the current charState of the stream onto a stack
// returns the depth of the stack, to be used as a marker to rewind the stream.
// Note: markers are 1-based!
- (NSInteger) mark 
{
    // NSLog(@"mark entry -- markers=%x, markDepth=%d\n", markers, markDepth);
    if ( markers == nil ) {
        markers = [ANTLRPtrBuffer newANTLRPtrBufferWithLen:100];
		[markers addObject:[NSNull null]]; // ANTLR generates code that assumes markers to be 1-based,
        markDepth = markers.ptr;
    }
    markDepth++;
	ANTLRCharStreamState *State = nil;
	if ( (markDepth) >= [markers count] ) {
        if ( markDepth > 1 ) {
            State = [ANTLRCharStreamState newANTLRCharStreamState];
            [State retain];
        }
        if ( markDepth == 1 )
            State = charState;
		[markers insertObject:State atIndex:markDepth];
        // NSLog(@"mark save State %x at %d, p=%d, line=%d, charPositionInLine=%d\n", State, markDepth, State.p, State.line, State.charPositionInLine);
	}
	else {
        // NSLog(@"mark retrieve markers=%x markDepth=%d\n", markers, markDepth);
        State = [markers objectAtIndex:markDepth];
        [State retain];
        State = (ANTLRCharStreamState *)[markers objectAtIndex:markDepth];
        // NSLog(@"mark retrieve charState %x from %d, p=%d, line=%d, charPositionInLine=%d\n", charState, markDepth, charState.p, charState.line, charState.charPositionInLine);
	}
    State.p = p;
	State.line = line;
	State.charPositionInLine = charPositionInLine;
	lastMarker = markDepth;
    // NSLog(@"mark exit -- markers=%x, charState=%x, p=%d, line=%d, charPositionInLine=%d\n", markers, charState, charState.p, charState.line, charState.charPositionInLine);
	return markDepth;
}

- (void) rewind:(NSInteger) marker 
{
    ANTLRCharStreamState *State;
    // NSLog(@"rewind entry -- markers=%x marker=%d\n", markers, marker);
    if ( marker == 1 )
        State = charState;
    else
        State = (ANTLRCharStreamState *)[markers objectAtIndex:marker];
    // NSLog(@"rewind entry -- marker=%d charState=%x, p=%d, line=%d, charPositionInLine=%d\n", marker, charState, charState.p, charState.line, charState.charPositionInLine);
	// restore stream charState
	[self seek:State.p];
	line = State.line;
	charPositionInLine = charState.charPositionInLine;
	[self release:marker];
    // NSLog(@"rewind exit -- marker=%d charState=%x, p=%d, line=%d, charPositionInLine=%d\n", marker, charState, charState.p, charState.line, charState.charPositionInLine);
}

- (void) rewind
{
	[self rewind:lastMarker];
}

// remove stream states on top of 'marker' from the marker stack
// returns the new markDepth of the stack.
// Note: unfortunate naming for Objective-C, but to keep close to the Java target this is named release:
- (void) release:(NSInteger) marker 
{
	// unwind any other markers made after marker and release marker
	markDepth = marker;
	markDepth--;
    // NSLog(@"release:marker= %d, markDepth = %d\n", marker, markDepth);
}

// when seeking forward we must handle character position and line numbers.
// seeking backward already has the correct line information on the markers stack, 
// so we just take it from there.
- (void) seek:(NSInteger) index 
{
    // NSLog(@"seek entry -- index=%d p=%d\n", index, p);
	if ( index <= p ) {
		p = index; // just jump; don't update stream charState (line, ...)
        // NSLog(@"seek exit return -- index=%d p=%d\n", index, p);
		return;
	}
	// seek forward, consume until p hits index
	while ( p < index ) {
		[self consume];
	}
    // NSLog(@"seek exit end -- index=%d p=%d\n", index, p);
}

// get a substring from our raw data.
- (NSString *) substring:(NSInteger)startIndex To:(NSInteger)stopIndex 
{
    NSRange theRange = NSMakeRange(startIndex, stopIndex-startIndex);
	return [data substringWithRange:theRange];
}

// get a substring from our raw data.
- (NSString *) substringWithRange:(NSRange) theRange 
{
	return [data substringWithRange:theRange];
}


- (NSInteger) getP
{
    return p;
}

- (void) setP:(NSInteger)num
{
    p = num;
}

- (NSInteger) getN
{
    return n;
}

- (void) setN:(NSInteger)num
{
    n = num;
}

- (NSInteger) getLine 
{
	return line;
}

- (void) setLine:(NSInteger) theLine 
{
	line = theLine;
}

- (NSInteger) getCharPositionInLine 
{
	return charPositionInLine;
}

- (void) setCharPositionInLine:(NSInteger) thePos 
{
	charPositionInLine = thePos;
}

- (ANTLRPtrBuffer *)getMarkers
{
    return markers;
}

- (void) setMarkers:(ANTLRPtrBuffer *)aMarkerList
{
    markers = aMarkerList;
}

- (NSString *)getSourceName
{
    return name;
}

- (void) setSourceName:(NSString *)aName
{
    name = aName;
}


- (ANTLRCharStreamState *)getCharState
{
    return charState;
}

- (void) setCharState:(ANTLRCharStreamState *)aCharState
{
    charState = aCharState;
}

- (NSString *)toString
{
    return [NSString stringWithString:data];
}

//---------------------------------------------------------- 
//  data 
//---------------------------------------------------------- 
- (NSString *) data
{
    return data; 
}

- (void) setData: (NSString *) aData
{
    if (data != aData) {
        data = [NSString stringWithString:aData];
        [data retain];
        [aData release];
    }
}

@end
