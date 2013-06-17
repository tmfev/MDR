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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

public class Rating implements Visitable {

	private static final Logger log = Logger.get(Rating.class);
	
	private Key target;

	private float averageRating;

	private int votes;
	
	private Key rater;

	private int value;
		
	public Rating() {
	}

	public Rating(HasKey target, float averageRating, int votes, HasKey rater, int value) {
		this.target = target.getKey();
		this.averageRating = averageRating;
		this.votes = votes;
		this.rater = rater.getKey();
		this.value = value;
	}

	public Key getTarget() {
		return target;
	}

	public float getAverageValue() {
		return averageRating;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public Key getRater() {
		return rater;
	}
	
	
	public void setRater(Key rater) {
		this.rater = rater;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setTarget(Key target) {
		this.target = target;
	}
	
	public static Rating rate(Rating rating) {
		EntityManager em = PMF.get();
		User me = Session.get().getSubject();
		
		Vote vote = (Vote)em.find(Vote.class, new VoteId(me.getKey().toString(), rating.target.toString()));
		if (vote == null) {
			vote = new Vote(me.getKey(), rating.target, rating.value);
			PMF.get().persist(vote);
		}
		
		vote.setValue(rating.value);
		
		em.flush();
		
		return getAverageRating(vote, rating.target);
	}

	public static Rating getAverageRating(HasKey target) {
		log.debug("Retrieving average rating of " + target.getKey());
		User me = Session.get().getSubject();
		EntityManager em = PMF.get();
		Vote myVote = (Vote)em.find(Vote.class, new VoteId(me.getKey().toString(), target.getKey().toString()));
		return getAverageRating(myVote, target);
	}

	public static Rating getAverageRating(Vote vote, HasKey target) {
		User me = Session.get().getSubject();
		
		EntityManager em = PMF.get();
		
		Query rating = em.createQuery("select avg(value), count(value) from Vote v where target = ?");
		rating.setParameter(1, target.getKey().toString());
		
		Object[] result = (Object[])rating.getSingleResult();
		
		if (result[0] == null) {
			return new Rating(target, 0f, 0, me, 0);			
		}
		else {
			return new Rating(target, ((Double)result[0]).floatValue(), ((Long)result[1]).intValue(), me, vote == null ? 0 : vote.getValue());
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}	
}
