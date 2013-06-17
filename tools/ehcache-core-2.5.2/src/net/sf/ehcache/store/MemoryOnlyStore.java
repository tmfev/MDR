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
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.impl.AggregateOnlyResult;
import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.search.impl.OrderComparator;
import net.sf.ehcache.search.impl.ResultImpl;
import net.sf.ehcache.search.impl.ResultsImpl;
import net.sf.ehcache.transaction.SoftLock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A memory-only store with support for all caching features.
 *
 * @author Ludovic Orban
 */
public final class MemoryOnlyStore extends FrontEndCacheTier<NullStore, MemoryStore> {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private final Map<String, AttributeExtractor> attributeExtractors = new ConcurrentHashMap<String, AttributeExtractor>();
    private final Map<String, Attribute> searchAttributes = new ConcurrentHashMap<String, Attribute>();


    private MemoryOnlyStore(CacheConfiguration cacheConfiguration, NullStore cache, MemoryStore authority) {
        super(cache, authority, cacheConfiguration.getCopyStrategy(), cacheConfiguration.isCopyOnWrite(), cacheConfiguration.isCopyOnRead());
    }

    /**
     * Create an instance of MemoryStore
     * @param cache the cache
     * @param onHeapPool the on heap pool
     * @return an instance of MemoryStore
     */
    public static Store create(Ehcache cache, Pool onHeapPool) {
        final NullStore nullStore = NullStore.create();
        final MemoryStore memoryStore = NotifyingMemoryStore.create(cache, onHeapPool);
        return new MemoryOnlyStore(cache.getCacheConfiguration(), nullStore, memoryStore);
    }

    /**
     * {inheritDoc}
     */
    @Override
    public void setInMemoryEvictionPolicy(final Policy policy) {
        authority.setInMemoryEvictionPolicy(policy);
    }

    /**
     * {inheritDoc}
     */
    @Override
    public Policy getInMemoryEvictionPolicy() {
        return authority.getInMemoryEvictionPolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.attributeExtractors.putAll(extractors);

        for (String name : extractors.keySet()) {
            searchAttributes.put(name, new Attribute(name));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Results executeQuery(StoreQuery query) {
        Criteria c = query.getCriteria();

        List<AggregatorInstance<?>> aggregators = query.getAggregatorInstances();


        boolean includeResults = query.requestsKeys() || query.requestsValues() || !query.requestedAttributes().isEmpty();

        ArrayList<Result> results = new ArrayList<Result>();

        boolean hasOrder = !query.getOrdering().isEmpty();

        boolean anyMatches = false;

        for (Element element : authority.elementSet()) {
            if (!hasOrder && query.maxResults() >= 0 && results.size() == query.maxResults()) {
                break;
            }
            if (element.getObjectValue() instanceof SoftLock) {
                continue;
            }

            if (c.execute(element, attributeExtractors)) {
                anyMatches = true;

                if (includeResults) {
                    final Map<String, Object> attributes;
                    if (query.requestedAttributes().isEmpty()) {
                        attributes = Collections.emptyMap();
                    } else {
                        attributes = new HashMap<String, Object>();
                        for (Attribute attribute : query.requestedAttributes()) {
                            String name = attribute.getAttributeName();
                            attributes.put(name, attributeExtractors.get(name).attributeFor(element, name));
                        }
                    }

                    final Object[] sortAttributes;
                    List<StoreQuery.Ordering> orderings = query.getOrdering();
                    if (orderings.isEmpty()) {
                        sortAttributes = EMPTY_OBJECT_ARRAY;
                    } else {
                        sortAttributes = new Object[orderings.size()];
                        for (int i = 0; i < sortAttributes.length; i++) {
                            String name = orderings.get(i).getAttribute().getAttributeName();
                            sortAttributes[i] = attributeExtractors.get(name).attributeFor(element, name);
                        }
                    }


                    results.add(new ResultImpl(element.getObjectKey(), element.getObjectValue(), query, attributes, sortAttributes));
                }

                for (AggregatorInstance<?> aggregator : aggregators) {
                    Attribute<?> attribute = aggregator.getAttribute();
                    if (attribute == null) {
                        aggregator.accept(null);
                    } else {
                        Object val = attributeExtractors.get(attribute.getAttributeName()).attributeFor(element,
                                attribute.getAttributeName());
                        aggregator.accept(val);
                    }
                }
            }
        }

        if (hasOrder) {
            Collections.sort(results, new OrderComparator(query.getOrdering()));

            // trim results to max length if necessary
            int max = query.maxResults();
            if (max >= 0 && (results.size() > max)) {
                results.subList(max, results.size()).clear();
                results.trimToSize();
            }
        }


        List<Object> aggregateResults = aggregators.isEmpty() ? Collections.emptyList() : new ArrayList<Object>();
        for (AggregatorInstance<?> aggregator : aggregators) {
            aggregateResults.add(aggregator.aggregateResult());
        }

        if (anyMatches && !includeResults && !aggregateResults.isEmpty()) {
            // add one row in the results if the only thing included was aggregators and anything matched
            results.add(new AggregateOnlyResult(query));
        }


        if (!aggregateResults.isEmpty()) {
            for (Result result : results) {
                // XXX: yucky cast
                ((BaseResult)result).setAggregateResults(aggregateResults);
            }
        }

        return new ResultsImpl(results, query.requestsKeys(), query.requestsValues(),
                !query.requestedAttributes().isEmpty(), anyMatches && !aggregateResults.isEmpty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        return searchAttributes.get(attributeName);
    }

    /**
     * {@inheritDoc}
     */
    public Object getMBean() {
        return null;
    }
}
