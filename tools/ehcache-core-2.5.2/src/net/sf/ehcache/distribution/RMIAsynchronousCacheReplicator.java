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

package net.sf.ehcache.distribution;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import java.lang.ref.SoftReference;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sf.ehcache.distribution.RmiEventMessage.RmiEventType;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Listens to {@link net.sf.ehcache.CacheManager} and {@link net.sf.ehcache.Cache} events and propagates those to
 * {@link CachePeer} peers of the Cache asynchronously.
 * <p/>
 * Updates are guaranteed to be replicated in the order in which they are received.
 * <p/>
 * While much faster in operation than {@link RMISynchronousCacheReplicator}, it does suffer from a number
 * of problems. Elements, which may be being spooled to DiskStore may stay around in memory because references
 * are being held to them from {@link EventMessage}s which are queued up. The replication thread runs once
 * per second, limiting the build up. However a lot of elements can be put into a cache in that time. We do not want
 * to get an {@link OutOfMemoryError} using distribution in circumstances when it would not happen if we were
 * just using the DiskStore.
 * <p/>
 * Accordingly, the Element values in {@link EventMessage}s are held by {@link java.lang.ref.SoftReference} in the queue,
 * so that they can be discarded if required by the GC to avoid an {@link OutOfMemoryError}. A log message
 * will be issued on each flush of the queue if there were any forced discards. One problem with GC collection
 * of SoftReferences is that the VM (JDK1.5 anyway) will do that rather than grow the heap size to the maximum.
 * The workaround is to either set minimum heap size to the maximum heap size to force heap allocation at start
 * up, or put up with a few lost messages while the heap grows.
 *
 * @author Greg Luck
 * @version $Id: RMIAsynchronousCacheReplicator.java 5220 2012-01-31 21:28:43Z cdennis $
 */
public class RMIAsynchronousCacheReplicator extends RMISynchronousCacheReplicator {


    private static final Logger LOG = LoggerFactory.getLogger(RMIAsynchronousCacheReplicator.class.getName());
    

    /**
     * A thread which handles replication, so that replication can take place asynchronously and not hold up the cache
     */
    private final Thread replicationThread = new ReplicationThread();

    /**
     * The amount of time the replication thread sleeps after it detects the replicationQueue is empty
     * before checking again.
     */
    private final int replicationInterval;

    /**
     * The maximum number of Element replication in single RMI message.
     */
    private final int maximumBatchSize;

    /**
     * A queue of updates.
     */
    private final Queue<Object> replicationQueue = new ConcurrentLinkedQueue<Object>();

    /**
     * Constructor for internal and subclass use
     */
    public RMIAsynchronousCacheReplicator(
            boolean replicatePuts,
            boolean replicatePutsViaCopy,
            boolean replicateUpdates,
            boolean replicateUpdatesViaCopy,
            boolean replicateRemovals,
            int replicationInterval,
            int maximumBatchSize) {
        super(replicatePuts,
                replicatePutsViaCopy,
                replicateUpdates,
                replicateUpdatesViaCopy,
                replicateRemovals);
        this.replicationInterval = replicationInterval;
        this.maximumBatchSize = maximumBatchSize;
        status = Status.STATUS_ALIVE;
        replicationThread.start();
    }

    /**
     * RemoteDebugger method for the replicationQueue thread.
     * <p/>
     * Note that the replicationQueue thread locks the cache for the entire time it is writing elements to the disk.
     */
    private void replicationThreadMain() {
        while (true) {
            // Wait for elements in the replicationQueue
            while (alive() && replicationQueue != null && replicationQueue.isEmpty()) {
                try {
                    Thread.sleep(replicationInterval);
                } catch (InterruptedException e) {
                    LOG.debug("Spool Thread interrupted.");
                    return;
                }
            }
            if (notAlive()) {
                return;
            }
            try {
                writeReplicationQueue();
            } catch (Throwable e) {
                LOG.error("Exception on flushing of replication queue: " + e.getMessage() + ". Continuing...", e);
            }
        }
    }


    /**
     * {@inheritDoc}
     * <p/>
     * This implementation queues the put notification for in-order replication to peers.
     *
     * @param cache   the cache emitting the notification
     * @param element the element which was just put into the cache.
     */
    public final void notifyElementPut(final Ehcache cache, final Element element) throws CacheException {
        if (notAlive()) {
            return;
        }

        if (!replicatePuts) {
            return;
        }

        if (replicatePutsViaCopy) {
            if (!element.isSerializable()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Object with key " + element.getObjectKey() + " is not Serializable and cannot be replicated.");
                }
                return;
            }
            addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.PUT, null, element));
        } else {
            if (!element.isKeySerializable()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Object with key " + element.getObjectKey()
                            + " does not have a Serializable key and cannot be replicated via invalidate.");
                }
                return;
            }
            addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.REMOVE, element.getKey(), null));
        }

    }

    /**
     * Called immediately after an element has been put into the cache and the element already
     * existed in the cache. This is thus an update.
     * <p/>
     * The {@link net.sf.ehcache.Cache#put(net.sf.ehcache.Element)} method
     * will block until this method returns.
     * <p/>
     * Implementers may wish to have access to the Element's fields, including value, so the element is provided.
     * Implementers should be careful not to modify the element. The effect of any modifications is undefined.
     *
     * @param cache   the cache emitting the notification
     * @param element the element which was just put into the cache.
     */
    public final void notifyElementUpdated(final Ehcache cache, final Element element) throws CacheException {
        if (notAlive()) {
            return;
        }
        if (!replicateUpdates) {
            return;
        }

        if (replicateUpdatesViaCopy) {
            if (!element.isSerializable()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Object with key " + element.getObjectKey() + " is not Serializable and cannot be updated via copy.");
                }
                return;
            }
            addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.PUT, null, element));
        } else {
            if (!element.isKeySerializable()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Object with key " + element.getObjectKey()
                            + " does not have a Serializable key and cannot be replicated via invalidate.");
                }
                return;
            }
            addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.REMOVE, element.getKey(), null));
        }
    }

    /**
     * Called immediately after an attempt to remove an element. The remove method will block until
     * this method returns.
     * <p/>
     * This notification is received regardless of whether the cache had an element matching
     * the removal key or not. If an element was removed, the element is passed to this method,
     * otherwise a synthetic element, with only the key set is passed in.
     * <p/>
     *
     * @param cache   the cache emitting the notification
     * @param element the element just deleted, or a synthetic element with just the key set if
     *                no element was removed.
     */
    public final void notifyElementRemoved(final Ehcache cache, final Element element) throws CacheException {
        if (notAlive()) {
            return;
        }

        if (!replicateRemovals) {
            return;
        }

        if (!element.isKeySerializable()) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Key " + element.getObjectKey() + " is not Serializable and cannot be replicated.");
            }
            return;
        }
        addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.REMOVE, element.getKey(), null));
    }


    /**
     * Called during {@link net.sf.ehcache.Ehcache#removeAll()} to indicate that the all
     * elements have been removed from the cache in a bulk operation. The usual
     * {@link #notifyElementRemoved(net.sf.ehcache.Ehcache,net.sf.ehcache.Element)}
     * is not called.
     * <p/>
     * This notification exists because clearing a cache is a special case. It is often
     * not practical to serially process notifications where potentially millions of elements
     * have been bulk deleted.
     *
     * @param cache the cache emitting the notification
     */
    public void notifyRemoveAll(final Ehcache cache) {
        if (notAlive()) {
            return;
        }

        if (!replicateRemovals) {
            return;
        }

        addToReplicationQueue(new RmiEventMessage(cache, RmiEventType.REMOVE_ALL, null, null));
    }


    /**
     * Adds a message to the queue.
     * <p/>
     * This method checks the state of the replication thread and warns
     * if it has stopped and then discards the message.
     *
     * @param cacheEventMessage
     */
    protected void addToReplicationQueue(RmiEventMessage eventMessage) {
        if (!replicationThread.isAlive()) {
            LOG.error("CacheEventMessages cannot be added to the replication queue because the replication thread has died.");
        } else {
            switch (eventMessage.getType()) {
                case PUT:
                    replicationQueue.add(new SoftReference(eventMessage));
                    break;
                default:
                    replicationQueue.add(eventMessage);
                    break;
            }
        }
    }


    /**
     * Gets called once per {@link #replicationInterval}.
     * <p/>
     * Sends accumulated messages in bulk to each peer. i.e. if ther are 100 messages and 1 peer,
     * 1 RMI invocation results, not 100. Also, if a peer is unavailable this is discovered in only 1 try.
     * <p/>
     * Makes a copy of the queue so as not to hold up the enqueue operations.
     * <p/>
     * Any exceptions are caught so that the replication thread does not die, and because errors are expected,
     * due to peers becoming unavailable.
     * <p/>
     * This method issues warnings for problems that can be fixed with configuration changes.
     */
    private void writeReplicationQueue() {
        List<EventMessage> eventMessages = extractEventMessages(maximumBatchSize);

        if (!eventMessages.isEmpty()) {
            for (CachePeer cachePeer : listRemoteCachePeers(eventMessages.get(0).getEhcache())) {
                try {
                    cachePeer.send(eventMessages);
                } catch (UnmarshalException e) {
                    String message = e.getMessage();
                    if (message.contains("Read time out") || message.contains("Read timed out")) {
                        LOG.warn("Unable to send message to remote peer due to socket read timeout. Consider increasing" +
                                " the socketTimeoutMillis setting in the cacheManagerPeerListenerFactory. " +
                                "Message was: " + message);
                    } else {
                        LOG.debug("Unable to send message to remote peer.  Message was: " + message);
                    }
                } catch (Throwable t) {
                    LOG.warn("Unable to send message to remote peer.  Message was: " + t.getMessage(), t);
                }
            }
        }
    }

    private void flushReplicationQueue() {
        while (!replicationQueue.isEmpty()) {
            writeReplicationQueue();
        }
    }

    /**
     * Extracts CacheEventMessages and attempts to get a hard reference to the underlying EventMessage
     * <p/>
     * If an EventMessage has been invalidated due to SoftReference collection of the Element, it is not
     * propagated. This only affects puts and updates via copy.
     *
     * @param replicationQueueCopy
     * @return a list of EventMessages which were able to be resolved
     */
    private List<EventMessage> extractEventMessages(int limit) {
        List<EventMessage> list = new ArrayList(Math.min(replicationQueue.size(), limit));

        int droppedMessages = 0;
        
        while (list.size() < limit) {
            Object polled = replicationQueue.poll();
            if (polled == null) {
                break;
            } else if (polled instanceof EventMessage) {
                list.add((EventMessage) polled);
            } else {
                EventMessage message = ((SoftReference<EventMessage>) polled).get();
                if (message == null) {
                    droppedMessages++;
                } else {
                    list.add(message);
                }
            }
        }
        
        if (droppedMessages > 0) {
            LOG.warn(droppedMessages + " messages were discarded on replicate due to reclamation of " +
                    "SoftReferences by the VM. Consider increasing the maximum heap size and/or setting the " +
                    "starting heap size to a higher value.");
        }
        return list;
    }

    /**
     * A background daemon thread that writes objects to the file.
     */
    private final class ReplicationThread extends Thread {
        public ReplicationThread() {
            super("Replication Thread");
            setDaemon(true);
            setPriority(Thread.NORM_PRIORITY);
        }

        /**
         * RemoteDebugger thread method.
         */
        public final void run() {
            replicationThreadMain();
        }
    }

    /**
     * Give the replicator a chance to flush the replication queue, then cleanup and free resources when no longer needed
     */
    public final void dispose() {
        status = Status.STATUS_SHUTDOWN;
        flushReplicationQueue();
    }


    /**
     * Creates a clone of this listener. This method will only be called by ehcache before a cache is initialized.
     * <p/>
     * This may not be possible for listeners after they have been initialized. Implementations should throw
     * CloneNotSupportedException if they do not support clone.
     *
     * @return a clone
     * @throws CloneNotSupportedException if the listener could not be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        //shutup checkstyle
        super.clone();
        return new RMIAsynchronousCacheReplicator(replicatePuts, replicatePutsViaCopy,
                replicateUpdates, replicateUpdatesViaCopy, replicateRemovals, replicationInterval, maximumBatchSize);
    }


}
