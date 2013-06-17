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

import java.math.BigInteger;
import java.util.concurrent.Callable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.xclinical.mdr.server.PMF;

/**
 * @author ms@xclinical.com
 */
@Entity
@Table(name="SESSION")
public class Session {

	private static final BigInteger PRIME = BigInteger.valueOf(72057594037927909L);

	private static final BigInteger MAX = BigInteger.valueOf(0xffffffffffffL);
		
	private static final ThreadLocal<Session> CURRENT_SESSION = new ThreadLocal<Session>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	private String publicId;

	@ManyToOne
	private User subject;
	
	public Long getId() {
		return id;
	}
	
	public String getPublicId() {
		return publicId;
	}
	
	public Session() {
	}

	private static String scramble(long id) {
		BigInteger i = BigInteger.valueOf(id);
		i = i.multiply(PRIME);
		i = i.mod(MAX);
		return i.toString(16);
	}
		
	public User getSubject() {
		return subject;
	}
	
	public void setSubject(User subject) {
		this.subject = subject;
	}
	
	public static Session findById(String id) {
		if (id == null) throw new NullPointerException();
		
		EntityManager em = PMF.get();
		Query query = em.createQuery("select s from Session s where publicId = ?");
		query.setParameter(1, id);
		
		try {
			Session session = (Session)query.getSingleResult();
			return session;
		}
		catch(NoResultException e) {
			throw new InvalidSessionException("The session " + id + " was not found");
		}
	}
	
	public static Session newSession(String email) {
		Session session = new Session();
		PMF.get().persist(session);
		session.publicId = scramble(session.getId());
		
		User subject;
		try {
			subject = User.findByEmail(email);
		}
		catch(NoResultException e) {
			subject = new User();
			subject.setEmail(email);
			PMF.get().persist(subject);
		}
		
		session.setSubject(subject);
		
		return session;
	}
	
	public static <T> T runInSession(ServletRequest req, Callable<T> callback) throws Exception {
		String sid = ((HttpServletRequest) req).getHeader("session");
		
		final Session session;
		if (sid != null) {
			session = Session.findById(sid);
			if (CURRENT_SESSION.get() != null) {
				throw new IllegalStateException();
			}
		}
		else {
			session = null;
		}
		
		try {
			CURRENT_SESSION.set(session);
			
			return callback.call();
		}
		finally {
			CURRENT_SESSION.set(null);
		}
	}

	public static <T> T runInSession(String sid, Callable<T> callback) throws Exception {		
		final Session session;
		if (sid != null) {
			session = Session.findById(sid);
			if (CURRENT_SESSION.get() != null) {
				throw new IllegalStateException();
			}
		}
		else {
			session = null;
		}
		
		try {
			CURRENT_SESSION.set(session);
			
			return callback.call();
		}
		finally {
			CURRENT_SESSION.set(null);
		}
	}
	
	public static Session get() {
		Session session = CURRENT_SESSION.get();
		if (session == null) {
			throw new InvalidSessionException("No session");
		}
		return session;
	}	
}
