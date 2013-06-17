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

package net.sf.ehcache.store;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.ReadWriteLockSync;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolableStore;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.impl.DefaultSizeOfEngine;
import net.sf.ehcache.store.chm.SelectableConcurrentHashMap;
import net.sf.ehcache.store.disk.StoreUpdateException;
import net.sf.ehcache.util.ratestatistics.AtomicRateStatistic;
import net.sf.ehcache.util.ratestatistics.RateStatistic;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * A Store implementation suitable for fast, concurrent in memory stores. The policy is determined by that
 * configured in the cache.
 *
 * @author <a href="mailto:ssuravarapu@users.sourceforge.net">Surya Suravarapu</a>
 * @version $Id: MemoryStore.java 5345 2012-03-05 22:44:29Z alexsnaps $
 */
public class MemoryStore extends AbstractStore implements TierableStore, PoolableStore, CacheConfigurationListener {

    /**
     * This is the default from {@link java.util.concurrent.ConcurrentHashMap}. It should never be used, because we size
     * the map to the max size of the store.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Set optimisation for 100 concurrent threads.
     */
    private static final int CONCURRENCY_LEVEL = 100;

    private static final int MAX_EVICTION_RATIO = 5;

    private static final Logger LOG = LoggerFactory.getLogger(MemoryStore.class.getName());

    private final boolean alwaysPutOnHeap;

    /**
     * The cache this store is associated with.
     */
    private final Ehcache cache;

    /**
     * Map where items are stored by key.
     */
    private final SelectableConcurrentHashMap map;
    private final PoolAccessor poolAccessor;

    private final RateStatistic hitRate = new AtomicRateStatistic(1000, TimeUnit.MILLISECONDS);
    private final RateStatistic missRate = new AtomicRateStatistic(1000, TimeUnit.MILLISECONDS);

    private final boolean cachePinned;
    private final boolean elementPinningEnabled;

    /**
     * The maximum size of the store (0 == no limit)
     */
    private volatile int maximumSize;

    /**
     * status.
     */
    private volatile Status status;

    /**
     * The eviction policy to use
     */
    private volatile Policy policy;

    /**
     * The pool accessor
     */

    private volatile CacheLockProvider lockProvider;
    private final boolean tierPinned;

    /**
     * Constructs things that all MemoryStores have in common.
     *
     * @param cache the cache
     * @param pool the pool tracking the on-heap usage
     * @param doNotifications whether to notify the Cache's EventNotificationService on eviction and expiry
     */
    protected MemoryStore(final Ehcache cache, Pool pool, final boolean doNotifications) {
        status = Status.STATUS_UNINITIALISED;
        this.cache = cache;
        this.maximumSize = (int) cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        this.policy = determineEvictionPolicy(cache);

        this.poolAccessor = pool.createPoolAccessor(this,
            SizeOfPolicyConfiguration.resolveMaxDepth(cache),
            SizeOfPolicyConfiguration.resolveBehavior(cache).equals(SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT));

        this.alwaysPutOnHeap = getAdvancedBooleanConfigProperty("alwaysPutOnHeap", cache.getCacheConfiguration().getName(), false);
        this.cachePinned = determineCachePinned(cache.getCacheConfiguration());
        this.tierPinned = cache.getCacheConfiguration().getPinningConfiguration() != null
                     && cache.getCacheConfiguration()
                            .getPinningConfiguration()
                            .getStore() == PinningConfiguration.Store.LOCALHEAP;

        this.elementPinningEnabled = !cache.getCacheConfiguration().isOverflowToOffHeap();

        // create the CHM with initialCapacity sufficient to hold maximumSize
        final float loadFactor = maximumSize == 1 ? 1 : DEFAULT_LOAD_FACTOR;
        int initialCapacity = getInitialCapacityForLoadFactor(maximumSize, loadFactor);
        map = new SelectableConcurrentHashMap(poolAccessor, elementPinningEnabled, initialCapacity,
            loadFactor, CONCURRENCY_LEVEL, isClockEviction() && !cachePinned ? maximumSize : 0,
                doNotifications ? cache.getCacheEventNotificationService() : null);

        status = Status.STATUS_ALIVE;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialized " + this.getClass().getName() + " for " + cache.getName());
        }
    }

    private boolean determineCachePinned(CacheConfiguration cacheConfiguration) {
        PinningConfiguration pinningConfiguration = cacheConfiguration.getPinningConfiguration();
        if (pinningConfiguration == null) {
            return false;
        }

        switch (pinningConfiguration.getStore()) {
            case LOCALHEAP:
                return true;

            case LOCALMEMORY:
                return !cacheConfiguration.isOverflowToOffHeap();

            case INCACHE:
                return !(cacheConfiguration.isOverflowToOffHeap() || cacheConfiguration.isOverflowToDisk() || cacheConfiguration.isDiskPersistent());

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Calculates the initialCapacity for a desired maximumSize goal and loadFactor.
     *
     * @param maximumSizeGoal the desired maximum size goal
     * @param loadFactor      the load factor
     * @return the calculated initialCapacity. Returns 0 if the parameter <tt>maximumSizeGoal</tt> is less than or equal
     *         to 0
     */
    static int getInitialCapacityForLoadFactor(int maximumSizeGoal, float loadFactor) {
        double actualMaximum = Math.ceil(maximumSizeGoal / loadFactor);
        return Math.max(0, actualMaximum >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) actualMaximum);
    }

    /**
     * A factory method to create a MemoryStore.
     *
     * @param cache the cache
     * @param pool the pool tracking the on-heap usage
     * @return an instance of a MemoryStore, configured with the appropriate eviction policy
     */
    public static MemoryStore create(final Ehcache cache, Pool pool) {
        MemoryStore memoryStore = new MemoryStore(cache, pool, false);
        cache.getCacheConfiguration().addConfigurationListener(memoryStore);
        return memoryStore;
    }
    /**
     * {@inheritDoc}
     */
    public void unpinAll() {
        if (elementPinningEnabled) {
            map.unpinAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setPinned(Object key, boolean pinned) {
        if (elementPinningEnabled) {
            map.setPinned(key, pinned);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPinned(Object key) {
        return elementPinningEnabled && map.isPinned(key);
    }

    private boolean isPinningEnabled(Element element) {
        return cachePinned || (elementPinningEnabled && isPinned(element.getObjectKey()));
    }

    /**
     * {@inheritDoc}
     */
    public void fill(Element element) {
        if (alwaysPutOnHeap || isPinningEnabled(element) || remove(element.getObjectKey()) != null || canPutWithoutEvicting(element)) {
            put(element);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeIfTierNotPinned(final Object key) {
        return !tierPinned && remove(key) != null;
    }

    /**
     * Puts an item in the store. Note that this automatically results in an eviction if the store is full.
     *
     * @param element the element to add
     */
    public boolean put(final Element element) throws CacheException {
        if (element == null) {
            return false;
        }

        long delta = poolAccessor.add(element.getObjectKey(), element.getObjectValue(), map.storedObject(element), isPinningEnabled(element));
        if (delta > -1) {
            Element old = map.put(element.getObjectKey(), element, delta);
            checkCapacity(element);
            return old == null;
        } else {
            notifyDirectEviction(element);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        long delta = poolAccessor.add(element.getObjectKey(), element.getObjectValue(), map.storedObject(element), isPinningEnabled(element));
        if (delta > -1) {
            Element old = map.put(element.getObjectKey(), element, delta);
            if (writerManager != null) {
                try {
                    writerManager.put(element);
                } catch (RuntimeException e) {
                    throw new StoreUpdateException(e, old != null);
                }
            }
            checkCapacity(element);
            return old == null;
        } else {
            notifyDirectEviction(element);
            return true;
        }
    }

    /**
     * Gets an item from the cache.
     * <p/>
     * The last access time in {@link net.sf.ehcache.Element} is updated.
     *
     * @param key the key of the Element
     * @return the element, or null if there was no match for the key
     */
    public final Element get(final Object key) {
        if (key == null) {
            return null;
        } else {
            final Element e = map.get(key);
            if (e == null) {
                missRate.event();
            } else {
                hitRate.event();
            }
            return e;
        }
    }

    /**
     * Gets an item from the cache, without updating statistics.
     *
     * @param key the cache key
     * @return the element, or null if there was no match for the key
     */
    public final Element getQuiet(Object key) {
        return get(key);
    }

    /**
     * Removes an Element from the store.
     *
     * @param key the key of the Element, usually a String
     * @return the Element if one was found, else null
     */
    public Element remove(final Object key) {
        if (key == null) {
            return null;
        }

        return map.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public void removeNoReturn(final Object key) {
        remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTierPinned() {
        return tierPinned;
    }

    /**
     * {@inheritDoc}
     */
    public Set getPresentPinnedKeys() {
        return map.pinnedKeySet();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPersistent() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        if (key == null) {
            return null;
        }

        // remove single item.
        Element element = map.remove(key);
        if (writerManager != null) {
            writerManager.remove(new CacheEntry(key, element));
        }
        if (element == null && LOG.isDebugEnabled()) {
            LOG.debug(cache.getName() + "Cache: Cannot remove entry as key " + key + " was not found");
        }
        return element;
    }

    /**
     * Memory stores are never backed up and always return false
     */
    public final boolean bufferFull() {
        return false;
    }

    /**
     * Expire all elements.
     * <p/>
     * This is a default implementation which does nothing. Expiration on demand is only implemented for disk stores.
     */
    public void expireElements() {
        for (Object key : map.keySet()) {
            expireElement(key);
        }
    }

    /**
     * Evicts the element for the given key, if it exists and is expired
     * @param key the key
     * @return the evicted element, if any. Otherwise null
     */
    protected Element expireElement(final Object key) {
        Element value = get(key);
        return value != null && value.isExpired() && map.remove(key, value) ? value : null;
    }

    /**
     * Chooses the Policy from the cache configuration
     * @param cache the cache
     * @return the chosen eviction policy
     */
    private static Policy determineEvictionPolicy(Ehcache cache) {
        MemoryStoreEvictionPolicy policySelection = cache.getCacheConfiguration().getMemoryStoreEvictionPolicy();

        if (policySelection.equals(MemoryStoreEvictionPolicy.LRU)) {
            return new LruPolicy();
        } else if (policySelection.equals(MemoryStoreEvictionPolicy.FIFO)) {
            return new FifoPolicy();
        } else if (policySelection.equals(MemoryStoreEvictionPolicy.LFU)) {
            return new LfuPolicy();
        } else if (policySelection.equals(MemoryStoreEvictionPolicy.CLOCK)) {
            return null;
        }

        throw new IllegalArgumentException(policySelection + " isn't a valid eviction policy");
    }

    /**
     * Remove all of the elements from the store.
     */
    public final void removeAll() throws CacheException {
        for (Object key : map.keySet()) {
            remove(key);
        }
    }

    /**
     * Prepares for shutdown.
     */
    public synchronized void dispose() {
        if (status.equals(Status.STATUS_SHUTDOWN)) {
            return;
        }
        status = Status.STATUS_SHUTDOWN;
        flush();
        poolAccessor.unlink();
    }

    /**
     * Flush to disk only if the cache is diskPersistent.
     */
    public final void flush() {
        if (cache.getCacheConfiguration().isClearOnFlush()) {
            removeAll();
        }
    }

    /**
     * Gets an Array of the keys for all elements in the memory cache.
     * <p/>
     * Does not check for expired entries
     *
     * @return An List
     */
    public final List<?> getKeys() {
        return new ArrayList<Object>(map.keySet());
    }

    /**
     * Returns the keySet for this store
     * @return keySet
     */
    protected Set<?> keySet() {
        return map.keySet();
    }

    /**
     * Returns the current store size.
     *
     * @return The size value
     */
    public final int getSize() {
        return map.size();
    }

    /**
     * Returns nothing since a disk store isn't clustered
     *
     * @return returns 0
     */
    public final int getTerracottaClusteredSize() {
        return 0;
    }

    /**
     * A check to see if a key is in the Store. No check is made to see if the Element is expired.
     *
     * @param key The Element key
     * @return true if found. If this method return false, it means that an Element with the given key is definitely not
     *         in the MemoryStore. If it returns true, there is an Element there. An attempt to get it may return null if
     *         the Element has expired.
     */
    public final boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    /**
     * Before eviction elements are checked.
     *
     * @param element the element to notify about its expiry
     */
    private void notifyExpiry(final Element element) {
        cache.getCacheEventNotificationService().notifyElementExpiry(element, false);
    }

    /**
     * Called when an element is evicted even before it could be installed inside the store
     *
     * @param element the evicted element
     */
    protected void notifyDirectEviction(final Element element) {
    }

    /**
     * An algorithm to tell if the MemoryStore is at or beyond its carrying capacity.
     *
     * @return true if the store is full, false otherwise
     */
    public final boolean isFull() {
        return maximumSize > 0 && map.quickSize() >= maximumSize;
    }

    /**
     * Check if adding an element won't provoke an eviction.
     *
     * @param element the element
     * @return true if the element can be added without provoking an eviction.
     */
    public final boolean canPutWithoutEvicting(Element element) {
        if (element == null) {
            return true;
        }

        return !isFull() && poolAccessor.canAddWithoutEvicting(element.getObjectKey(), element.getObjectValue(), map.storedObject(element));
    }

    /**
     * If the store is over capacity, evict elements until capacity is reached
     *
     * @param elementJustAdded the element added by the action calling this check
     */
    private void checkCapacity(final Element elementJustAdded) {
        if (maximumSize > 0 && !isClockEviction()) {
            int evict = Math.min(map.quickSize() - maximumSize, MAX_EVICTION_RATIO);
            for (int i = 0; i < evict; i++) {
                removeElementChosenByEvictionPolicy(elementJustAdded);
            }
        }
    }

    /**
     * Removes the element chosen by the eviction policy
     *
     * @param elementJustAdded it is possible for this to be null
     * @return true if an element was removed, false otherwise.
     */
    private boolean removeElementChosenByEvictionPolicy(final Element elementJustAdded) {

        if (policy == null) {
            return map.evict();
        }

        Element element = findEvictionCandidate(elementJustAdded);
        if (element == null) {
            LOG.debug("Eviction selection miss. Selected element is null");
            return false;
        }

        // If the element is expired, remove
        if (element.isExpired()) {
            remove(element.getObjectKey());
            notifyExpiry(element);
            return true;
        }

        if (isPinningEnabled(element)) {
            return false;
        }

        return evict(element);
    }

    /**
     * Find a "relatively" unused element.
     *
     * @param elementJustAdded the element added by the action calling this check
     * @return the element chosen as candidate for eviction
     */
    private Element findEvictionCandidate(final Element elementJustAdded) {
        Object objectKey = elementJustAdded != null ? elementJustAdded.getObjectKey() : null;
        Element[] elements = sampleElements(objectKey);
        // this can return null. Let the cache get bigger by one.
        return policy.selectedBasedOnPolicy(elements, elementJustAdded);
    }

    /**
     * Uses random numbers to sample the entire map.
     * <p/>
     * This implemenation uses a key array.
     *
     * @param keyHint a key used as a hint indicating where the just added element is
     * @return a random sample of elements
     */
    private Element[] sampleElements(Object keyHint) {
        int size = AbstractPolicy.calculateSampleSize(map.quickSize());
        return map.getRandomValues(size, keyHint);
    }

    /**
     * {@inheritDoc}
     */
    public Object getInternalContext() {
        if (lockProvider != null) {
            return lockProvider;
        } else {
            lockProvider = new LockProvider();
            return lockProvider;
        }
    }

    /**
     * Gets the status of the MemoryStore.
     */
    public final Status getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    public void timeToIdleChanged(long oldTti, long newTti) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void timeToLiveChanged(long oldTtl, long newTtl) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void diskCapacityChanged(int oldCapacity, int newCapacity) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void loggingChanged(boolean oldValue, boolean newValue) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
        maximumSize = newCapacity;
        if (isClockEviction() && !cachePinned) {
            map.setMaxSize(maximumSize);
        }
    }

    private boolean isClockEviction() {
        return policy == null;
    }

    /**
     * {@inheritDoc}
     */
    public void registered(CacheConfiguration config) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void deregistered(CacheConfiguration config) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void maxBytesLocalHeapChanged(final long oldValue, final long newValue) {
        this.poolAccessor.setMaxSize(newValue);
    }

    /**
     * {@inheritDoc}
     */
    public void maxBytesLocalDiskChanged(final long oldValue, final long newValue) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKeyInMemory(Object key) {
        return containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKeyOffHeap(Object key) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKeyOnDisk(Object key) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Policy getInMemoryEvictionPolicy() {
        return policy;
    }

    /**
     * {@inheritDoc}
     */
    public int getInMemorySize() {
        return getSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getInMemorySizeInBytes() {
        if (poolAccessor.getSize() < 0) {
            DefaultSizeOfEngine defaultSizeOfEngine = new DefaultSizeOfEngine(
                SizeOfPolicyConfiguration.resolveMaxDepth(cache),
                SizeOfPolicyConfiguration.resolveBehavior(cache).equals(SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT)
            );
            long sizeInBytes = 0;
            for (Object o : map.values()) {
                Element element = (Element)o;
                if (element != null) {
                    Size size = defaultSizeOfEngine.sizeOf(element.getObjectKey(), element, map.storedObject(element));
                    sizeInBytes += size.getCalculated();
                }
            }
            return sizeInBytes;
        }
        return poolAccessor.getSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getOffHeapSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getOffHeapSizeInBytes() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getOnDiskSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getOnDiskSizeInBytes() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAbortedSizeOf() {
        return poolAccessor.hasAbortedSizeOf();
    }

    /**
     * {@inheritDoc}
     */
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.policy = policy;
    }

    /**
     * {@inheritDoc}
     */
    public Element putIfAbsent(Element element) throws NullPointerException {
        if (element == null) {
            return null;
        }

        long delta = poolAccessor.add(element.getObjectKey(), element.getObjectValue(), map.storedObject(element), isPinningEnabled(element));
        if (delta > -1) {
            Element old = map.putIfAbsent(element.getObjectKey(), element, delta);
            if (old == null) {
              checkCapacity(element);
            } else {
              poolAccessor.delete(delta);
            }
            return old;
        } else {
            notifyDirectEviction(element);
            return null;
        }
    }

    /**
     * Evicts the element from the store
     * @param element the element to be evicted
     * @return true if succeeded, false otherwise
     */
    protected boolean evict(final Element element) {
        final Element remove = remove(element.getObjectKey());
        RegisteredEventListeners cacheEventNotificationService = cache.getCacheEventNotificationService();
        final FrontEndCacheTier frontEndCacheTier = cacheEventNotificationService.getFrontEndCacheTier();
        if (remove != null && frontEndCacheTier != null && frontEndCacheTier.notifyEvictionFromCache(remove.getKey())) {
            cacheEventNotificationService.notifyElementEvicted(remove, false);
        }
        return remove != null;
    }

    /**
     * {@inheritDoc}
     */
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        if (element == null || element.getObjectKey() == null) {
            return null;
        }

        Object key = element.getObjectKey();

        Lock lock = getWriteLock(key);
        lock.lock();
        try {
            Element toRemove = map.get(key);
            if (comparator.equals(element, toRemove)) {
                map.remove(key);
                return toRemove;
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException,
            IllegalArgumentException {
        if (element == null || element.getObjectKey() == null) {
            return false;
        }

        Object key = element.getObjectKey();

        long delta = poolAccessor.add(element.getObjectKey(), element.getObjectValue(), map.storedObject(element), isPinningEnabled(element));
        if (delta > -1) {
            Lock lock = getWriteLock(key);
            lock.lock();
            try {
                Element toRemove = map.get(key);
                if (comparator.equals(old, toRemove)) {
                    map.put(key, element, delta);
                    return true;
                } else {
                    poolAccessor.delete(delta);
                    return false;
                }
            } finally {
                lock.unlock();
            }
        } else {
            notifyDirectEviction(element);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Element replace(Element element) throws NullPointerException {
        if (element == null || element.getObjectKey() == null) {
            return null;
        }

        Object key = element.getObjectKey();

        long delta = poolAccessor.add(element.getObjectKey(), element.getObjectValue(), map.storedObject(element), isPinningEnabled(element));
        if (delta > -1) {
            Lock lock = getWriteLock(key);
            lock.lock();
            try {
                Element toRemove = map.get(key);
                if (toRemove != null) {
                    map.put(key, element, delta);
                    return toRemove;
                } else {
                    poolAccessor.delete(delta);
                    return null;
                }
            } finally {
                lock.unlock();
            }
        } else {
            notifyDirectEviction(element);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getMBean() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean evictFromOnHeap(int count, long size) {
        if (cachePinned) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            boolean removed = removeElementChosenByEvictionPolicy(null);
            if (!removed) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean evictFromOnDisk(int count, long size) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateDiskHitRate() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateDiskMissRate() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateDiskCountSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateDiskByteSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateHeapHitRate() {
        return hitRate.getRate();
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateHeapMissRate() {
        return missRate.getRate();
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateHeapCountSize() {
        return map.quickSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateHeapByteSize() {
        return poolAccessor.getSize();
    }

    private Lock getWriteLock(Object key) {
        return map.lockFor(key).writeLock();
    }

    /**
     * Get a collection of the elements in this store
     *
     * @return element collection
     */
    public Collection<Element> elementSet() {
        return map.values();
    }

    /**
     * LockProvider implementation that uses the segment locks.
     */
    private class LockProvider implements CacheLockProvider {

        /**
         * {@inheritDoc}
         */
        public Sync getSyncForKey(Object key) {
            return new ReadWriteLockSync(map.lockFor(key));
        }
    }

    private static boolean getAdvancedBooleanConfigProperty(String property, String cacheName, boolean defaultValue) {
        String globalPropertyKey = "net.sf.ehcache.store.config." + property;
        String cachePropertyKey = "net.sf.ehcache.store." + cacheName + ".config." + property;
        return Boolean.parseBoolean(System.getProperty(cachePropertyKey, System.getProperty(globalPropertyKey, Boolean.toString(defaultValue))));
    }

    @Override
    public void recalculateSize(Object key) {
        if (key == null) {
            return;
        }
        map.recalculateSize(key);
    }
}

