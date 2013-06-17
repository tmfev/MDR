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

import java.io.Serializable;

public class VoteId implements Serializable {

	private static final long serialVersionUID = -9100840321198409495L;

	private String rater;
	
	private String target;

	public VoteId() {
	}

	public VoteId(String rater, String target) {
		this.rater = rater;
		this.target = target;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VoteId) {
			VoteId other = (VoteId)obj;
			return other.rater.equals(rater) && other.target.equals(target);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int)(target.hashCode() ^ rater.hashCode());
	}
}
