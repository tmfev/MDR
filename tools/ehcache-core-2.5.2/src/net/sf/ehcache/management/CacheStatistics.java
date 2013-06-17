/**
 *  Copyright 2003-2010 Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.management;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Statistics;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * A JMX CacheStatistics decorator for an ehcache Statistics class.
 * <p/>
 * An immutable Cache statistics implementation}
 * <p/>
 * This is like a value object, with the added ability to clear cache statistics on the cache.
 * That ability does not survive any Serialization of this class. On deserialization the cache
 * can be considered disconnected.
 * <p/>
 * The accuracy of these statistics are determined by the value of {#getStatisticsAccuracy()}
 * at the time the statistic was computed. This can be changed by setting {@link net.sf.ehcache.Cache#setStatisticsAccuracy}.
 * <p/>
 * Because this class maintains a reference to an Ehcache, any references held to this class will precent the Ehcache
 * from getting garbage collected.
 *
 * @author Greg Luck
 * @version $Id: CacheStatistics.java 5335 2012-03-02 20:23:01Z cdennis $
 * @since 1.3
 */
public class CacheStatistics implements CacheStatisticsMBean, Serializable {

    private static final long serialVersionUID = 8085302752781762030L;

    private final transient Ehcache ehcache;
    private final ObjectName objectName;
    private final long ttlInMillis;
    
    private Statistics statistics;
    private long lastUpdated;

    /**
     * Constructs an object from an ehcache statistics object
     *
     * @param ehcache the backing ehcache
     * @param ttl statistics ttl value
     * @param unit statistics ttl unit
     */
    public CacheStatistics(Ehcache ehcache, long ttl, TimeUnit unit) {
        this.ttlInMillis = unit.toMillis(ttl);
        this.ehcache = ehcache;
        this.objectName = createObjectName(ehcache.getCacheManager().getName(), ehcache.getName());
    }

    /**
     * Constructs an object from an ehcache statistics object using the default statistics ttl.
     *
     * @param ehcache the backing ehcache
     */
    public CacheStatistics(Ehcache ehcache) {
        this(ehcache, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates an object name using the scheme "net.sf.ehcache:type=CacheStatistics,CacheManager=<cacheManagerName>,name=<cacheName>"
     */
    static ObjectName createObjectName(String cacheManagerName, String cacheName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheStatistics,CacheManager=" + cacheManagerName + ",name="
                    + EhcacheHibernateMbeanNames.mbeanSafe(cacheName));
        } catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    /**
     * Accurately measuring statistics can be expensive. Returns the current accuracy setting used
     * in the creation of these statistics.
     *
     * @return one of {@link Statistics#STATISTICS_ACCURACY_BEST_EFFORT}, {@link Statistics#STATISTICS_ACCURACY_GUARANTEED}, {@link Statistics#STATISTICS_ACCURACY_NONE}
     */
    public int getStatisticsAccuracy() {
        updateIfNeeded();
        return statistics.getStatisticsAccuracy();
    }

    private void updateIfNeeded() {
        long statsAge = System.currentTimeMillis() - lastUpdated;
        if (statsAge < 0 || statsAge > ttlInMillis) {
            statistics = ehcache.getStatistics();
            lastUpdated = System.currentTimeMillis();
        }
    }

    /**
     * Accurately measuring statistics can be expensive. Returns the current accuracy setting.
     *
     * @return a human readable description of the accuracy setting. One of "None", "Best Effort" or "Guaranteed".
     */
    public String getStatisticsAccuracyDescription() {
        updateIfNeeded();
        return statistics.getStatisticsAccuracyDescription();
    }

    /**
     * @return the name of the Ehcache, or null is there no associated cache
     */
    public String getAssociatedCacheName() {
        if (statistics == null) {
            return null;
        } else {
            return statistics.getAssociatedCacheName();
        }
    }

    /**
     * Clears the statistic counters to 0 for the associated Cache.
     */
    public void clearStatistics() {
        statistics.clearStatistics();
    }

    /**
     * The number of times a requested item was found in the cache.
     * <p/>
     * Warning. This statistic is recorded as a long. If the statistic is large than Integer#MAX_VALUE
     * precision will be lost.
     *
     * @return the number of times a requested item was found in the cache
     */
    public long getCacheHits() {
        updateIfNeeded();
        return statistics.getCacheHits();
    }

    /**
     * Number of times a requested item was found in the Memory Store.
     *
     * @return the number of times a requested item was found in memory
     */
    public long getInMemoryHits() {
        updateIfNeeded();
        return statistics.getInMemoryHits();
    }

    /**
     * Number of times a requested item was found in the off-heap store.
     *
     * @return the number of times a requested item was found in off-heap
     */
    public long getOffHeapHits() {
        updateIfNeeded();
        return statistics.getOffHeapHits();
    }

    /**
     * Number of times a requested item was found in the Disk Store.
     *
     * @return the number of times a requested item was found on Disk, or 0 if there is no disk storage configured.
     */
    public long getOnDiskHits() {
        updateIfNeeded();
        return statistics.getOnDiskHits();
    }

    /**
     * Warning. This statistic is recorded as a long. If the statistic is large than Integer#MAX_VALUE
     * precision will be lost.
     *
     * @return the number of times a requested element was not found in the cache
     */
    public long getCacheMisses() {
        updateIfNeeded();
        return statistics.getCacheMisses();
    }

    /** {@inheritDoc} */
    public long getInMemoryMisses() {
        updateIfNeeded();
        return statistics.getInMemoryMisses();
    }

    /** {@inheritDoc} */
    public long getOffHeapMisses() {
        updateIfNeeded();
        return statistics.getOffHeapMisses();
    }

    /** {@inheritDoc} */
    public long getOnDiskMisses() {
        updateIfNeeded();
        return statistics.getOnDiskMisses();
    }

    /**
     * Gets the number of elements stored in the cache. Caclulating this can be expensive. Accordingly,
     * this method will return three different values, depending on the statistics accuracy setting.
     * <h3>Best Effort Size</h3>
     * This result is returned when the statistics accuracy setting is {@link net.sf.ehcache.Statistics#STATISTICS_ACCURACY_BEST_EFFORT}.
     * <p/>
     * The size is the number of {@link net.sf.ehcache.Element}s in the {@link net.sf.ehcache.store.MemoryStore} plus
     * the number of {@link net.sf.ehcache.Element}s in the {@link net.sf.ehcache.store.disk.DiskStore}.
     * <p/>
     * This number is the actual number of elements, including expired elements that have
     * not been removed. Any duplicates between stores are accounted for.
     * <p/>
     * Expired elements are removed from the the memory store when
     * getting an expired element, or when attempting to spool an expired element to
     * disk.
     * <p/>
     * Expired elements are removed from the disk store when getting an expired element,
     * or when the expiry thread runs, which is once every five minutes.
     * <p/>
     * <h3>Guaranteed Accuracy Size</h3>
     * This result is returned when the statistics accuracy setting is {@link net.sf.ehcache.Statistics#STATISTICS_ACCURACY_GUARANTEED}.
     * <p/>
     * This method accounts for elements which might be expired or duplicated between stores. It take approximately
     * 200ms per 1000 elements to execute.
     * <h3>Fast but non-accurate Size</h3>
     * This result is returned when the statistics accuracy setting is {@link Statistics#STATISTICS_ACCURACY_NONE}.
     * <p/>
     * The number given may contain expired elements. In addition if the DiskStore is used it may contain some double
     * counting of elements. It takes 6ms for 1000 elements to execute. Time to execute is O(log n). 50,000 elements take
     * 36ms.
     *
     * @return the number of elements in the ehcache, with a varying degree of accuracy, depending on accuracy setting.
     */
    public long getObjectCount() {
        updateIfNeeded();
        return statistics.getObjectCount();
    }

    /**
     * Gets the size of the write-behind queue, if any.
     * The value is for all local buckets
     * @return Elements waiting to be processed by the write behind writer. -1 if no write-behind
     */
    public long getWriterQueueLength() {
        updateIfNeeded();
        return statistics.getWriterQueueSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getWriterMaxQueueSize() {
        return ehcache.getCacheConfiguration().getCacheWriterConfiguration().getWriteBehindMaxQueueSize();
    }

    /**
     * Gets the number of objects in the MemoryStore
     * @return the MemoryStore size which is always a count unadjusted for duplicates or expiries
     */
    public long getMemoryStoreObjectCount() {
        updateIfNeeded();
        return statistics.getMemoryStoreObjectCount();
    }

    /**
     * {@inheritDoc}
     */
    public long getOffHeapStoreObjectCount() {
        updateIfNeeded();
        return statistics.getOffHeapStoreObjectCount();
    }

    /**
     * Gets the number of objects in the DiskStore
     * @return the DiskStore size which is always a count unadjusted for duplicates or expiries
     */
    public long getDiskStoreObjectCount() {
        updateIfNeeded();
        return statistics.getDiskStoreObjectCount();
    }


    /**
     * @return the object name for this MBean
     */
    ObjectName getObjectName() {
        return objectName;
    }

    /**
     * Return the backing cache.
     * @return the backing cache, if one is connected. On Serialization
     * the transient Ehcache reference is dropped.
     */
    public Ehcache getEhcache() {
        return ehcache;
    }

    private static double getPercentage(long number, long total) {
        if (total == 0) {
            return 0.0;
        } else {
            return number / (double)total;
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getCacheHitPercentage() {
        updateIfNeeded();
        long hits = statistics.getCacheHits();
        long misses = statistics.getCacheMisses();

        long total = hits + misses;
        return getPercentage(hits, total);
    }

    /**
     * {@inheritDoc}
     */
    public double getCacheMissPercentage() {
        updateIfNeeded();
        long hits = statistics.getCacheHits();
        long misses = statistics.getCacheMisses();

        long total = hits + misses;
        return getPercentage(misses, total);
    }

    /**
     * {@inheritDoc}
     */
    public double getInMemoryHitPercentage() {
        updateIfNeeded();
        long hits = statistics.getInMemoryHits();
        long misses = statistics.getInMemoryMisses();

        long total = hits + misses;
        return getPercentage(hits, total);
    }

    /**
     * {@inheritDoc}
     */
    public double getOffHeapHitPercentage() {
        updateIfNeeded();
        long hits = statistics.getOffHeapHits();
        long misses = statistics.getOffHeapMisses();

        long total = hits + misses;
        return getPercentage(hits, total);
    }

    /**
     * {@inheritDoc}
     */
    public double getOnDiskHitPercentage() {
        updateIfNeeded();
        long hits = statistics.getOnDiskHits();
        long misses = statistics.getOnDiskMisses();

        long total = hits + misses;
        return getPercentage(hits, total);
    }
}
