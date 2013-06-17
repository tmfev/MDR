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
package net.sf.ehcache.writer;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

import java.util.Collection;

/**
 * A CacheWriter is an interface used for write-through and write-behind caching to a underlying resource.
 * <p/>
 * If configured for a cache, CacheWriter's methods will be called on a cache operation. A cache put will cause a CacheWriter write
 * and a cache remove will cause a writer delete.
 * <p/>
 * Implementers should create an implementation which handles storing and deleting to an underlying resource.
 * <p/>
 * <h4>Write-Through</h4>
 * In write-through mode, the cache operation will occur and the writer operation will occur before CacheEventListeners are notified. If
 * the write operation fails an exception will be thrown. This can result in a cache which is inconsistent with the underlying resource.
 * To avoid this, the cache and the underlying resource should be configured to participate in a transaction. In the event of a failure
 * a rollback can return all components to a consistent state.
 * <p/>
 * <h4>Write-Behind</h4>
 * In write-behind mode, writes are written to a write-behind queue. They are written by a separate execution thread in a configurable
 * way. When used with Terracotta Server Array, the queue is highly available. In addition any node in the cluster may perform the
 * write-behind operations.
 * <p/>
 * It's important to note that the operations that are handled by the {@code CacheWriter} don't have any guaranteed ordering in write-behind mode.
 * The processing ordering can be different than the scheduling ordering, so your application needs to be written with this
 * in mind. More information in the CacheWriter chapter of the documentation.
 * <p/>
 * <h4>Creation and Configuration</h4>
 * CacheWriters can be created using the CacheWriterFactory or explicitly by instantiating them through Java code, giving
 * you access to local resources.
 * <p/>
 * The manner upon which a CacheWriter is actually called is determined by the {@link net.sf.ehcache.config.CacheWriterConfiguration} that is set up for cache
 * that is using the CacheWriter.
 * <p/>
 * See the CacheWriter chapter in the documentation for more information on how to use writers.
 *
 * @author Greg Luck
 * @author Geert Bevin
 * @version $Id: CacheWriter.java 4467 2011-08-02 10:07:08Z alexsnaps $
 */
public interface CacheWriter {

    /**
     * Creates a clone of this writer. This method will only be called by ehcache before a
     * cache is initialized.
     * <p/>
     * Implementations should throw CloneNotSupportedException if they do not support clone
     * but that will stop them from being used with defaultCache.
     *
     * @return a clone
     * @throws CloneNotSupportedException if the extension could not be cloned.
     */
    public CacheWriter clone(Ehcache cache) throws CloneNotSupportedException;


    /**
     * Notifies writer to initialise themselves.
     * <p/>
     * This method is called during the Cache's initialise method after it has changed it's
     * status to alive. Cache operations are legal in this method. If you register a cache writer
     * manually after a cache has been initialised already, this method will be called on the
     * cache writer as soon as it has been registered.
     * <p/>
     * Note that if you reuse cache writer instances or create a factory that returns the
     * same cache writer instance as a singleton, your <code>init</code> method should be able
     * to handle that situation. Unless you perform this multiple usage of a cache writer yourself,
     * Ehcache will not do this though. So in the majority of the use cases, you don't need to do
     * anything special. 
     *
     * @throws net.sf.ehcache.CacheException
     */
    void init();

    /**
     * Providers may be doing all sorts of exotic things and need to be able to clean up on
     * dispose.
     * <p/>
     * Cache operations are illegal when this method is called. The cache itself is partly
     * disposed when this method is called.
     */
    void dispose() throws CacheException;

    /**
     * Write the specified value under the specified key to the underlying store.
     * This method is intended to support both key/value creation and value update for a specific key.
     *
     * @param element the element to be written
     */
    void write(Element element) throws CacheException;

    /**
     * Write the specified Elements to the underlying store. This method is intended to support both insert and update.
     * If this operation fails (by throwing an exception) after a partial success,
     * the convention is that entries which have been written successfully are to be removed from the specified mapEntries,
     * indicating that the write operation for the entries left in the map has failed or has not been attempted.
     *
     * @param elements the Elements to be written
     */
    void writeAll(Collection<Element> elements) throws CacheException;


    /**
     * Delete the cache entry from the store
     *
     * @param entry the cache entry that is used for the delete operation
     */
    void delete(CacheEntry entry) throws CacheException;


    /**
     * Remove data and keys from the underlying store for the given collection of keys, if present. If this operation fails
     * (by throwing an exception) after a partial success, the convention is that keys which have been erased successfully
     * are to be removed from the specified keys, indicating that the erase operation for the keys left in the collection
     * has failed or has not been attempted.
     *
     * @param entries the entries that have been removed from the cache
     */
    void deleteAll(Collection<CacheEntry> entries) throws CacheException;

    /**
     * This method will be called, whenever an Element couldn't be handled by the writer and all
     * the {@link net.sf.ehcache.config.CacheWriterConfiguration#getRetryAttempts() retryAttempts} have been tried.
     * <p>When batching is enabled all the elements in the failing batch will be passed to this methods
     * <p>Try to not throw RuntimeExceptions from this method. Should an Exception occur, it will be logged, but
     * the element will be lost anyways.
     * @param element the Element that triggered the failure, or one of the elements part of the batch that failed
     * @param operationType the operation we tried to execute
     * @param e the RuntimeException thrown by the Writer when the last retry attempt was being executed
     */
    void throwAway(Element element, SingleOperationType operationType, RuntimeException e);
}
