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
package com.xclinical.mdr.server.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


/**
 * Provides string utilities.
 * 
 * @author michael@mictale.com
 */
public final class Strings {

	public static final char NEWLINE = '\n';

	public static final String NEWLINE_STRING = String.valueOf(NEWLINE);
	
	private static final int[] EXPONENTS = {
		1, 
		10, 
		100, 
		1000, 
		10000, 
		100000, 
		1000000, 
		10000000,
		100000000,
		1000000000
		};

	private static final char[] CODES = new char[] {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 
		'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	
	private static final String[] CHARACTERS = new String[] {
		"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
		"alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliett", "kilo", "lima", "mike",
		"november", "oscar", "papa", "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu"
	};

	/**
	 * Simple formatting for floats.
	 * 
	 * @param value is the value to format.
	 * @param intDigits are the number of integer digits.
	 * @param fracDigits are the number of fractional digits (must be <=9).
	 * @return the formatted float value.
	 */
	public static String formatNumber(double value, int intDigits, int fracDigits) {
		char[] str = new char[32];
		long v = Math.round(Math.abs(value) * EXPONENTS[fracDigits]);
		
		int ind = str.length - 1;
		
		if (fracDigits > 0) {
			for (int i = 0; i < fracDigits; i++) {
				str[ind--] = Character.forDigit((int)(v % 10), 10);				
				v /= 10;
			}
			str[ind--] = '.';
		}

		for (int i = 0; v > 0 || i < intDigits; i++) {
			str[ind--] = Character.forDigit((int)(v % 10), 10);				
			v /= 10;
		}
		
		if (value < 0) {
			str[ind--] = '-';
		}
		
		String result = new String(str, ind + 1, str.length - ind - 1);
		return result;
	}

	public static String implode(String delim, Object[] args) {
		return implode(delim, Arrays.asList(args));
	}
	
	/**
	 * Concatenates all elements in the specified collection.
	 * @param delim is a string placed between elements.
	 * @param args contains the elements.
	 * @return the concatenated string.
	 */
	public static String implode(String delim, Collection<? extends Object> args) {
		StringBuilder sb = new StringBuilder();

		Iterator<? extends Object> i = args.iterator();
		
		if (i.hasNext()) {
			sb.append(String.valueOf(i.next()));
		}
		
		while (i.hasNext()) {
			sb.append(delim);
			sb.append(String.valueOf(i.next()));
		}
		
		return sb.toString();
	}
	
	public static String[] asArray(Object[] arr) {
		String[] oids = new String[arr.length];
		int i = 0;
		for (Object n : arr) {
			oids[i++] = String.valueOf(n);
		}
		return oids;
	}
	
	public static void copy(String src, char[] dest) {
		for (int i = src.length() - 1; i >= 0; i--) {
			dest[i] = src.charAt(i);
		}
	}
	
	public static int countLines(CharSequence s) {
		int lines = 1;
		
		if (s != null) {
			for (int i = s.length() - 1; i > 0; i--) {
				if (s.charAt(i) == '\n') lines++;
			}
		}
		
		return lines;
	}
	
	/**
	 * Converts a byte array into a hexadecimal string representation.
	 *
	 * @param bytes is the byte array to transform.
	 * @return the human readable presentation.
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			int hiNibble = (b >>> 4) & 0x0f;
			int loNibble = b & 0x0f;
			
			sb.append(Character.forDigit(hiNibble, 16));
			sb.append(Character.forDigit(loNibble, 16));
		}
		
		return sb.toString();
	}

	public static String toString(Throwable th) {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		th.printStackTrace(p);
		return w.toString();
	}
	
	/**
	 * Returns a subset of characters from the specified array without performing a copy.
	 * 
	 * @param buffer contains the characters.
	 * @param start is the start index, inclusive.
	 * @param end is the end index, exclusive.
	 * @return the characters.
	 */
	public static CharSequence reference(char[] buffer, int start, int end) {
		return new SubSequence(buffer, start, end);
	}
	
	private static class SubSequence implements CharSequence {
		private final char[] data;
		private final int start;
		private final int end;
		
		public SubSequence(char[] data, int start, int end) {
			this.data = data;
			this.start = start;
			this.end = end;
		}

		@Override
		public char charAt(int position) {
			return data[start + position];
		}

		@Override
		public int length() {
			return end - start;
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return new SubSequence(data, this.start + start, this.start + end);
		}
		
		@Override
		public String toString() {
			return new String(data, start, length());
		}
	}

	public static CharSequence spell(char ch, boolean capitalize) {
		int ind = Arrays.binarySearch(CODES, Character.toLowerCase(ch));
		if (ind < 0) {
			throw new IllegalArgumentException("Don't know how to spell " + ch);
		}
		String s = CHARACTERS[ind];
		
		if (Character.isUpperCase(ch) && capitalize) {
			return s.toUpperCase();
		}
		else {
			return s;
		}
	}
	
	public static CharSequence spell(CharSequence str, boolean capitalize) {
		StringBuilder sb = new StringBuilder();
		
		if (str.length() > 0) {
			for (int i = 0;;) {
				char ch = str.charAt(i);
				sb.append(spell(ch, capitalize));
				if (++i < str.length()) {
					sb.append('-');
				}
				else {
					break;
				}
			}
		}
		
		return sb.toString();
	}
}
