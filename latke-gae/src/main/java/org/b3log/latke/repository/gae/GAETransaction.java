/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.repository.gae;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.repository.Transaction;
import org.json.JSONObject;

/**
 * Google App Engine datastore transaction. Just wraps {@link com.google.appengine.api.datastore.Transaction} simply.
 * 
 * <p>
 * In this transaction, the caller can {@link org.b3log.latke.repository.Repository#get(java.lang.String) get data} (by id) for retrieving 
 * previous writes. Because the {@link #add(org.json.JSONObject) add}, {@link #update(java.lang.String, org.json.JSONObject) update} and
 * {@link #remove(java.lang.String) remove} will effect on the {@link  #cache transaction cache}, operation results specified by id.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 3, 2011
 * @see GAERepository
 */
public final class GAETransaction implements Transaction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GAETransaction.class.getName());
    /**
     * Underlying Google App Engine transaction.
     */
    private com.google.appengine.api.datastore.Transaction appEngineDatastoreTx;
    /**
     * Times of commit retries.
     */
    public static final int COMMIT_RETRIES = 3;
    /**
     * Transaction cache.
     * 
     * <p>
     * The cache is used to maintain transactional {@link org.b3log.latke.repository.Repository#add(org.json.JSONObject) add}, 
     * {@link org.b3log.latke.repository.Repository#update(java.lang.String, org.json.JSONObject) update} and 
     * {@link org.b3log.latke.repository.Repository#remove(java.lang.String) remove} uncommitted effects on datastore for subsequent 
     * {@link org.b3log.latke.repository.Repository#get(java.lang.String) get} (by id) queries can retrieve the result made before.
     * </p>
     * 
     * <p>
     * Holds data like &lt;oId, json&gt;.
     * </p>
     */
    private Map<String, JSONObject> cache = new HashMap<String, JSONObject>();
    /**
     * Flag of clear query cache.
     */
    private boolean clearQueryCache = true;

    /**
     * Constructs a {@link GAETransaction} object with the specified Google App Engine datastore 
     * {@link com.google.appengine.api.datastore.Transaction transaction}.
     *
     * @param appEngineDatastoreTx the specified Google App Engine datastore transaction
     */
    public GAETransaction(final com.google.appengine.api.datastore.Transaction appEngineDatastoreTx) {
        this.appEngineDatastoreTx = appEngineDatastoreTx;
    }

    @Override
    public String getId() {
        return appEngineDatastoreTx.getId();
    }

    /**
     * Gets a json object from uncommitted transaction cache with the specified id.
     * 
     * @param id the specified id
     * @return json object, returns {@code null} if not found
     */
    public JSONObject getUncommitted(final String id) {
        return cache.get(id);
    }

    /**
     * Gets json objects from uncommitted transaction cache with the specified ids.
     * 
     * @param ids the specified ids
     * @return json objects, return an empty map if not found
     */
    public Map<String, JSONObject> getUncommitted(final Iterable<String> ids) {
        final Map<String, JSONObject> ret = new HashMap<String, JSONObject>();

        for (final String id : ids) {
            final JSONObject o = cache.get(id);
            if (null != o) {
                ret.put(id, o);
            }
        }

        return ret;
    }

    /**
     * Determines the specified id is in transaction cache or not.
     * 
     * @param id the specified id
     * @return {@code true} if in transaction cache, returns {@code false} otherwise
     */
    public boolean hasUncommitted(final String id) {
        return cache.containsKey(id);
    }

    /**
     * Determines the specified ids is in transaction cache or not.
     * 
     * @param ids the specified ids
     * @return {@code true} if in transaction cache, returns {@code false} otherwise
     */
    public boolean hasUncommitted(final Iterable<String> ids) {
        for (final String id : ids) {
            if (cache.containsKey(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Puts the specified uncommitted json object into transaction cache with the specified id.
     * 
     * @param id the specified id
     * @param jsonObject the specified uncommitted json object
     */
    public void putUncommitted(final String id, final JSONObject jsonObject) {
        cache.put(id, jsonObject);
    }

    /**
     * Commits this transaction with {@value #COMMIT_RETRIES} times of retries.
     * 
     * <p>
     * If the transaction committed, clears all transaction cache. If the {@link #clearQueryCache flag} is {@code true}, clears global 
     * cache regions.
     * </p>
     *
     * <p>
     * <b>Throws</b>:<br/>
     * {@link java.util.ConcurrentModificationException} - if commits failed
     * </p>
     * @see #COMMIT_RETRIES
     * @see #cache
     * @see PageCaches#removeAll() 
     */
    @Override
    public void commit() {
        int retries = COMMIT_RETRIES;

        while (true) {
            try {
                appEngineDatastoreTx.commit();

                // Flushes transaction cache into global query (by id) cache.
                for (final Entry<String, JSONObject> cached : cache.entrySet()) {
                    final String cacheKey = GAERepository.CACHE_KEY_PREFIX + cached.getKey();
                    final JSONObject value = cached.getValue();

                    // If the value is null, it means the value has been removed
                    if (null == value) {
                        GAERepository.CACHE.remove(cacheKey);
                    } else {
                        GAERepository.CACHE.put(cacheKey, value);
                    }
                }

                // Committed, clears cache and transaction thread var in repository
                cache.clear();
                GAERepository.TX.set(null);

                if (clearQueryCache) {
                    PageCaches.removeAll();
                }

                break;
            } catch (final ConcurrentModificationException e) {
                if (retries == 0) {
                    throw e;
                }

                --retries;
                LOGGER.log(Level.WARNING, "Retrying to commit this transaction[id={0}, app={1}]",
                           new Object[]{appEngineDatastoreTx.getId(), appEngineDatastoreTx.getApp()});
            }
        }
    }

    @Override
    public void rollback() {
        appEngineDatastoreTx.rollback();

        // Rollbacked, clears cache and transaction thread var in repository
        cache.clear();
        GAERepository.TX.set(null);
    }

    @Override
    public boolean isActive() {
        return appEngineDatastoreTx.isActive();
    }

    @Override
    public void clearQueryCache(final boolean flag) {
        this.clearQueryCache = flag;
    }
}
