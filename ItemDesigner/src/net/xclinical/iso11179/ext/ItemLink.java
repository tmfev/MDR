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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="ITEM_LINK")
public class ItemLink {

	public static final String TAG_HISTORY = "history";

	public static final String TAG_FAVOURITES = "favourites";

	public static final String TAG_UPDATE = "update";
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	private String target;

	private Date created;

	@ManyToOne(targetEntity=User.class)
	private User user;
	
	private String tag;
	
	public ItemLink() {
	}

	public ItemLink(HasKey target) {
		this.target = target.getKey().toString();
		this.created = new Date();
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}
	
	public Object getTargetEntity() {
		return PMF.find(Key.parse(target));
	}
	
	public Date getCreated() {
		return created;
	}
}
