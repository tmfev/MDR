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
package net.sf.ehcache.transaction.xa.commands;

import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.xa.XidTransactionID;

/**
 * @author Ludovic Orban
 */
public interface Command {

    /**
     * Is this command represents adding a key to the store
     * @param key the key
     * @return true, if this command would try to add an Element for key, otherwise false
     */
    public boolean isPut(Object key);

    /**
     * Is this command represents removing a key to the store
     * @param key the key
     * @return true, if this command would try to remove an Element for key, otherwise false
     */
    public boolean isRemove(Object key);

    /**
     * Prepare the commmand un the underlying store
     * @param store the underdyling store
     * @param softLockFactory the soft lock factory
     * @param transactionId the transaction ID
     * @param comparator the element value comparator
     * @return true if prepare updated the store, false otherwise
     */
    boolean prepare(Store store, SoftLockFactory softLockFactory, XidTransactionID transactionId, ElementValueComparator comparator);

    /**
     * Rollback the prepared change
     * @param store the underlying store
     */
    public void rollback(Store store);

    /**
     * Get the key of the element this command is working on
     * @return the element's key
     */
    Object getObjectKey();

}
