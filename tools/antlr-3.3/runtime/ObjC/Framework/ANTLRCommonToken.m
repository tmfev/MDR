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


#import "ANTLRCommonToken.h"

static ANTLRCommonToken *SKIP_TOKEN;
static ANTLRCommonToken *EOF_TOKEN;
static ANTLRCommonToken *INVALID_TOKEN;

@implementation ANTLRCommonToken

// @synthesize text;
@synthesize type;
@synthesize line;
@synthesize charPositionInLine;
@synthesize channel;
@synthesize index;
@synthesize startIndex;
@synthesize stopIndex;
// @synthesize input;

+ (void) initialize
{
    EOF_TOKEN = [ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeEOF Text:@"EOF"];
    SKIP_TOKEN = [ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeInvalid Text:@"Skip"];
    INVALID_TOKEN = [ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeInvalid Text:@"Invalid"];
    [EOF_TOKEN retain];
    [SKIP_TOKEN retain];
    [INVALID_TOKEN retain];
}

+ (ANTLRCommonToken *) newANTLRCommonToken
{
    return [[ANTLRCommonToken alloc] init];
}

+ (ANTLRCommonToken *) newANTLRCommonToken:(id<ANTLRCharStream>)anInput Type:(NSInteger)aTType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop
{
    return [[ANTLRCommonToken alloc] initWithInput:(id<ANTLRCharStream>)anInput Type:(NSInteger)aTType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop];
}

+ (ANTLRCommonToken *) newANTLRCommonToken:(ANTLRTokenType)tokenType
{
    return( [[ANTLRCommonToken alloc] initWithType:tokenType] );
}

+ (ANTLRCommonToken *) newANTLRCommonToken:(NSInteger)tokenType Text:(NSString *)tokenText
{
    return( [[ANTLRCommonToken alloc] initWithType:tokenType Text:tokenText] );
}

+ (ANTLRCommonToken *) newANTLRCommonTokenWithToken:(ANTLRCommonToken *)fromToken
{
    return( [[ANTLRCommonToken alloc] initWithToken:fromToken] );
}

// return the singleton EOF Token 
+ (id<ANTLRToken>) eofToken
{
	if (EOF_TOKEN == nil) {
		EOF_TOKEN = [[ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeEOF Text:@"EOF"] retain];
	}
	return EOF_TOKEN;
}

// return the singleton skip Token 
+ (id<ANTLRToken>) skipToken
{
	if (SKIP_TOKEN == nil) {
		SKIP_TOKEN = [[ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeInvalid Text:@"Skip"] retain];
	}
	return SKIP_TOKEN;
}

// return the singleton skip Token 
+ (id<ANTLRToken>) invalidToken
{
	if (INVALID_TOKEN == nil) {
		INVALID_TOKEN = [[ANTLRCommonToken newANTLRCommonToken:ANTLRTokenTypeInvalid Text:@"Invalid"] retain];
	}
	return SKIP_TOKEN;
}

// the default channel for this class of Tokens
+ (ANTLRTokenChannel) defaultChannel
{
	return ANTLRTokenChannelDefault;
}

- (ANTLRCommonToken *) init
{
    if ((self = [super init]) != nil) {
        input = nil;
        type = ANTLRTokenTypeInvalid;
        channel = ANTLRTokenChannelDefault;
        startIndex = 0;
        stopIndex = 0;
    }
    return self;
}

// designated initializer
- (ANTLRCommonToken *) initWithInput:(id<ANTLRCharStream>)anInput
                           Type:(NSInteger)aTType
                             Channel:(NSInteger)aChannel
                               Start:(NSInteger)aStart
                                Stop:(NSInteger)aStop
{
    if ((self = [super init]) != nil) {
        input = anInput;
        type = aTType;
        channel = aChannel;
        startIndex = aStart;
        stopIndex = aStop;
    }
    return self;
}

- (ANTLRCommonToken *) initWithToken:(ANTLRCommonToken *)oldToken
{
    if ((self = [super init]) != nil) {
        text = oldToken.text;
        type = oldToken.type;
        line = oldToken.line;
        index = oldToken.index;
        charPositionInLine = oldToken.charPositionInLine;
        channel = oldToken.channel;
        input = oldToken.input;
        if ( [oldToken isKindOfClass:[ANTLRCommonToken class]] ) {
            startIndex = oldToken.startIndex;
            stopIndex = oldToken.stopIndex;
        }
    }
	return self;
}

- (ANTLRCommonToken *) initWithType:(ANTLRTokenType)aTType
{
	if ((self = [super init]) != nil) {
        self.type = aTType;
	}
	return self;
}

- (ANTLRCommonToken *) initWithType:(ANTLRTokenType)aTType Text:(NSString *)tokenText
{
	if ((self = [super init]) != nil) {
        self.type = aTType;
        self.text = tokenText;
	}
	return self;
}

- (void) dealloc
{
    [self setInput:nil];
    [self setText:nil];
    [super dealloc];
}

// create a copy, including the text if available
// the input stream is *not* copied!
- (id) copyWithZone:(NSZone *)theZone
{
    ANTLRCommonToken *copy = [[[self class] allocWithZone:theZone] init];
    
    if (text)
        copy.text = [text copyWithZone:nil];
    copy.type = type;
    copy.line = line;
    copy.charPositionInLine = charPositionInLine;
    copy.channel = channel;
    copy.index = index;
    copy.startIndex = startIndex;
    copy.stopIndex = stopIndex;
    copy.input = input;
    return copy;
}


//---------------------------------------------------------- 
//  text 
//---------------------------------------------------------- 
- (NSString *) getText
{
	if (text != nil) {
		return text;
	}
	if (input == nil) {
		return nil;
	}
	return [input substringWithRange:NSMakeRange(startIndex, stopIndex-startIndex)];
}

- (void) setText: (NSString *) aText
{
    if (text != aText) {
        [aText retain];
        [text release];
        text = aText;
    }
}


//---------------------------------------------------------- 
//  type 
//---------------------------------------------------------- 
- (NSInteger) getType
{
    return type;
}

- (void) setType: (NSInteger) aType
{
    type = aType;
}

//---------------------------------------------------------- 
//  line 
//---------------------------------------------------------- 
- (NSUInteger) getLine
{
    return line;
}

- (void) setLine: (NSUInteger) aLine
{
    line = aLine;
}

//---------------------------------------------------------- 
//  charPositionInLine 
//---------------------------------------------------------- 
- (NSUInteger) getCharPositionInLine
{
    return charPositionInLine;
}

- (void) setCharPositionInLine: (NSUInteger) aCharPositionInLine
{
    charPositionInLine = aCharPositionInLine;
}

//---------------------------------------------------------- 
//  channel 
//---------------------------------------------------------- 
- (NSUInteger) getChannel
{
    return channel;
}

- (void) setChannel: (NSUInteger) aChannel
{
    channel = aChannel;
}


//---------------------------------------------------------- 
//  input 
//---------------------------------------------------------- 
- (id<ANTLRCharStream>) getInput
{
    return input; 
}

- (void) setInput: (id<ANTLRCharStream>) anInput
{
    if (input != anInput) {
        [anInput retain];
        [input release];
        input = anInput;
    }
}


//---------------------------------------------------------- 
//  start 
//---------------------------------------------------------- 
- (NSUInteger) getStart
{
    return startIndex;
}

- (void) setStart: (NSUInteger) aStart
{
    startIndex = aStart;
}

//---------------------------------------------------------- 
//  stop 
//---------------------------------------------------------- 
- (NSUInteger) getStop
{
    return stopIndex;
}

- (void) setStop: (NSUInteger) aStop
{
    stopIndex = aStop;
}

//---------------------------------------------------------- 
//  index 
//---------------------------------------------------------- 
- (NSUInteger) getTokenIndex;
{
    return index;
}

- (void) setTokenIndex: (NSUInteger) aTokenIndex;
{
    index = aTokenIndex;
}


// provide a textual representation for debugging
- (NSString *) description
{
   return [self toString];
}

- (NSString *)toString
{
    NSMutableString *channelString;
    NSMutableString *txtString;

    if ( channel > 0 ) {
        channelString = [NSString stringWithFormat:@", channel=%d\n", channel];
    }
    else {
        channelString = [NSMutableString stringWithCapacity:25];
    }
	if ([self getText] != nil) {
		txtString = [NSMutableString stringWithString:[self getText]];
		[txtString replaceOccurrencesOfString:@"\n" withString:@"\\\n" options:NSAnchoredSearch range:NSMakeRange(0, [txtString length])];
		[txtString replaceOccurrencesOfString:@"\r" withString:@"\\\r" options:NSAnchoredSearch range:NSMakeRange(0, [txtString length])];
		[txtString replaceOccurrencesOfString:@"\t" withString:@"\\\t" options:NSAnchoredSearch range:NSMakeRange(0, [txtString length])];
	} else {
		txtString = [NSMutableString stringWithString:@"<no text>"];
    }
	return [NSString stringWithFormat:@"[@%d, %d, %d=%@,<%d>,channel=%d,%d:%d]", index, startIndex, stopIndex, txtString, type, channel, line, charPositionInLine];
}

@end
