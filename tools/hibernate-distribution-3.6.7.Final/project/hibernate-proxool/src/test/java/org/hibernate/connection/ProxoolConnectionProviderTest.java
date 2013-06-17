/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
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
package org.hibernate.connection;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Environment;
import org.logicalcobwebs.proxool.ProxoolFacade;

import junit.framework.TestCase;

/**
 * Test to verify connection pools are closed, and that only the managed one is closed.
 * @author Sanne Grinovero
 */
public class ProxoolConnectionProviderTest extends TestCase {
	
	public void testPoolsClosed() {
		assertDefinedPools(); // zero-length-vararg used as parameter
		
		ProxoolConnectionProvider providerOne = new ProxoolConnectionProvider();
		providerOne.configure( getPoolConfigurarion( "pool-one" ) );
		assertDefinedPools( "pool-one" );
		
		ProxoolConnectionProvider providerTwo = new ProxoolConnectionProvider();
		providerTwo.configure( getPoolConfigurarion( "pool-two" ) );
		assertDefinedPools( "pool-one", "pool-two" );
		
		providerOne.close();
		assertDefinedPools( "pool-two" );
		
		providerTwo.close();
		assertDefinedPools();
	}

	private void assertDefinedPools(String... expectedPoolNames) {
		List<String> aliases = Arrays.asList( ProxoolFacade.getAliases() );
		assertEquals( expectedPoolNames.length,	aliases.size() );
		for (String poolName : expectedPoolNames) {
			assertTrue( "pool named " + poolName + " missing", aliases.contains( poolName ) );
		}
	}

	/**
	 * @param pool name
	 * @return his configuration - see src/tests/resources for matches
	 */
	private Properties getPoolConfigurarion(String poolName) {
		Properties cfg = new Properties();
		cfg.setProperty( Environment.PROXOOL_POOL_ALIAS, poolName );
		cfg.setProperty( Environment.PROXOOL_PROPERTIES, poolName + ".properties" );
		return cfg;
	}
	
}
