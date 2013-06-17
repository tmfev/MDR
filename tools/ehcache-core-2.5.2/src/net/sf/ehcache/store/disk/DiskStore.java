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

package net.sf.ehcache.store.disk;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.StripedReadWriteLock;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolableStore;
import net.sf.ehcache.pool.impl.UnboundedPool;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StripedReadWriteLockProvider;
import net.sf.ehcache.store.TierableStore;
import net.sf.ehcache.store.disk.DiskStorageFactory.DiskMarker;
import net.sf.ehcache.store.disk.DiskStorageFactory.DiskSubstitute;
import net.sf.ehcache.store.disk.DiskStorageFactory.Placeholder;
import net.sf.ehcache.writer.CacheWriterManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implements a persistent-to-disk store.
 * <p>
 * All new elements are automatically scheduled for writing to disk.
 *
 * @author Chris Dennis
 * @author Ludovic Orban
 */
public final class DiskStore extends AbstractStore implements TierableStore, PoolableStore, StripedReadWriteLockProvider {

    /**
     * If the CacheManager needs to resolve a conflict with the disk path, it will create a
     * subdirectory in the given disk path with this prefix followed by a number. The presence of this
     * name is used to determined whether it makes sense for a persistent DiskStore to be loaded. Loading
     * persistent DiskStores will only have useful semantics where the diskStore path has not changed.
     */
    public static final String AUTO_DISK_PATH_DIRECTORY_PREFIX = "ehcache_auto_created";

    private static final int FFFFCD7D = 0xffffcd7d;
    private static final int FIFTEEN = 15;
    private static final int TEN = 10;
    private static final int THREE = 3;
    private static final int SIX = 6;
    private static final int FOURTEEN = 14;
    private static final int SIXTEEN = 16;

    private static final int RETRIES_BEFORE_LOCK = 2;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_SEGMENT_COUNT = 64;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int SLEEP_INTERVAL_MS = 10;

    private final DiskStorageFactory disk;
    private final Random rndm = new Random();
    private final Segment[] segments;
    private final int segmentShift;
    private final AtomicReference<Status> status = new AtomicReference<Status>(Status.STATUS_UNINITIALISED);
    private final boolean tierPinned;
    private final boolean persistent;

    private volatile CacheLockProvider lockProvider;
    private volatile Set<Object> keySet;
    private volatile PoolAccessor onHeapPoolAccessor;
    private volatile PoolAccessor onDiskPoolAccessor;


    private DiskStore(DiskStorageFactory disk, Ehcache cache, Pool onHeapPool, Pool onDiskPool) {
        this.segments = new Segment[DEFAULT_SEGMENT_COUNT];
        this.segmentShift = Integer.numberOfLeadingZeros(segments.length - 1);
        this.onHeapPoolAccessor = onHeapPool.createPoolAccessor(this,
            SizeOfPolicyConfiguration.resolveMaxDepth(cache),
            SizeOfPolicyConfiguration.resolveBehavior(cache).equals(SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT));
        this.onDiskPoolAccessor = onDiskPool.createPoolAccessor(this, new DiskSizeOfEngine());

        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR,
                    disk, cache.getCacheConfiguration(), onHeapPoolAccessor, onDiskPoolAccessor);
        }

        this.disk = disk;
        this.disk.bind(this);
        this.status.set(Status.STATUS_ALIVE);
        this.tierPinned = cache.getCacheConfiguration().getPinningConfiguration() != null &&
                     cache.getCacheConfiguration().getPinningConfiguration().getStore() == PinningConfiguration.Store.INCACHE;
        persistent = cache.getCacheConfiguration().isDiskPersistent();
    }

    /**
     * Wait until all elements have been written to disk
     *
     * @throws InterruptedException if the thread was interrupted while waiting
     * @param delayInMs the maximum time to wait, in milliseconds
     */
    void waitUntilEverythingGotFlushedToDisk(long delayInMs) throws InterruptedException {
        int iterations = (int) (delayInMs / SLEEP_INTERVAL_MS);

        for (Segment segment : segments) {
            int count = 0;
            while (segment.countOnHeap() != 0) {
                Thread.sleep(SLEEP_INTERVAL_MS);
                count++;

                if (count > iterations) {
                    throw new CacheException(delayInMs + " ms delay expired");
                }
            }
        }
    }

    /**
     * Creates a persitent-to-disk store for the given cache, using the given disk path.
     *
     * @param cache cache that fronts this store
     * @param diskStorePath disk path to store data in
     * @param onHeapPool pool to track heap usage
     * @param onDiskPool pool to track disk usage
     * @return a fully initialized store
     */
    public static DiskStore create(Ehcache cache, String diskStorePath, Pool onHeapPool, Pool onDiskPool) {
        DiskStorageFactory disk = new DiskStorageFactory(cache, diskStorePath, cache.getCacheEventNotificationService());
        DiskStore store = new DiskStore(disk, cache, onHeapPool, onDiskPool);
        cache.getCacheConfiguration().addConfigurationListener(new CacheConfigurationListenerAdapter(disk, onDiskPool));
        return store;
    }

    /**
     * Creates a persitent-to-disk store for the given cache, using the given disk path.
     * Heap and disk usage are not tracked by the returned store.
     *
     * @param cache cache that fronts this store
     * @param diskStorePath disk path to store data in
     * @return a fully initialized store
     */
    public static DiskStore create(Cache cache, String diskStorePath) {
        return create(cache, diskStorePath, new UnboundedPool(), new UnboundedPool());
    }

    /**
     * {@inheritDoc}
     */
    public void unpinAll() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPinned(Object key) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setPinned(Object key, boolean pinned) {
        // no-op
    }


    /**
     * Generates a unique directory name for use in automatically creating a diskStorePath where there is a conflict.
     *
     * @return a path consisting of {@link #AUTO_DISK_PATH_DIRECTORY_PREFIX} followed by "_" followed by the current
     *         time as a long e.g. ehcache_auto_created_1149389837006
     */
    public static String generateUniqueDirectory() {
        return DiskStore.AUTO_DISK_PATH_DIRECTORY_PREFIX + "_" + System.currentTimeMillis();
    }

    /**
     * Will check whether a Placeholder that failed to flush to disk is lying around
     * If so, it'll try to evict it
     * @param key the key
     * @return true if a failed marker was or is still there, false otherwise
     */
    public boolean cleanUpFailedMarker(final Serializable key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).cleanUpFailedMarker(key, hash);
    }

    /**
     * {@inheritDoc}
     */
    public StripedReadWriteLock createStripedReadWriteLock() {
        return new DiskStoreStripedReadWriteLock();
    }

    /**
     *
     */
    private static final class CacheConfigurationListenerAdapter implements CacheConfigurationListener {

        private final DiskStorageFactory disk;
        private final Pool diskPool;

        private CacheConfigurationListenerAdapter(DiskStorageFactory disk, Pool diskPool) {
            this.disk = disk;
            this.diskPool = diskPool;
        }

        /**
         * {@inheritDoc}
         */
        public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
            // no-op
        }

        /**
         * {@inheritDoc}
         */
        public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
            // no-op
        }

        /**
         * {@inheritDoc}
         */
        public void diskCapacityChanged(int oldCapacity, int newCapacity) {
            disk.setOnDiskCapacity(newCapacity);
        }

        /**
         * {@inheritDoc}
         */
        public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
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
            // no-op
        }

        /**
         * {@inheritDoc}
         */
        public void maxBytesLocalDiskChanged(final long oldValue, final long newValue) {
            diskPool.setMaxSize(newValue);
        }
    }

    /**
     * Change the disk capacity, in number of elements
     * @param newCapacity the new max elements on disk
     */
    public void changeDiskCapacity(int newCapacity) {
        disk.setOnDiskCapacity(newCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean bufferFull() {
        return disk.bufferFull();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKeyInMemory(Object key) {
        return false;
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
        return containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public void expireElements() {
        disk.expireElements();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws IOException {
        disk.flush();
    }

    /**
     * {@inheritDoc}
     */
    public Policy getInMemoryEvictionPolicy() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getInMemorySize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getInMemorySizeInBytes() {
        long size = onHeapPoolAccessor.getSize();
        if (size < 0) {
            return 0;
        } else {
            return size;
        }
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
        return disk.getOnDiskSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getOnDiskSizeInBytes() {
        long size = onDiskPoolAccessor.getSize();
        if (size < 0) {
            return disk.getOnDiskSizeInBytes();
        } else {
            return size;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getTerracottaClusteredSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setInMemoryEvictionPolicy(Policy policy) {
    }

    /**
     * Return a reference to the data file backing this store.
     *
     * @return a reference to the data file backing this store.
     */
    public File getDataFile() {
        return disk.getDataFile();
    }

    /**
     * Return a reference to the index file for this store.
     *
     * @return a reference to the index file for this store.
     */
    public File getIndexFile() {
        return disk.getIndexFile();
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
    public void fill(Element e) {
        put(e);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeIfTierNotPinned(final Object key) {
        return !tierPinned && remove(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean put(Element element) {
        if (element == null) {
            return false;
        } else {
            Object key = element.getObjectKey();
            int hash = hash(key.hashCode());
            Element oldElement = segmentFor(hash).put(key, hash, element, false);
            return oldElement == null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) {
        boolean newPut = put(element);
        if (writerManager != null) {
            try {
                writerManager.put(element);
            } catch (RuntimeException e) {
                throw new StoreUpdateException(e, !newPut);
            }
        }
        return newPut;
    }

    /**
     * {@inheritDoc}
     */
    public Element get(Object key) {
        if (key == null) {
            return null;
        }

        int hash = hash(key.hashCode());
        return segmentFor(hash).get(key, hash);
    }

    /**
     * {@inheritDoc}
     */
    public Element getQuiet(Object key) {
        return get(key);
    }

    /**
     * Return the unretrieved (undecoded) value for this key
     *
     * @param key key to lookup
     * @return Element or ElementSubstitute
     */
    public Object unretrievedGet(Object key) {
        if (key == null) {
            return null;
        }

        int hash = hash(key.hashCode());
        return segmentFor(hash).unretrievedGet(key, hash);
    }

    /**
     * Put the given encoded element directly into the store
     *
     * @param key the key of the element
     * @param encoded the encoded element
     * @return true if the encoded element was installed
     * @throws IllegalArgumentException if the supplied key is already present
     */
    public boolean putRawIfAbsent(Object key, DiskMarker encoded) throws IllegalArgumentException {
        int hash = hash(key.hashCode());
        return segmentFor(hash).putRawIfAbsent(key, hash, encoded);
    }

    /**
     * {@inheritDoc}
     */
    public List getKeys() {
        return new ArrayList(keySet());
    }

    /**
     * Get a set view of the keys in this store
     *
     * @return a set view of the keys in this store
     */
    public Set<Object> keySet() {
        if (keySet != null) {
            return keySet;
        } else {
            keySet = new KeySet();
            return keySet;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }

        int hash = hash(key.hashCode());
        return segmentFor(hash).remove(key, hash, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public void removeNoReturn(Object key) {
        if (key != null) {
            int hash = hash(key.hashCode());
            segmentFor(hash).removeNoReturn(key, hash);
        }
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
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * {@inheritDoc}
     */
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) {
        Element removed = remove(key);
        if (writerManager != null) {
            writerManager.remove(new CacheEntry(key, removed));
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    public void removeAll() {
        for (Segment s : segments) {
            s.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (status.compareAndSet(Status.STATUS_ALIVE, Status.STATUS_SHUTDOWN)) {
            disk.unbind();
            onHeapPoolAccessor.unlink();
            onDiskPoolAccessor.unlink();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        final Segment[] segs = this.segments;
        long size = -1;
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
            size = volatileSize(segs);
            if (size >= 0) {
                break;
            }
        }
        if (size < 0) {
            // Resort to locking all segments
            size = lockedSize(segs);
        }
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) size;
        }
    }

    private static long volatileSize(Segment[] segs) {
        int[] mc = new int[segs.length];
        long check = 0;
        long sum = 0;
        int mcsum = 0;
        for (int i = 0; i < segs.length; ++i) {
            sum += segs[i].count;
            mc[i] = segs[i].modCount;
            mcsum += mc[i];
        }
        if (mcsum != 0) {
            for (int i = 0; i < segs.length; ++i) {
                check += segs[i].count;
                if (mc[i] != segs[i].modCount) {
                    return -1;
                }
            }
        }
        if (check == sum) {
            return sum;
        } else {
            return -1;
        }
    }

    private static long lockedSize(Segment[] segs) {
        long size = 0;
        for (Segment seg : segs) {
            seg.readLock().lock();
        }
        for (Segment seg : segs) {
            size += seg.count;
        }
        for (Segment seg : segs) {
            seg.readLock().unlock();
        }

        return size;
    }

    /**
     * {@inheritDoc}
     */
    public Status getStatus() {
        return status.get();
    }

    /**
     * {@inheritDoc}
     */
    public boolean evictFromOnHeap(int count, long size) {
        // evicting from disk also frees up heap
        return disk.evict(count) == count;
    }

    /**
     * {@inheritDoc}
     */
    public boolean evictFromOnDisk(int count, long size) {
        return disk.evict(count) == count;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateDiskHitRate() {
        float sum = 0;
        for (Segment s : segments) {
            sum += s.getDiskHitRate();
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateDiskMissRate() {
        float sum = 0;
        for (Segment s : segments) {
            sum += s.getDiskMissRate();
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateDiskCountSize() {
        return getOnDiskSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateDiskByteSize() {
        return getOnDiskSizeInBytes();
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateHeapHitRate() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public float getApproximateHeapMissRate() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateHeapCountSize() {
        return getInMemorySize();
    }

    /**
     * {@inheritDoc}
     */
    public long getApproximateHeapByteSize() {
        return getInMemorySizeInBytes();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).containsKey(key, hash);
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
     * {@inheritDoc}
     */
    public Element putIfAbsent(Element element) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = hash(key.hashCode());
        return segmentFor(hash).put(key, hash, element, true);
    }

    /**
     * {@inheritDoc}
     */
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = hash(key.hashCode());
        return segmentFor(hash).remove(key, hash, element, comparator);
    }

    /**
     * {@inheritDoc}
     */
    public boolean replace(Element old, Element element, ElementValueComparator comparator)
            throws NullPointerException, IllegalArgumentException {
        Object key = element.getObjectKey();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, old, element, comparator);
    }

    /**
     * {@inheritDoc}
     */
    public Element replace(Element element) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, element);
    }

    /**
     * Atomically switch (CAS) the <code>expect</code> representation of this element for the
     * <code>fault</code> representation.
     * <p>
     * A successful switch will return <code>true</code>, and free the replaced element/element-proxy.
     * A failed switch will return <code>false</code> and free the element/element-proxy which was not
     * installed.
     *
     * @param key key to which this element (proxy) is mapped
     * @param expect element (proxy) expected
     * @param fault element (proxy) to install
     * @return <code>true</code> if <code>fault</code> was installed
     */
    public boolean fault(Object key, Placeholder expect, DiskMarker fault) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).fault(key, hash, expect, fault);
    }

    /**
     * Remove the matching mapping. The evict method does referential comparison
     * of the unretrieved substitute against the argument value.
     *
     * @param key key to match against
     * @param substitute optional value to match against
     * @return <code>true</code> on a successful remove
     */
    public boolean evict(Object key, DiskSubstitute substitute) {
        return evictElement(key, substitute) != null;
    }

    /**
     * Remove the matching mapping. The evict method does referential comparison
     * of the unretrieved substitute against the argument value.
     *
     * @param key key to match against
     * @param substitute optional value to match against
     * @return the evicted element on a successful remove
     */
    public Element evictElement(Object key, DiskSubstitute substitute) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).evict(key, hash, substitute);
    }

    /**
     * Select a random sample of elements generated by the supplied factory.
     *
     * @param factory generator of the given type
     * @param sampleSize minimum number of elements to return
     * @param keyHint a key on which we are currently working
     * @return list of sampled elements/element substitute
     */
    public List<DiskStorageFactory.DiskSubstitute> getRandomSample(ElementSubstituteFilter factory, int sampleSize, Object keyHint) {
        ArrayList<DiskStorageFactory.DiskSubstitute> sampled = new ArrayList<DiskStorageFactory.DiskSubstitute>(sampleSize);

        // pick a random starting point in the map
        int randomHash = rndm.nextInt();

        final int segmentStart;
        if (keyHint == null) {
            segmentStart = (randomHash >>> segmentShift);
        } else {
            segmentStart = (hash(keyHint.hashCode()) >>> segmentShift);
        }

        int segmentIndex = segmentStart;
        do {
            segments[segmentIndex].addRandomSample(factory, sampleSize, sampled, randomHash);
            if (sampled.size() >= sampleSize) {
                break;
            }

            // move to next segment
            segmentIndex = (segmentIndex + 1) & (segments.length - 1);
        } while (segmentIndex != segmentStart);

        return sampled;
    }

    private static int hash(int hash) {
        int spread = hash;
        spread += (spread << FIFTEEN ^ FFFFCD7D);
        spread ^= spread >>> TEN;
        spread += (spread << THREE);
        spread ^= spread >>> SIX;
        spread += (spread << 2) + (spread << FOURTEEN);
        return (spread ^ spread >>> SIXTEEN);
    }

    private Segment segmentFor(int hash) {
        return segments[hash >>> segmentShift];
    }

    /**
     * Key set implementation for the DiskStore
     */
    final class KeySet extends AbstractSet<Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Object> iterator() {
            return new KeyIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return DiskStore.this.getSize();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return DiskStore.this.containsKey(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object o) {
            return DiskStore.this.remove(o) != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            DiskStore.this.removeAll();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] toArray() {
            Collection<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Collection<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray(a);
        }
    }

    /**
     * LockProvider implementation that uses the segment locks.
     */
    private class LockProvider implements CacheLockProvider {

        /**
         * {@inheritDoc}
         */
        public Sync getSyncForKey(Object key) {
            int hash = key == null ? 0 : hash(key.hashCode());
            return new ReadWriteLockSync(segmentFor(hash));
        }
    }

    /**
     * Superclass for all store iterators.
     */
    abstract class HashIterator {
        private int segmentIndex;
        private Iterator<HashEntry> currentIterator;

        /**
         * Constructs a new HashIterator
         */
        HashIterator() {
            segmentIndex = segments.length;

            while (segmentIndex > 0) {
                segmentIndex--;
                currentIterator = segments[segmentIndex].hashIterator();
                if (currentIterator.hasNext()) {
                    return;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            if (this.currentIterator == null) {
                return false;
            }

            if (this.currentIterator.hasNext()) {
                return true;
            } else {
                while (segmentIndex > 0) {
                    segmentIndex--;
                    currentIterator = segments[segmentIndex].hashIterator();
                    if (currentIterator.hasNext()) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns the next hash-entry - called by subclasses
         *
         * @return next HashEntry
         */
        protected HashEntry nextEntry() {
            if (currentIterator == null) {
                return null;
            }

            if (currentIterator.hasNext()) {
                return currentIterator.next();
            } else {
                while (segmentIndex > 0) {
                    segmentIndex--;
                    currentIterator = segments[segmentIndex].hashIterator();
                    if (currentIterator.hasNext()) {
                        return currentIterator.next();
                    }
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            currentIterator.remove();
        }

        /**
         * Get the current segment index of this iterator
         *
         * @return the current segment index
         */
        int getCurrentSegmentIndex() {
            return segmentIndex;
        }
    }

    /**
     * Iterator over the store key set.
     */
    private final class KeyIterator extends HashIterator implements Iterator<Object> {
        /**
         * {@inheritDoc}
         */
        public Object next() {
            return super.nextEntry().key;
        }
    }

    /**
     * Sync implementation that wraps the segment locks
     */
    private static final class ReadWriteLockSync implements Sync {

        private final ReentrantReadWriteLock lock;

        private ReadWriteLockSync(ReentrantReadWriteLock lock) {
            this.lock = lock;
        }

        /**
         * {@inheritDoc}
         */
        public void lock(LockType type) {
            switch (type) {
            case READ:
                lock.readLock().lock();
                break;
            case WRITE:
                lock.writeLock().lock();
                break;
            default:
                throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean tryLock(LockType type, long msec) throws InterruptedException {
            switch (type) {
            case READ:
                return lock.readLock().tryLock(msec, TimeUnit.MILLISECONDS);
            case WRITE:
                return lock.writeLock().tryLock(msec, TimeUnit.MILLISECONDS);
            default:
                throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
            }
        }

        /**
         * {@inheritDoc}
         */
        public void unlock(LockType type) {
            switch (type) {
            case READ:
                lock.readLock().unlock();
                break;
            case WRITE:
                lock.writeLock().unlock();
                break;
            default:
                throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean isHeldByCurrentThread(LockType type) {
            switch (type) {
            case READ:
                throw new UnsupportedOperationException("Querying of read lock is not supported.");
            case WRITE:
                return lock.isWriteLockedByCurrentThread();
            default:
                throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
            }
        }

    }

    /**
     * StripedReadWriteLock impl.
     */
    private final class DiskStoreStripedReadWriteLock implements StripedReadWriteLock {

        private final net.sf.ehcache.concurrent.ReadWriteLockSync[] locks =
            new net.sf.ehcache.concurrent.ReadWriteLockSync[DEFAULT_SEGMENT_COUNT];

        private DiskStoreStripedReadWriteLock() {
            for (int i = 0; i < locks.length; i++) {
                locks[i] = new net.sf.ehcache.concurrent.ReadWriteLockSync();
            }
        }

        /**
         * {@inheritDoc}
         */
        public ReadWriteLock getLockForKey(final Object key) {
            return getSyncForKey(key).getReadWriteLock();
        }

        /**
         * {@inheritDoc}
         */
        public List<net.sf.ehcache.concurrent.ReadWriteLockSync> getAllSyncs() {
            ArrayList<net.sf.ehcache.concurrent.ReadWriteLockSync> syncs =
                new ArrayList<net.sf.ehcache.concurrent.ReadWriteLockSync>(locks.length);
            Collections.addAll(syncs, locks);
            return syncs;
        }

        /**
         * {@inheritDoc}
         */
        public net.sf.ehcache.concurrent.ReadWriteLockSync getSyncForKey(final Object key) {
            return locks[indexFor(key)];
        }

        private int indexFor(final Object key) {
            return hash(key.hashCode()) >>> segmentShift;
        }
    }
}
