package org.hibernate.testing.cache;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.ReadWriteCache;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.junit.functional.FunctionalTestCase;
import org.hibernate.testing.tm.ConnectionProviderImpl;
import org.hibernate.testing.tm.TransactionManagerLookupImpl;
import org.hibernate.transaction.JDBCTransactionFactory;

import java.util.Map;

/**
 * @author Alex Snaps
 */
public abstract class BaseCacheTestCase extends FunctionalTestCase {
	public BaseCacheTestCase(final String string) {super(string);}

	@Override
	public String getBaseForMappings() {
		return "org/hibernate/testing/";
	}

	public String[] getMappings() {
		return new String[] { "cache/Item.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "" );
		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
		cfg.setProperty( Environment.USE_STRUCTURED_CACHE, "true" );
		configCache(cfg);

		if ( useTransactionManager() ) {
			cfg.setProperty( Environment.CONNECTION_PROVIDER, ConnectionProviderImpl.class.getName() );
			cfg.setProperty( Environment.TRANSACTION_MANAGER_STRATEGY, TransactionManagerLookupImpl.class.getName() );
		}
		else {
			cfg.setProperty( Environment.TRANSACTION_STRATEGY, JDBCTransactionFactory.class.getName() );
		}
	}

	protected abstract void configCache(Configuration cfg);

	/**
	 * For provider-specific configuration, the name of the property key the
	 * provider expects.
	 *
	 * @return The provider-specific config key.
	 */
	protected abstract String getConfigResourceKey();

	/**
	 * For provider-specific configuration, the resource location of that
	 * config resource.
	 *
	 * @return The config resource location.
	 */
	protected abstract String getConfigResourceLocation();

	/**
	 * Should we use a transaction manager for transaction management.
	 *
	 * @return True if we should use a RM; false otherwise.
	 */
	protected abstract boolean useTransactionManager();

	public void testQueryCacheInvalidation() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Item i = new Item();
		i.setName("widget");
		i.setDescription("A really top-quality, full-featured widget.");
		s.persist(i);
		t.commit();
		s.close();

		SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics()
				.getSecondLevelCacheStatistics( Item.class.getName() );

		assertEquals( slcs.getPutCount(), 1 );
		assertEquals( slcs.getElementCountInMemory(), 1 );
		assertEquals( slcs.getEntries().size(), 1 );

		s = openSession();
		t = s.beginTransaction();
		i = (Item) s.get( Item.class, i.getId() );

		assertEquals( slcs.getHitCount(), 1 );
		assertEquals( slcs.getMissCount(), 0 );

		i.setDescription("A bog standard item");

		t.commit();
		s.close();

		assertEquals( slcs.getPutCount(), 2 );

		Object entry = slcs.getEntries().get( i.getId() );
		Map map = getMapFromCachedEntry(entry);
		assertTrue( map.get("description").equals("A bog standard item") );
		assertTrue( map.get("name").equals("widget") );

		// cleanup
		s = openSession();
		t = s.beginTransaction();
		s.delete( i );
		t.commit();
		s.close();
	}

	public void testEmptySecondLevelCacheEntry() throws Exception {
		getSessions().evictEntity( Item.class.getName() );
		Statistics stats = getSessions().getStatistics();
		stats.clear();
		SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics( Item.class.getName() );
        Map cacheEntries = statistics.getEntries();
		assertEquals( 0, cacheEntries.size() );
	}

	public void testStaleWritesLeaveCacheConsistent() {
		Session s = openSession();
		Transaction txn = s.beginTransaction();
		VersionedItem item = new VersionedItem();
		item.setName( "steve" );
		item.setDescription( "steve's item" );
		s.save( item );
		txn.commit();
		s.close();

		Long initialVersion = item.getVersion();

		// manually revert the version property
		item.setVersion( new Long( item.getVersion().longValue() - 1 ) );

		try {
			s = openSession();
			txn = s.beginTransaction();
			s.update( item );
			txn.commit();
			s.close();
			fail( "expected stale write to fail" );
		}
		catch( Throwable expected ) {
			// expected behavior here
			if ( txn != null ) {
				try {
					txn.rollback();
				}
				catch( Throwable ignore ) {
				}
			}
		}
		finally {
			if ( s != null && s.isOpen() ) {
				try {
					s.close();
				}
				catch( Throwable ignore ) {
				}
			}
		}

		// check the version value in the cache...
		SecondLevelCacheStatistics slcs = sfi().getStatistics()
				.getSecondLevelCacheStatistics( VersionedItem.class.getName() );

		Object entry = slcs.getEntries().get( item.getId() );
		Long cachedVersionValue;
		if ( entry instanceof ReadWriteCache.Lock ) {
			//FIXME don't know what to test here
			cachedVersionValue = new Long( ( (ReadWriteCache.Lock) entry).getUnlockTimestamp() );
		} else if(entry.getClass().getName().equals("net.sf.ehcache.hibernate.strategy.AbstractReadWriteEhcacheAccessStrategy$Lock")) {
			//FIXME don't know what to test here, which is the same as issue as above...
	    } else {
			cachedVersionValue = ( Long ) getMapFromCachedEntry(entry).get( "_version" );
			assertEquals( initialVersion.longValue(), cachedVersionValue.longValue() );
		}


		// cleanup
		s = openSession();
		txn = s.beginTransaction();
		item = ( VersionedItem ) s.load( VersionedItem.class, item.getId() );
		s.delete( item );
		txn.commit();
		s.close();

	}

	protected abstract Map getMapFromCachedEntry(Object entry);
}
