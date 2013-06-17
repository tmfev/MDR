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

#import "ANTLRToken.h"
#import "ANTLRCommonTokenStream.h"


@implementation ANTLRCommonTokenStream

@synthesize channelOverride;
@synthesize channel;

#pragma mark Initialization

+ (ANTLRCommonTokenStream *)newANTLRCommonTokenStream
{
    return [[ANTLRCommonTokenStream alloc] init];
}

+ (ANTLRCommonTokenStream *)newANTLRCommonTokenStreamWithTokenSource:(id<ANTLRTokenSource>)theTokenSource
{
    return [[ANTLRCommonTokenStream alloc] initWithTokenSource:(id<ANTLRTokenSource>)theTokenSource];
}

+ (ANTLRCommonTokenStream *)newANTLRCommonTokenStreamWithTokenSource:(id<ANTLRTokenSource>)theTokenSource
                                                               Channel:(NSInteger)aChannel
{
    return [[ANTLRCommonTokenStream alloc] initWithTokenSource:(id<ANTLRTokenSource>)theTokenSource
                                                       Channel:aChannel];
}

- (id) init
{
	if ((self = [super init]) != nil) {
		p = -1;
		tokens = [[NSMutableArray arrayWithCapacity:1000] retain];
		channelOverride = [[NSMutableDictionary dictionaryWithCapacity:100] retain];
		channel = ANTLRTokenChannelDefault;
        tokenSource = nil;
	}
	return self;
}

- (id) initWithTokenSource:(id<ANTLRTokenSource>)theTokenSource
{
	if ((self = [super init]) != nil) {
		p = -1;
		tokens = [[NSMutableArray arrayWithCapacity:1000] retain];
		channelOverride = [[NSMutableDictionary dictionaryWithCapacity:100] retain];
		channel = ANTLRTokenChannelDefault;
		tokenSource = theTokenSource;
	}
	return self;
}

- (id) initWithTokenSource:(id<ANTLRTokenSource>)theTokenSource Channel:(NSInteger)aChannel
{
	if ((self = [super init]) != nil) {
		p = -1;
		tokens = [[NSMutableArray arrayWithCapacity:1000] retain];
		channelOverride = [[NSMutableDictionary dictionaryWithCapacity:100] retain];
		tokenSource = theTokenSource;
		channel = aChannel;
	}
	return self;
}

- (void) dealloc
{
	[channelOverride release];
	[tokens release];
	[self setTokenSource:nil];
	[super dealloc];
}

#pragma mark Accessors

- (id<ANTLRTokenSource>) getTokenSource
{
    return tokenSource; 
}

/** Reset this token stream by setting its token source. */
- (void) setTokenSource:(id<ANTLRTokenSource>)aTokenSource
{
    [super setTokenSource:aTokenSource];
    channel = ANTLRTokenChannelDefault;
}

/** Always leave p on an on-channel token. */
- (void) consume
{
    if (p == -1) [self setup];
    p++;
    [self sync:p];
    while ( [[tokens objectAtIndex:p] getChannel] != channel ) {
		p++;
		[self sync:p];
	}
}

#pragma mark Lookahead

- (id<ANTLRToken>) LB:(NSInteger)k
{
	if ( k == 0 || (p-k) < 0 ) {
		return nil;
	}
	int i = p;
	int n = 1;
    // find k good tokens looking backwards
	while ( n <= k ) {
		i = [self skipOffChannelTokensReverse:i-1];
		n++;
	}
	if ( i < 0 ) {
		return nil;
	}
	return [tokens objectAtIndex:i];
}

- (id<ANTLRToken>) LT:(NSInteger)k
{
	if ( p == -1 ) [self setup];
	if ( k == 0 ) return nil;
	if ( k < 0 ) return [self LB:-k];
	int i = p;
	int n = 1;
	while ( n < k ) {
		i = [self skipOffChannelTokens:i+1];
		n++;
	}
	if ( i >= (NSInteger)[tokens count] ) {
		return [ANTLRCommonToken eofToken];
	}
    if (i > range) range = i;
	return [tokens objectAtIndex:i];
}

#pragma mark Channels & Skipping

- (NSInteger) skipOffChannelTokens:(NSInteger) idx
{
    [self sync:idx];
	while ( [[tokens objectAtIndex:idx] getChannel] != channel ) {
		idx++;
        [self sync:idx];
	}
	return idx;
}

- (NSInteger) skipOffChannelTokensReverse:(NSInteger) i
{
	while ( i >= 0 && [(id<ANTLRToken>)[tokens objectAtIndex:i] getChannel] != channel ) {
		i--;
	}
	return i;
}

- (void) setup
{
    p = 0;
    [self sync:0];
    int index = 0;
    while ( [((id<ANTLRToken>)[tokens objectAtIndex:index]) getChannel] != channel ) {
        index++;
        [self sync:index];
    }
	// leave p pointing at first token on channel
    p = index;
}

#pragma mark Token access

- (NSArray *) tokensInRange:(NSRange)aRange
{
	return [tokens subarrayWithRange:aRange];
}

- (NSArray *) tokensInRange:(NSRange)aRange inBitSet:(ANTLRBitSet *)aBitSet
{
	unsigned int startIndex = aRange.location;
	unsigned int stopIndex = aRange.location+aRange.length;
	if ( p == -1 ) {
		[self setup];
	}
	if (stopIndex >= [tokens count]) {
		stopIndex = [tokens count] - 1;
	}
	NSMutableArray *filteredTokens = [NSMutableArray arrayWithCapacity:100];
	unsigned int i=0;
	for (i = startIndex; i<=stopIndex; i++) {
		id<ANTLRToken> token = [tokens objectAtIndex:i];
		if (aBitSet == nil || [aBitSet member:[token getType]]) {
			[filteredTokens addObject:token];
            [token retain];
		}
	}
	if ([filteredTokens count]) {
		return filteredTokens;
	} else {
		[filteredTokens release];
		return nil;
	}
}

- (NSArray *) tokensInRange:(NSRange)aRange withTypes:(NSArray *)tokenTypes
{
	ANTLRBitSet *bits = [[ANTLRBitSet alloc] initWithArrayOfBits:tokenTypes];
	NSArray *returnTokens = [[self tokensInRange:aRange inBitSet:bits] retain];
	[bits release];
	return returnTokens;
}

- (NSArray *) tokensInRange:(NSRange)aRange withType:(NSInteger)tokenType
{
	ANTLRBitSet *bits = [[ANTLRBitSet alloc] init];
	[bits add:tokenType];
	NSArray *returnTokens = [[self tokensInRange:aRange inBitSet:bits] retain];
	[bits release];
	return returnTokens;
}

- (id<ANTLRToken>) getToken:(NSInteger)i
{
	return [tokens objectAtIndex:i];
}

- (NSInteger) size
{
	return [tokens count];
}

- (NSInteger) getIndex
{
	return p;
}

- (void) rewind
{
	[self seek:lastMarker];
}

- (void) rewind:(NSInteger)marker
{
	[self seek:marker];
}

- (void) seek:(NSInteger)index
{
	p = index;
}
#pragma mark toString routines

- (NSString *) toString
{
	if ( p == -1 ) {
		[self setup];
	}
	return [self toStringFromStart:0 ToEnd:[tokens count]];
}

- (NSString *) toStringFromStart:(NSInteger)startIdx ToEnd:(NSInteger) stopIdx
{
    NSString *stringBuffer;
    id<ANTLRToken> t;

    if ( startIdx < 0 || stopIdx < 0 ) {
        return nil;
    }
    if ( p == -1 ) {
        [self setup];
    }
    if ( stopIdx >= [tokens count] ) {
        stopIdx = [tokens count]-1;
    }
    stringBuffer = [NSString string];
    for (int i = startIdx; i <= stopIdx; i++) {
        t = (id<ANTLRToken>)[tokens objectAtIndex:i];
        [stringBuffer stringByAppendingString:[t getText]];
    }
    return stringBuffer;
}

- (NSString *) toStringFromToken:(id<ANTLRToken>)startToken ToToken:(id<ANTLRToken>)stopToken
{
	if (startToken && stopToken) {
		int startIdx = [startToken getTokenIndex];
		int stopIdx = [stopToken getTokenIndex];
		return [self toStringFromStart:startIdx ToEnd:stopIdx];
	}
	return nil;
}

- (id) copyWithZone:(NSZone *)aZone
{
    ANTLRCommonTokenStream *copy;
	
    //    copy = [[[self class] allocWithZone:aZone] init];
    copy = [super copyWithZone:aZone]; // allocation occurs in ANTLRBaseTree
    if ( self.channelOverride )
        copy.channelOverride = [channelOverride copyWithZone:aZone];
    copy.channel = channel;
    return copy;
}

- (NSInteger)getChannel
{
    return channel;
}

- (void)setChannel:(NSInteger)aChannel
{
    channel = aChannel;
}

- (NSMutableDictionary *)getChannelOverride
{
    return channelOverride;
}

- (void)setChannelOverride:(NSMutableDictionary *)anOverride
{
    channelOverride = anOverride;
}

@end
