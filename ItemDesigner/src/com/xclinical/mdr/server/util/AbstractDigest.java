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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author michael@mictale.com
 */
public abstract class AbstractDigest {

	private final MessageDigest digest;
	
	private static final String ENCODING = "UTF-8";

	/**
	 * Initializes a new digest.
	 * 
	 * @throws RuntimeException when the digest algorithm was not found.
	 */
	public AbstractDigest(String algorithm) {
		try {
			digest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a single byte to the digest.
	 *  
	 * @param data is the byte to add.
	 */
	public void update(byte data) {
		digest.update(data);
	}
	
	/**
	 * Adds a byte array to the digest.
	 * 
	 * @param data is the byte array to add.
	 */
	public void update(byte[] data) {
		digest.update(data);
	}

	/**
	 * Updates the current digest with a slice of a byte array.
	 * 
	 * @param data is the full byte array.
	 * @param offset contains the number of bytes to skip.
	 * @param length contains the number of bytes to use.
	 */
	public void update(byte[] data, int offset, int length) {
		digest.update(data, offset, length);
	}

	/**
	 * Adds a string to the current digest.
	 * @param data is the string to add.
	 */
	public void update(String data) {
		byte[] bytes;
		try {
			bytes = data.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		digest.update(bytes);
	}

	/**
	 * Retrieves the digest string.
	 * @return is the digest string.
	 */
	public String digest() {
		byte[] hash = digest.digest();
		return Strings.toHexString(hash);
	}	
}
