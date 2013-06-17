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

/**
 * @author Emmanuel Bernard
 */
public class BeanValidationDisabledTest extends TestCase {
	public void testListeners() {
		CupHolder ch = new CupHolder();
		ch.setRadius( new BigDecimal( "12" ) );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		try {
			s.persist( ch );
			s.flush();
		}
		catch ( ConstraintViolationException e ) {
			fail( "invalid object should not be validated" );
		}
		tx.rollback();
		s.close();
	}

	public void testDDLDisabled() {
		PersistentClass classMapping = getCfg().getClassMapping( Address.class.getName() );
		Column countryColumn = (Column) classMapping.getProperty( "country" ).getColumnIterator().next();
		assertTrue( "DDL constraints are applied", countryColumn.isNullable() );
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( "javax.persistence.validation.mode", "none" );
	}

	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Address.class,
				CupHolder.class
		};
	}
}
