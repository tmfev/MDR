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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A transaction ID implementation with uniqueness across a single JVM
 *
 * @author Ludovic Orban
 */
public final class TransactionIDImpl implements TransactionID {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;
    private volatile boolean commit;

    /**
     * Create a new TransactionIDImpl instance
     */
    TransactionIDImpl() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDecisionCommit() {
        return commit;
    }

    /**
     * {@inheritDoc}
     */
    public void markForCommit() {
        this.commit = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof TransactionIDImpl) {
            TransactionIDImpl otherId = (TransactionIDImpl) obj;
            return id == otherId.id;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "" + id + (commit ? " (marked for commit)" : "");
    }
}
