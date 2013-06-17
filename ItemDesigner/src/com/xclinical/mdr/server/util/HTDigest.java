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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Processes <code>htdigest</code> files.
 * 
 * @author ms@xclinical.com
 */
public class HTDigest {

	private final File file;
	
	private Map<Key, String> map;

	/**
	 * Parses a specified file.
	 * 
	 * @param file is the file to parse.
	 * @throws IOException if the specified file could not be accessed.
	 */
	public HTDigest(File file) throws IOException {
		this.file = file;
		this.map = load(file);
	}
	
	private static Map<Key, String> load(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		
		try {
			Map<Key, String> map = new HashMap<HTDigest.Key, String>();
			
			String line;
			while ((line = r.readLine()) != null) {
				if (line.length() > 0) { 
					String[] parts = line.split(":");
					map.put(new Key(parts[0], parts[1]), parts[2]);
				}
			}
			return map;
		}
		finally {
			r.close();
		}
	}

	private void flush() throws IOException {
		FileWriter w = new FileWriter(file);
		try {
			for (Map.Entry<Key, String> e : map.entrySet()) {
				w.write(e.getKey().user);
				w.write(':');
				w.write(e.getKey().realm);
				w.write(':');
				w.write(e.getValue());
				w.write('\n');
			}
		}
		finally {
			w.close();
		}
	}
	
	/**
	 * Creates or updates a specific entry.
	 * 
	 * @param user is the user name.
	 * @param realm is the realm.
	 * @param password is the new password.
	 * @throws IOException if the contents could not be written to the backing store.
	 */
	public void put(String user, String realm, String password) throws IOException {
		map.put(new Key(user, realm), password);
		flush();
	}

	/**
	 * Removes a specific entry.
	 * 
	 * @param user is the user name.
	 * @param realm is the realm.
	 * @throws IOException if the contents could not be written to the backing store.
	 */
	public void remove(String user, String realm) throws IOException {		
		map.remove(new Key(user, realm));
		flush();
	}

	public Set<Entry<Key, String>> entrySet() {
		return map.entrySet();
	}
	
	/**
	 * Validates a password.
	 * 
	 * @param user is the user name.
	 * @param realm is the realm.
	 * @param password is the password to check.
	 * @throws InvalidCredentialsException if the password could not be validated.
	 */
	public void check(String user, String realm, String password) throws InvalidCredentialsException {
		String key = map.get(new Key(user, realm));
		if (key == null) {
			throw new InvalidCredentialsException();
		}
		
		MD5 digest = new MD5();
		digest.update(user);
		digest.update(":");
		digest.update(realm);
		digest.update(":");
		digest.update(password);
		
		if (!key.equals(digest.digest())) {
			throw new InvalidCredentialsException();
		}
	}
	
	public static final class Key {
		private final String user;		
		private final String realm;
		
		Key(String user, String realm) {
			if (user == null) throw new NullPointerException();
			if (realm == null) throw new NullPointerException();
			
			this.user = user;
			this.realm = realm;
		}
		
		public String getUser() {
			return user;
		}

		public String getRealm() {
			return realm;
		}
		
		@Override
		public int hashCode() {
			return user.hashCode() ^ realm.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			Key other = (Key)obj;
			return other.user.equals(user) && other.realm.equals(realm);
		}
	}
}
