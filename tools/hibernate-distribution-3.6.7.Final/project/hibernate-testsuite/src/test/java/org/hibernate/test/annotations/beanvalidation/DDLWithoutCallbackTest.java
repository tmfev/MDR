/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
 * third-party contributors as indicated by either @author tags or express
 * copyright attribution statements applied by the authors.  All
 * third-party contributions are distributed under license by Red Hat Inc.
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

package org.hibernate.test.annotations.beanvalidation;

import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.test.annotations.TestCase;
import org.hibernate.testing.junit.DialectChecks;
import org.hibernate.testing.junit.RequiresDialectFeature;

/**
 * @author Vladimir Klyushnikov
 * @author Hardy Ferentschik
 */
public class DDLWithoutCallbackTest extends TestCase {
	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testListeners() {
		CupHolder ch = new CupHolder();
		ch.setRadius( new BigDecimal( "12" ) );
		assertDatabaseConstraintViolationThrown( ch );
	}

	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testMinAndMaxChecksGetApplied() {
		MinMax minMax = new MinMax( 1 );
		assertDatabaseConstraintViolationThrown( minMax );

		minMax = new MinMax( 11 );
		assertDatabaseConstraintViolationThrown( minMax );

		minMax = new MinMax( 5 );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.persist( minMax );
		s.flush();
		tx.rollback();
		s.close();
	}

	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testRangeChecksGetApplied() {
		Range range = new Range( 1 );
		assertDatabaseConstraintViolationThrown( range );

		range = new Range( 11 );
		assertDatabaseConstraintViolationThrown( range );

		range = new Range( 5 );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.persist( range );
		s.flush();
		tx.rollback();
		s.close();
	}

	public void testDDLEnabled() {
		PersistentClass classMapping = getCfg().getClassMapping( Address.class.getName() );
		Column countryColumn = (Column) classMapping.getProperty( "country" ).getColumnIterator().next();
		assertFalse( "DDL constraints are not applied", countryColumn.isNullable() );
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( "javax.persistence.validation.mode", "ddl" );
	}

	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Address.class,
				CupHolder.class,
				MinMax.class,
				Range.class
		};
	}

	private void assertDatabaseConstraintViolationThrown(Object o) {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		try {
			s.persist( o );
			s.flush();
			fail( "expecting SQL constraint violation" );
		}
		catch ( ConstraintViolationException e ) {
			fail( "invalid object should not be validated" );
		}
		catch ( org.hibernate.exception.ConstraintViolationException e ) {
			if ( getDialect().supportsColumnCheck() ) {
				// expected
			}
			else {
				fail( "Unexpected SQL constraint violation [" + e.getConstraintName() + "] : " + e.getSQLException() );
			}
		}
		tx.rollback();
		s.close();
	}
}
