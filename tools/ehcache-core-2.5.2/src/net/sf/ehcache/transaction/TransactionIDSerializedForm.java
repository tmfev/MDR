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
package net.sf.ehcache.transaction;

import net.sf.ehcache.CacheManager;

import java.io.Serializable;

/**
 * A replacement serialized form for transaction IDs. It can be used by transaction ID factories
 * to create IDs that serialize to this form (using writeReplace()) if they don't want or cannot
 * provide directly serializable IDs.
 * <p/>
 * During deserialization, objects of this class will be replaced by the result of the
 * CacheManager.restoreTransactionID() call.
 *
 * @author Ludovic Orban
 */
public final class TransactionIDSerializedForm implements Serializable {
    private final String cacheManagerName;
    private final String clusterUUID;
    private final long creationTime;
    private final int id;
    private final boolean commit;

    /**
     * Constructor
     *
     * @param cacheManagerName the name of the cache manager which contains the factory
     *                         that created the original TransactionID
     * @param clusterUUID the TransactionID's cluster UUID
     * @param creationTime the TransactionID's creation time
     * @param id the TransactionID's internal ID
     * @param commit TransactionID's commit status
     */
    public TransactionIDSerializedForm(String cacheManagerName, String clusterUUID, long creationTime, int id, boolean commit) {
        this.cacheManagerName = cacheManagerName;
        this.clusterUUID = clusterUUID;
        this.creationTime = creationTime;
        this.id = id;
        this.commit = commit;
    }

    /**
     * Get the name of the cache manager which contains the factory that created the
     * original TransactionID
     *
     * @return the cache manager name
     */
    public String getCacheManagerName() {
        return cacheManagerName;
    }

    /**
     * Get the original TransactionID's cluster UUID
     *
     * @return the original TransactionID's cluster UUID
     */
    public String getClusterUUID() {
        return clusterUUID;
    }

    /**
     * Get the original TransactionID's creation time
     *
     * @return the original TransactionID's creation time
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Get the original TransactionID's internal ID
     *
     * @return the original TransactionID's internal ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the original TransactionID's commit status
     *
     * @return the original TransactionID's commit status
     */
    public boolean isCommit() {
        return commit;
    }

    private Object readResolve() {
        CacheManager cacheManager = CacheManager.getCacheManager(cacheManagerName);
        if (cacheManager == null) {
            throw new TransactionException("unable to restore transaction ID from " + cacheManagerName);
        }
        return cacheManager.getOrCreateTransactionIDFactory().restoreTransactionID(this);
    }

}
