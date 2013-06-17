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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is the interface for all stores. A store is a physical counterpart to a cache, which
 * is a logical concept.
 *
 * @author Greg Luck
 * @version $Id: Store.java 4919 2011-11-03 16:36:33Z asingh $
 */
public interface Store {

    /**
     * clusterCoherent property
     */
    static final String CLUSTER_COHERENT = "ClusterCoherent";

    /**
     * nodeCoherent property
     */
    static final String NODE_COHERENT = "NodeCoherent";

    /**
     * unpin all pinned keys
     */
    void unpinAll();

    /**
     * Check if the key is pinned
     * @param key the key to be checked
     * @return {@code true} if the element is pinned
     */
    boolean isPinned(Object key);

    /**
     * Mark the key as pinned or not
     * @param key the key to be pinned or not
     * @param pinned  true if the key should be pinned, false otherwise
     */
    void setPinned(Object key, boolean pinned);

    /**
     * Add a listener to the store.
     * @param listener
     */
    void addStoreListener(StoreListener listener);

    /**
     * Remove listener from store.
     * @param listener
     */
    void removeStoreListener(StoreListener listener);

    /**
     * Puts an item into the store.
     * @return true if this is a new put for the key or element is null. Returns false if it was an update.
     */
    boolean put(Element element) throws CacheException;

    /**
     * Puts a collection of elements into the store.
     * @param elements Collection of elements to be put in the store
     */
    void putAll(Collection<Element> elements) throws CacheException;

    /**
     * Puts an item into the store and the cache writer manager in an atomic operation
     * @return true if this is a new put for the key or element is null. Returns false if it was an update.
     */
    boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException;

    /**
     * Gets an item from the cache.
     */
    Element get(Object key);

    /**
     * Gets an {@link Element} from the Store, without updating statistics
     *
     * @return The element
     */
    Element getQuiet(Object key);

    /**
     * Gets an Array of the keys for all elements in the disk store.
     *
     * @return An List of {@link java.io.Serializable} keys
     */
    List getKeys();

    /**
     * Removes an item from the cache.
     *
     * @since signature changed in 1.2 from boolean to Element to support notifications
     */
    Element remove(Object key);

    /**
     * Removes a collection of elements from the cache.
     */
    void removeAll(Collection<?> keys);

    /**
     * Removes an item from the store and the cache writer manager in an atomic operation.
     */
    Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException;

    /**
     * Remove all of the elements from the store.
     * <p/>
     * If there are registered <code>CacheEventListener</code>s they are notified of the expiry or removal
     * of the <code>Element</code> as each is removed.
     */
    void removeAll() throws CacheException;

    /**
     * Put an element in the store if no element is currently mapped to the elements key.
     *
     * @param element element to be added
     * @return the element previously cached for this key, or null if none.
     *
     * @throws NullPointerException if the element is null, or has a null key
     */
    Element putIfAbsent(Element element) throws NullPointerException;

    /**
     * Remove the Element mapped to the key for the supplied element if the value of the supplied Element
     * is equal to the value of the cached Element. This is a CAS operation. It is consistent even against
     * a distributed cache that is not coherent. If the old value is stale when this operation is attempted
     * the remove does not take place.
     *
     * @param element Element to be removed
     * @param comparator ElementValueComparator to use to compare elements
     * @return the Element removed or null if no Element was removed
     *
     * @throws NullPointerException if the element is null, or has a null key
     */
    Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException;

    /**
     * Replace the cached element only if the value of the current Element is equal to the value of the
     * supplied old Element.
     *
     * @param old Element to be test against
     * @param element Element to be cached
     * @param comparator ElementValueComparator to use to compare elements
     * @return true is the Element was replaced
     * @throws NullPointerException if the either Element is null or has a null key
     * @throws IllegalArgumentException if the two Element keys are non-null but not equal
     */
    boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException;

    /**
     * Replace the cached element only if an Element is currently cached for this key
     * @param element Element to be cached
     * @return the Element previously cached for this key, or null if no Element was cached
     * @throws NullPointerException if the Element is null or has a null key
     */
    Element replace(Element element) throws NullPointerException;

    /**
     * Prepares for shutdown.
     */
    void dispose();

    /**
     * Returns the current local store size
     * @return the count of the Elements in the Store on the local machine
     */
    int getSize();

    /**
     * Returns the current local in-memory store size
     * @return the count of the Elements in the Store and in-memory on the local machine
     */
    int getInMemorySize();

    /**
     * Returns the current local off-heap store size
     * @return the count of the Elements in the Store and off-heap on the local machine
     */
    int getOffHeapSize();

    /**
     * Returns the current local on-disk store size
     * @return the count of the Elements in the Store and on-disk on the local machine
     */
    int getOnDiskSize();

    /**
     * Returns the current Terracotta clustered store size
     * @return the count of the Elements in the Store across the cluster
     */
    int getTerracottaClusteredSize();

    /**
     * Gets the size of the in-memory portion of the store, in bytes.
     * <p/>
     * This method may be expensive to run, depending on implementation. Implementers may choose to return
     * an approximate size.
     *
     * @return the approximate in-memory size of the store in bytes
     */
    long getInMemorySizeInBytes();

    /**
     * Gets the size of the off-heap portion of the store, in bytes.
     *
     * @return the approximate off-heap size of the store in bytes
     */
    long getOffHeapSizeInBytes();

    /**
     * Gets the size of the on-disk portion of the store, in bytes.
     *
     * @return the on-disk size of the store in bytes
     */
    long getOnDiskSizeInBytes();

    /**
     * Checks if the cache may contain elements for which the SizeOf engine gave up and only
     * partially calculated the size.
     *
     * @return true if at least one partially sized element may be in the cache
     */
    boolean hasAbortedSizeOf();

    /**
     * Returns the cache status.
     */
    Status getStatus();


    /**
     * A check to see if a key is in the Store.
     *
     * @param key The Element key
     * @return true if found. No check is made to see if the Element is expired.
     *  1.2
     */
    boolean containsKey(Object key);

    /**
     * A check to see if a key is in the Store and is currently held on disk.
     *
     * @param key The Element key
     * @return true if found. No check is made to see if the Element is expired.
     */
    boolean containsKeyOnDisk(Object key);

    /**
     * A check to see if a key is in the Store and is currently held off-heap.
     *
     * @param key The Element key
     * @return true if found. No check is made to see if the Element is expired.
     */
    boolean containsKeyOffHeap(Object key);

    /**
     * A check to see if a key is in the Store and is currently held in memory.
     *
     * @param key The Element key
     * @return true if found. No check is made to see if the Element is expired.
     */
    boolean containsKeyInMemory(Object key);

    /**
     * Expire all elements.
     */
    public void expireElements();

    /**
     * Flush elements to persistent store.
     * @throws IOException if any IO error occurs
     */
    void flush() throws IOException;

    /**
     * Some store types, such as the disk stores can fill their write buffers if puts
     * come in too fast. The thread will wait for a short time before checking again.
     * @return true if the store write buffer is backed up.
     */
    boolean bufferFull();

    /**
     * @return the current eviction policy. This may not be the configured policy, if it has been
     *         dynamically set.
     * @see #setInMemoryEvictionPolicy(Policy)
     */
    Policy getInMemoryEvictionPolicy();

    /**
     * Sets the eviction policy strategy. The Store will use a policy at startup. The store may allow changing
     * the eviction policy strategy dynamically. Otherwise implementations will throw an exception if this method
     * is called.
     *
     * @param policy the new policy
     */
    void setInMemoryEvictionPolicy(Policy policy);

    /**
     * This should not be used, and will generally return null
     * @return some internal context (probably null)
     */
    Object getInternalContext();

    /**
     * Indicates whether this store provides a coherent view of all the elements
     * in a cache.
     *
     * Note that this is same as calling {@link #isClusterCoherent()} (introduced since 2.0)
     * Use {@link #isNodeCoherent()} to find out if the cache is coherent in the current node in the cluster
     *
     * @return {@code true} if the store is coherent; or {@code false} if the
     *         store potentially splits the cache storage with another store or
     *         isn't internally coherent
     * @since 1.7
     */
    boolean isCacheCoherent();

    /**
     * Returns true if the cache is in coherent mode cluster-wide. Returns false otherwise.
     * <p />
     * It applies to coherent clustering mechanisms only e.g. Terracotta
     *
     * @return true if the cache is in coherent mode cluster-wide, false otherwise
     * @since 2.0
     */
    public boolean isClusterCoherent() throws TerracottaNotRunningException;

    /**
     * Returns true if the cache is in coherent mode for the current node. Returns false otherwise.
     * <p />
     * It applies to coherent clustering mechanisms only e.g. Terracotta
     *
     * @return true if the cache is in coherent mode cluster-wide, false otherwise
     * @since 2.0
     */
    public boolean isNodeCoherent() throws TerracottaNotRunningException;

    /**
     * Sets the cache in coherent or incoherent mode for the current node depending on the parameter.
     * Calling {@code setNodeCoherent(true)} when the cache is already in coherent mode or
     * calling {@code setNodeCoherent(false)} when already in incoherent mode will be a no-op.
     * <p />
     * It applies to coherent clustering mechanisms only e.g. Terracotta
     *
     * @param coherent
     *            true transitions to coherent mode, false to incoherent mode
     * @throws UnsupportedOperationException if this store does not support cache coherence, like RMI replication
     * @since 2.0
     */
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException, TerracottaNotRunningException;

    /**
     * This method waits until the cache is in coherent mode in all the connected nodes. If the cache is already in coherent mode it returns
     * immediately
     * <p />
     * It applies to coherent clustering mechanisms only e.g. Terracotta
     * @throws UnsupportedOperationException if this store does not support cache coherence, like RMI replication
     * @throws InterruptedException
     * @since 2.0
     */
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException;

    /**
     * Optional implementation specific MBean exposed by the store.
     *
     * @return implementation specific management bean
     */
    public Object getMBean();

    /**
     * Inform this store of the configured attribute extractors. Stores that will not invoke extractors are free to ignore this call
     *
     * @param extractors
     */
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors);

    /**
     * Execute the given query on this store
     *
     * @param query query to execute
     * @return query results
     */
    public Results executeQuery(StoreQuery query) throws SearchException;

    /**
     * Retrieve the given named search attribute
     *
     * @param <T> type of the attribute
     * @param attributeName the name of the attribute to retrieve
     * @return the search attribute or null if non-existent
     */
    public <T> Attribute<T> getSearchAttribute(String attributeName);

    /**
     * Retries the elements associated with a set of keys without updating the statistics
     * Keys which are not present in the cache will have null values associated
     * with them in the returned map
     *
     * @param keys a collection of keys to look for
     * @return a map of keys and their corresponding values
     */
    Map<Object, Element> getAllQuiet(Collection<?> keys);

    /**
     * Retries the elements associated with a set of keys and update the statistics
     * Keys which are not present in the cache will have null values associated
     * with them in the returned map
     *
     * @param keys a collection of keys to look for
     * @return a map of keys and their corresponding values
     */
    Map<Object, Element> getAll(Collection<?> keys);

    /**
     * Recalculate size of the element mapped to the key
     * @param key the key
     */
    void recalculateSize(Object key);
}
