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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import net.xclinical.iso11179.Item;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.MD5;
import com.xclinical.mdr.server.util.SHA1;
import com.xclinical.mdr.server.util.Strings;

@Entity
@Table(name="USR")
@NamedQueries({
	@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.User.ALL, query="select u from User u"),
	@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.User.FAVOURITES, query="select new com.xclinical.mdr.repository.Key(l.target) from ItemLink l where l.user = ? and tag='favourites' order by l.created desc"),
	@NamedQuery(name=com.xclinical.mdr.client.iso11179.model.User.HISTORY, query="select new com.xclinical.mdr.repository.Key(l.target) from ItemLink l where l.user = ? and tag='history' order by l.created desc limit 100")
})
public class User extends Item {

	private String identifier;
	
	private String email;
	
	private String fullName;
	
	private String passwordHash;

	@OneToMany(targetEntity=ItemLink.class, mappedBy="user")
	private List<ItemLink> links = new ArrayList<ItemLink>();

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.User.URN);		
	}
	
	private void addLink0(HasKey target, String tag) {
		ItemLink link = new ItemLink(target);
		link.setUser(this);
		link.setTag(tag);
		PMF.get().persist(link);
		links.add(link);
	}
	
	public List<ItemLink> getLinks() {
		return links;
	}

	@SuppressWarnings("unchecked")
	public List<Item> getFavourites() {
		Query q = PMF.get().createNamedQuery(com.xclinical.mdr.client.iso11179.model.User.FAVOURITES);
		q.setParameter(1, this);
		return q.getResultList();
	}
	
	public void clearFavourites() {
		PMF.get().createQuery("delete from ItemLink where user=? and tag=?").setParameter(1, this).setParameter(2, ItemLink.TAG_FAVOURITES).executeUpdate();
	}

	public void clearHistory() {
		PMF.get().createQuery("delete from ItemLink where user=? and tag=?").setParameter(1, this).setParameter(2, ItemLink.TAG_HISTORY).executeUpdate();
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public boolean hasPassword() {
		return passwordHash != null;
	}
	
	public void setPassword(String password) {
		SecureRandom rand = new SecureRandom();
		byte[] salt = new byte[64];
		rand.nextBytes(salt);
		String saltString = Strings.toHexString(salt);
		SHA1 hash = new SHA1();
		hash.update(saltString);
		hash.update(password);

		final String newHash = "salted_sha1:" + saltString + ":" + hash.digest();
		if (!newHash.equals(passwordHash)) {
			this.passwordHash = newHash;
		}
	}

	public boolean checkPassword(String password) {
		if (passwordHash == null)
			return false;
		String[] parts = passwordHash.split(":");
		if ("salted_sha1".equals(parts[0])) {
			SHA1 hash = new SHA1();
			hash.update(parts[1]);
			hash.update(password);
			return hash.digest().equals(parts[2]);
		}
		else if ("htdigest".equals(parts[0])) {
			MD5 hash = new MD5();
			hash.update(parts[1]);
			hash.update(":");
			hash.update(parts[2]);
			hash.update(":");
			hash.update(password);
			return hash.digest().equals(parts[3]);
		}
		else {
			return false;
		}
	}

	public void changePassword(String password, String newPassword) {
		if (!checkPassword(password)) {
			throw new IllegalArgumentException("Bad password");				
		}
		
		setPassword(newPassword);
	}

	public void setHtAccess(String user, String realm, String passwordHash) {
		this.passwordHash = "htdigest:" + user + ":" + realm + ":" + passwordHash;
	}
	
	public static User findByEmail(String email) {
		EntityManager em = PMF.get();
		Query query = em.createQuery("select s from User s where upper(email) = ?");
		query.setParameter(1, email.toUpperCase());
		User subject = (User)query.getSingleResult();
		return subject;
	}
	
	public static void addLink(Key key, String tag) {
		User me = Session.get().getSubject();
		me.addLink0(key, tag);
		PMF.get().persist(me);		
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	
}
