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


/**
 * Builds MD5 hash codes.
 *
 * To make use of this class, create an instance, fill it with calls
 * to the various {@link #update} methods and finally call {@link #digest()}
 * to retrieve the formatted MD5 string.
 * 
 * @author michael@mictale.com
 */
public final class MD5 extends AbstractDigest {

	/**
	 * Initializes a new digest.
	 * 
	 * @throws RuntimeException when the digest algorithm was not found.
	 */
	public MD5() {
		super("MD5");
	}
}
