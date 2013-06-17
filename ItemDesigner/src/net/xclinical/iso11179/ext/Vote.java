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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * A rating of a single user targeting a single entity.
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="VOTE")
@IdClass(VoteId.class)
public class Vote {

	@Id
	private String rater;
	
	@Id
	private String target;

	private int value;

	public Vote() {
	}

	public Vote(HasKey rater, HasKey target, int value) {
		this.rater = rater.getKey().toString();
		this.target = target.getKey().toString();
		this.value = value;
	}

	public Object getRater() {
		return PMF.find(Key.parse(rater));
	}
	
	public Object getTarget() {
		return PMF.find(Key.parse(target));
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
