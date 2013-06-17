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

import net.sf.ehcache.Element;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.xa.OptimisticLockFailureException;
import net.sf.ehcache.transaction.xa.XidTransactionID;

/**
 * @author Ludovic Orban
 */
public abstract class AbstractStoreCommand implements Command {

    private final Element oldElement;
    private final Element newElement;

    private Element softLockedElement;

    /**
     * Create a Store Command
     * @param oldElement the element in the underlying store at the time this command is created
     * @param newElement the new element to put in the underlying store
     */
    public AbstractStoreCommand(final Element oldElement, final Element newElement) {
        this.newElement = newElement;
        this.oldElement = oldElement;
    }

    /**
     * Get the element in the underlying store at the time this command is created
     * @return the old element
     */
    protected Element getOldElement() {
        return oldElement;
    }

    /**
     * Get the new element to put in the underlying store
     * @return the new element to put in the underlying store
     */
    protected Element getNewElement() {
        return newElement;
    }

    /**
     * {@inheritDoc}
     */
    public boolean prepare(Store store, SoftLockFactory softLockFactory, XidTransactionID transactionId,
                           ElementValueComparator comparator) {
        Object objectKey = getObjectKey();
        final boolean wasPinned = store.isPinned(objectKey);

        SoftLock softLock = softLockFactory.createSoftLock(transactionId, objectKey, newElement, oldElement, wasPinned);
        softLockedElement = createElement(objectKey, softLock, store, wasPinned);
        softLock.lock();
        softLock.freeze();

        if (oldElement == null) {
            Element previousElement = store.putIfAbsent(softLockedElement);
            if (previousElement != null) {
                softLock.unfreeze();
                softLock.unlock();
                softLockedElement = null;
                throw new OptimisticLockFailureException();
            }
        } else {
            boolean replaced = store.replace(oldElement, softLockedElement, comparator);
            if (!replaced) {
                softLock.unfreeze();
                softLock.unlock();
                softLockedElement = null;
                throw new OptimisticLockFailureException();
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(Store store) {
        if (oldElement == null) {
            store.remove(getObjectKey());
        } else {
            store.put(oldElement);
        }

        SoftLock softLock = (SoftLock) softLockedElement.getObjectValue();
        if (!softLock.wasPinned()) {
            store.setPinned(softLock.getKey(), false);
        }
        softLock.unfreeze();
        softLock.unlock();
        softLockedElement = null;
    }

    private Element createElement(Object key, SoftLock softLock, Store store, boolean wasPinned) {
        Element element = new Element(key, softLock);
        element.setEternal(true);
        if (!wasPinned) {
            store.setPinned(key, true);
        }
        return element;
    }

}
