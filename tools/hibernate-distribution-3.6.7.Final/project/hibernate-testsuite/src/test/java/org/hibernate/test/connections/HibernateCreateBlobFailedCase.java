/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.connections;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.testing.junit.functional.FunctionalTestCase;

import java.sql.SQLException;

/**
 * Test originally developed to verify and fix HHH-5550
 *
 * @author Steve Ebersole
 */
public class HibernateCreateBlobFailedCase extends FunctionalTestCase {
	public HibernateCreateBlobFailedCase(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[] { "connections/Silly.hbm.xml" };
	}

	@Override
	public void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread" );
	}

	public void testLobCreation() throws SQLException {
		Session session = sfi().getCurrentSession();
		session.beginTransaction();
		Hibernate.getLobCreator( session ).createBlob( new byte[] {} );
		Hibernate.createBlob( new byte[] {}, session );
		Hibernate.getLobCreator( session ).createClob( "Steve" );
		Hibernate.createClob( "Steve", session );
		session.getTransaction().commit();
		assertFalse( session.isOpen() );
	}
}