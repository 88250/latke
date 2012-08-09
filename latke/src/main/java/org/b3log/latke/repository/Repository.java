/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.b3log.latke.cache.Cache;
import org.json.JSONObject;

/**
 * Repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.1, Apr 9, 2012
 */
public interface Repository {

    /**
     * Adds the specified json object.
     *
     * @param jsonObject the specified json object
     * @return the generated object id
     * @throws RepositoryException repository exception
     */
    String add(final JSONObject jsonObject) throws RepositoryException;

    /**
     * Updates a certain json object by the specified id and the specified new
     * json object.
     *
     * @param id the specified id
     * @param jsonObject the specified new json object
     * @throws RepositoryException repository exception
     */
    void update(final String id, final JSONObject jsonObject)
            throws RepositoryException;

    /**
     * Removes a json object by the specified id.
     *
     * @param id the specified id
     * @throws RepositoryException repository exception
     */
    void remove(final String id) throws RepositoryException;

    /**
     * Gets a json object by the specified id.
     *
     * @param id the specified id
     * @return a json object, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject get(final String id) throws RepositoryException;

    /**
     * Gets json objects by the specified ids.
     * 
     * @param ids the specified ids
     * @return json objects matched in the specified ids
     * @throws RepositoryException repository exception 
     */
    Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException;

    /**
     * Determines a json object specified by the given id exists in this 
     * repository.
     * 
     * @param id the given id
     * @return {@code true} if it exists, otherwise {@code false}
     * @throws RepositoryException repository exception
     */
    boolean has(final String id) throws RepositoryException;

    /**
     * Gets json objects by the specified query.
     * 
     * <h4>Pagination</h4>
     * If the "paginationPageCount" has been specified by caller (as the argument
     * {@link Query#pageCount}), the value will be used in the returned value. 
     * In other words, the page count result will not be calculated by this 
     * interface.
     * 
     * <h4>Caching</h4>
     * Each object in the result list (specified by {@value org.b3log.latke.Keys#RESULTS} 
     * in returned value) will be cached for future queries if the specified 
     * query {@link Query#index(java.lang.String[]) with indices}. Property "oId"
     * will be treated as an index and cached for interface 
     * {@link #get(java.lang.String) get by id} as default.
     *
     * <p>
     *   <b>Note</b>: The order of elements of the returned result list is
     *   decided by datastore implementation, excepts 
     *   {@link Query#addSort(java.lang.String, org.b3log.latke.repository.SortDirection)}
     *   be invoked.
     * </p>
     *
     * @param query the specified query
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250 // May be specified by the specified query.pageCount
     *     },
     *     "rslts": [{
     *         "oId": "...."
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject get(final Query query) throws RepositoryException;

    /**
     * Gets a list of json objects randomly with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of json objects, its size less or equal to the specified
     * fetch size, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException;

    /**
     * Gets the count of all json objects.
     *
     * @return count, returns {@code -1} if not available
     * @throws RepositoryException repository exception
     */
    long count() throws RepositoryException;

    /**
     * Gets the name of this repository.
     *
     * @return the name of this repository
     */
    String getName();

    /**
     * Begins a transaction against the repository.
     * 
     * <p>
     * Callers are responsible for explicitly calling {@linkplain Transaction#commit()}
     * or {@linkplain Transaction#rollback()} when they no longer need the
     * {@code Transaction}. The {@code Transaction} returned by this call will
     * be considered <i>the current transaction</i> until one of the
     * following happens:
     *   <ol>
     *     <li>{@linkplain #beginTransaction()} is invoked from the same thread</li>
     *     <li>{@linkplain Transaction#commit()} is invoked on the
     *        {@code Transaction} returned by this method</li>
     *     Whether or not the commit returns successfully, the {@code Transaction}
     *     will no longer be <i>the current transaction</i>.
     *     <li>{@linkplain Transaction#rollback()} is invoked on the
     *     {@code Transaction} returned by this method</li>
     *      Whether or not the rollback returns successfully, the {@code Transaction}
     *      will no longer be <i>the current transaction</i>.
     *   </ol>
     * </p>
     * 
     * @return the transaction that was started.
     */
    Transaction beginTransaction();

    /**
     * Is the cache enabled?
     *
     * @return {@code true} for enabled, {@code false} otherwise
     */
    boolean isCacheEnabled();

    /**
     * Sets the cache enabled with the specified switch.
     *
     * @param isCacheEnabled the specified switch, {@code true} for enable
     * cache, {@code false} otherwise
     */
    void setCacheEnabled(final boolean isCacheEnabled);
    
    /**
     * Whether the repository is writable.
     * 
     * @return {@code true} if it is writable, returns {@code false} otherwise
     */
    boolean isWritable();
    
    /**
     * Sets whether the repository is writable with the specified flag.
     * 
     * @param writable the specified flat, {@code true} for writable, {@code false} otherwise
     */
    void setWritable(final boolean writable);

    /**
     * Gets the query cache.
     * 
     * <p>
     * The cached object corresponding key is constructed by the underlying 
     * repository implementation, so if the caller of this method want to put 
     * object into this query cache, please take care about the cache key.
     * </p>
     * 
     * @return query cache
     */
    Cache<String, Serializable> getCache();
}
