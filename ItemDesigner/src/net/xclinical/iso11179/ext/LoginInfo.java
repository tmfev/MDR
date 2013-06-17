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
package net.xclinical.iso11179.ext;


/**
 * Contains information about a user that is logged in.
 * 
 * The server returns an instance of this class when the user successfully logged in.
 * The client stores this instance for later reference.
 * 
 * @author Michael Schollmeyer (ms@xclinical.com)
 */
public class LoginInfo implements Visitable {

	private User user;
	
	private String session;
	
	public LoginInfo() {
	}
	
	public LoginInfo(User user, String session) {
		this.user = user;
		this.session = session;
	}

	public User getUser() {
		return user;
	}
	
	/**
	 * Retrieves the current session.
	 * 
	 * @return the current session.
	 */
	public String getSession() {
		return session;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
}
