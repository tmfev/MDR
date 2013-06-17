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
package com.xclinical.mdr.server;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import net.xclinical.iso11179.TypeNotFoundException;
import net.xclinical.iso11179.Types;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.repository.RepositoryException;
import com.xclinical.mdr.repository.RepositoryStoreException;
import com.xclinical.mdr.server.util.Logger;

public final class PMF {
	private static final Logger LOG = Logger.get(PMF.class);
	
	public static final String PRODUCTION_NAME = "prod";

	public static final String TEST_NAME = "test";

	public static final String INDEX_NAME = "index";
	
    private static final ThreadLocal<EntityManager> emInstance = new ThreadLocal<EntityManager>();

    private static Map<String, EntityManagerFactory> FACTORIES = new HashMap<String, EntityManagerFactory>();
    
    private PMF() {}

    public static EntityManagerFactory getFactory(String name) {
    	EntityManagerFactory factory = FACTORIES.get(name);
    	if (factory == null) {
    		LOG.info("Creating entity manager");
    		
    		// <property name="javax.persistence.jdbc.url" value="jdbc:derby:~/mdr/data;create=true" />

        	final Map<String, String> props = new HashMap<String, String>();
        	        	
        	props.put("javax.persistence.jdbc.url", "jdbc:derby:" + MDRServletConfig.getBaseFolder().getAbsolutePath() + "/data;create=true;upgrade=true");
    		
    		factory = Persistence.createEntityManagerFactory(name, props);
    		FACTORIES.put(name, factory);
    	}
    	
    	return factory;
    }
    
    public static <T> T runInServlet(Callable<T> callback) throws ServletException {
    	try {
    		return run(callback);
    	}
    	catch(InvocationTargetException e) {
    		throw new ServletException(e.getCause());
    	}
    }

    public static <T> T run(Callable<T> callback) throws InvocationTargetException {
    	return run(PRODUCTION_NAME, callback);
    }

    public static <T> T runTest(Callable<T> callback) throws InvocationTargetException {
    	return run(TEST_NAME, callback);
    }
    
    public static <T> T run(String unitName, Callable<T> callback) throws InvocationTargetException {
    	final EntityManager em = getFactory(unitName).createEntityManager();
    	final EntityTransaction tx = em.getTransaction();
    	
    	try {
    		tx.begin();
    		
    		emInstance.set(em);
    		
    		T t = callback.call();

    		tx.commit();
    		
			return t;
    	}
    	catch(Exception e) {
    		throw new InvocationTargetException(e);
    	}
    	finally {
    		if (tx.isActive()) {
    			tx.rollback();
    		}
    		
    		em.close();
    		emInstance.set(null);
    	}
    }

    public static <T> T run(EntityManager em, Callable<T> callback) throws InvocationTargetException {
    	final EntityTransaction tx = em.getTransaction();
    	
    	try {
    		tx.begin();
    		
    		emInstance.set(em);
    		
    		T t = callback.call();

    		tx.commit();
    		
			return t;
    	}
    	catch(Exception e) {
    		throw new InvocationTargetException(e);
    	}
    	finally {
    		if (tx.isActive()) {
    			tx.rollback();
    		}
    		
    		em.close();
    		emInstance.set(null);
    	}
    }
    
    public static EntityManager get() {
    	EntityManager em = emInstance.get();
    	if (em == null) {
    		throw new RepositoryException("get() called in a bad context");
    	}
    	return em;
    }
    
    public static Object find(final Key key) {
    	if (key == null) throw new NullPointerException();
    	
    	EntityManager em = get();
    	
		Class<?> entityType;
		try {
			entityType = Types.getType(key.getName());
		} catch (TypeNotFoundException e) {
			throw new RepositoryStoreException(e);
		}

		
		if (!key.hasValue()) {
			throw new RepositoryStoreException("Cannot retrieve unbound entity");
		}
		
		Object entity = em.find(entityType, key.getValue());
		if (entity == null) {
			throw new RepositoryStoreException("The element " + key + " was not found");
		}
		return entity;			
    }
    
    public static boolean executeSql(String sql) throws HibernateException, SQLException {
		SessionFactoryImplementor impl = (SessionFactoryImplementor) PMF.get().unwrap(Session.class).getSessionFactory();
		Connection connection = impl.getConnectionProvider().getConnection();
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		try {
			return stmt.execute();
		}
		finally {
			stmt.close();
		}
    }
    
    public static String executeSelect(String sql) throws HibernateException, SQLException {
		SessionFactoryImplementor impl = (SessionFactoryImplementor) PMF.get().unwrap(Session.class).getSessionFactory();
		Connection connection = impl.getConnectionProvider().getConnection();
		
		PreparedStatement stmt = connection.prepareStatement(sql);
		try {
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getString(1);
		}
		finally {
			stmt.close();
		}
    }

    public static String getColumnType(String tableName, String columnName) throws HibernateException, SQLException {
		return PMF.executeSelect("select c.columndatatype from sys.syscolumns c where referenceid in (select tableid from sys.systables t,sys.sysschemas s where tablename='" + tableName + "' and columnname='" + columnName + "' and s.schemaid=t.schemaid and schemaname='APP')");					    	
    }
}
