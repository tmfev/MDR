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

package net.sf.ehcache.pool.impl;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolableStore;
import net.sf.ehcache.pool.SizeOfEngine;

/**
 * The PoolAccessor class of the BoundedPool
 *
 * @author Chris Dennis
 * @author Ludovic Orban
 */
final class AtomicPoolAccessor extends AbstractPoolAccessor<PoolableStore> {

    private final AtomicLong size;

    /**
     * Creates an atomic pool accessor with the specified properties.
     *
     * @param pool pool to be accessed
     * @param store accessing store
     * @param sizeOfEngine engine used to size objects
     * @param currentSize initial size of the store
     */
    AtomicPoolAccessor(Pool<PoolableStore> pool, PoolableStore store, SizeOfEngine sizeOfEngine, long currentSize) {
        super(pool, store, sizeOfEngine);
        this.size = new AtomicLong(currentSize);
    }

    /**
     * {@inheritDoc}
     */
    protected long add(long sizeOf, boolean force) {
        long newSize = getPool().getSize() + sizeOf;

        if (newSize <= getPool().getMaxSize()) {
            // there is enough room => add & approve
            size.addAndGet(sizeOf);
            return sizeOf;
        } else {
            // check that the element isn't too big
            if (!force && sizeOf > getPool().getMaxSize()) {
                // this is too big to fit in the pool
                return -1;
            }

            // if there is not enough room => evict
            long missingSize = newSize - getPool().getMaxSize();

            if (getPool().getEvictor().freeSpace(getPool().getPoolableStores(), missingSize) || force) {
                size.addAndGet(sizeOf);
                return sizeOf;
            } else {
                // cannot free enough bytes
                return -1;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canAddWithoutEvicting(long sizeOf) {
        long newSize = getPool().getSize() + sizeOf;
        return newSize <= getPool().getMaxSize();
    }

    /**
     * {@inheritDoc}
     */
    public long delete(long sizeOf) {
        checkLinked();

        size.addAndGet(-sizeOf);

        return sizeOf;
    }

    /**
     * {@inheritDoc}
     */
    public long getSize() {
        return size.get();
    }

    /**
     * {@inheritDoc}
     */
    protected void doClear() {
        size.set(0);
    }
}
