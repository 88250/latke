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

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DataTypeUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.RuntimeMode;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Blob;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Projection;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Google App Engine repository implementation, wraps
 * <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/package-summary.html">
 * The Datastore Java API(Low-level API)</a> of GAE.
 * 
 * <h3>Transaction</h3>
 * The invocation of {@link #add(org.json.JSONObject) add}, 
 * {@link #update(java.lang.String, org.json.JSONObject) update} and
 * {@link #remove(java.lang.String) remove} MUST in a transaction. 
 * Invocation of method {@link #get(java.lang.String) get} (by id) in a 
 * transaction will try to get object from cache of the transaction, if not hit,
 * retrieve object from transaction snapshot; if the invocation made is not in
 * a transaction, retrieve object from datastore directly. See 
 * <a href="http://88250.b3log.org/transaction_isolation.html">GAE 事务隔离</a>
 * for more details.
 * 
 * <h3>Caching</h3>
 * {@link #CACHE Repository cache} is used to cache the {@link #get(java.lang.String) get} 
 * and {@link #get(org.b3log.latke.repository.Query) query} results if 
 * {@link #cacheEnabled enabled} caching.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.6.0, Jun 27, 2012
 * @see Query
 * @see GAETransaction
 */
@SuppressWarnings("unchecked")
// TODO: 88250, adds async support
public final class GAERepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GAERepository.class.getName());
    /**
     * GAE datastore service.
     */
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    /**
     * GAE datastore supported types.
     */
    private static final Set<Class<?>> GAE_SUPPORTED_TYPES = DataTypeUtils.getSupportedTypes();
    /**
     * Default parent key. Kind is {@code "parentKind"}, name is
     * {@code "parentKeyName"}.
     */
    private static final Key DEFAULT_PARENT_KEY = KeyFactory.createKey("parentKind", "parentKeyName");
    /**
     * Repository cache.
     * <p>
     * &lt;oId, JSONObject&gt;
     * </p>
     */
    public static final Cache<String, Serializable> CACHE;
    /**
     * Repository cache name.
     */
    public static final String REPOSITORY_CACHE_NAME = "repositoryCache";
    /**
     * Repository cache count.
     */
    private static final String REPOSITORY_CACHE_COUNT = "#count";
    /**
     * Repository cache query cursor.
     */
    private static final String REPOSITORY_CACHE_QUERY_CURSOR = "#query#cursor";
    /**
     * Is cache enabled?
     */
    private boolean cacheEnabled = true;
    /**
     * Writable?
     */
    private boolean writable = true;
    /**
     * Cache key prefix.
     */
    public static final String CACHE_KEY_PREFIX = "repository";
    /**
     * Query chunk size.
     */
    private static final int QUERY_CHUNK_SIZE = 50;
    /**
     * The current transaction.
     */
    public static final ThreadLocal<GAETransaction> TX = new InheritableThreadLocal<GAETransaction>();
    /**
     * Repository name.
     */
    private String name;

    /**
     * Initializes cache.
     */
    static {
        final RuntimeEnv runtime = Latkes.getRuntimeEnv();
        if (!runtime.equals(RuntimeEnv.GAE)) {
            throw new RuntimeException("GAE repository can only runs on Google App Engine, please "
                                       + "check your configuration and make sure "
                                       + "Latkes.setRuntimeEnv(RuntimeEnv.GAE) was invoked before "
                                       + "using GAE repository.");
        }

        CACHE = (Cache<String, Serializable>) CacheFactory.getCache(REPOSITORY_CACHE_NAME);

        // TODO: Intializes the runtime mode at application startup
        LOGGER.info("Initializing runtime mode....");
        final Value gaeEnvValue = SystemProperty.environment.value();
        if (SystemProperty.Environment.Value.Production == gaeEnvValue) {
            LOGGER.info("B3log Solo runs in [production] mode");
            Latkes.setRuntimeMode(RuntimeMode.PRODUCTION);
        } else {
            LOGGER.info("B3log Solo runs in [development] mode");
            Latkes.setRuntimeMode(RuntimeMode.DEVELOPMENT);
        }
    }

    /**
     * Constructs a GAE repository with the specified name.
     * 
     * @param name the specified name
     */
    public GAERepository(final String name) {
        this.name = name;
    }

    /**
     * Adds the specified json object with the {@linkplain #DEFAULT_PARENT_KEY
     * default parent key}.
     *
     * @param jsonObject the specified json object
     * @return the generated object id
     * @throws RepositoryException repository exception
     */
    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        final GAETransaction currentTransaction = TX.get();

        if (null == currentTransaction) {
            throw new RepositoryException("Invoking add() outside a transaction");
        }

        final String ret = add(jsonObject, DEFAULT_PARENT_KEY.getKind(), DEFAULT_PARENT_KEY.getName());

        currentTransaction.putUncommitted(ret, jsonObject);

        return ret;
    }

    /**
     * Adds.
     * 
     * @param jsonObject the specified json object
     * @param parentKeyKind the specified parent key kind
     * @param parentKeyName the specified parent key name
     * @return id
     * @throws RepositoryException repository exception
     */
    private String add(final JSONObject jsonObject, final String parentKeyKind, final String parentKeyName)
            throws RepositoryException {
        String ret = null;
        try {
            if (!jsonObject.has(Keys.OBJECT_ID)) {
                ret = genTimeMillisId();

                jsonObject.put(Keys.OBJECT_ID, ret);
            } else {
                ret = jsonObject.getString(Keys.OBJECT_ID);
            }

            final Key parentKey = KeyFactory.createKey(parentKeyKind, parentKeyName);
            final Entity entity = new Entity(getName(), ret, parentKey);
            setProperties(entity, jsonObject);

            datastoreService.put(entity);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.FINER, "Added an object[oId={0}] in repository[{1}]", new Object[]{ret, getName()});

        return ret;
    }

    /**
     * Updates a certain json object by the specified id and the specified new json object.
     *
     * <p>
     * The parent key of the entity to update is the {@linkplain #DEFAULT_PARENT_KEY default parent key}.
     * </p>
     *
     * <p>
     * Invokes this method for an non-existent entity will create a new entity in database, as the same effect of method 
     * {@linkplain #add(org.json.JSONObject)}.
     * </p>
     *
     * <p>
     * Update algorithm steps:
     *   <ol>
     *     <li>Sets the specified id into the specified new json object</li>
     *     <li>Creates a new entity with the specified id</li>
     *     <li>Puts the entity into database</li>
     *   </ol>
     * </p>
     *
     * <p>
     *   <b>Note</b>: the specified id is NOT the key of a database record, but
     *   the value of "oId" stored in database value entry of a record.
     * </p>
     *
     * @param id the specified id
     * @param jsonObject the specified new json object
     * @throws RepositoryException repository exception
     * @see Keys#OBJECT_ID
     */
    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        if (Strings.isEmptyOrNull(id)) {
            return;
        }

        final GAETransaction currentTransaction = TX.get();

        if (null == currentTransaction) {
            throw new RepositoryException("Invoking update() outside a transaction");
        }

        update(id, jsonObject, DEFAULT_PARENT_KEY.getKind(), DEFAULT_PARENT_KEY.getName());

        currentTransaction.putUncommitted(id, jsonObject);
    }

    /**
     * Caches the specified query results with the specified query.
     * 
     * @param results the specified query results
     * @param query the specified query
     * @throws JSONException json exception
     */
    private void cacheQueryResults(final JSONArray results, final org.b3log.latke.repository.Query query) throws JSONException {
        String cacheKey;
        for (int i = 0; i < results.length(); i++) {
            final JSONObject jsonObject = results.optJSONObject(i);

            // 1. Caching for get by id.
            cacheKey = CACHE_KEY_PREFIX + jsonObject.optString(Keys.OBJECT_ID);
            CACHE.putAsync(cacheKey, jsonObject);
            LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}] for default index[oId]",
                       new Object[]{cacheKey, getName()});

            // 2. Caching for get by query with filters (EQUAL operator) only
            final Set<String[]> indexes = query.getIndexes();
            final StringBuilder logMsgBuilder = new StringBuilder();
            for (final String[] index : indexes) {
                final org.b3log.latke.repository.Query futureQuery = new org.b3log.latke.repository.Query().setPageCount(1);
                for (int j = 0; j < index.length; j++) {
                    final String propertyName = index[j];

                    futureQuery.setFilter(new PropertyFilter(propertyName, FilterOperator.EQUAL, jsonObject.opt(propertyName)));
                    logMsgBuilder.append(propertyName).append(",");
                }
                logMsgBuilder.deleteCharAt(logMsgBuilder.length() - 1); // Removes the last comma

                cacheKey = CACHE_KEY_PREFIX + futureQuery.getCacheKey() + "_" + getName();

                final JSONObject futureQueryRet = new JSONObject();
                final JSONObject pagination = new JSONObject();
                futureQueryRet.put(Pagination.PAGINATION, pagination);
                pagination.put(Pagination.PAGINATION_PAGE_COUNT, 1);

                final JSONArray futureQueryResults = new JSONArray();
                futureQueryRet.put(Keys.RESULTS, futureQueryResults);
                futureQueryResults.put(jsonObject);

                CACHE.putAsync(cacheKey, futureQueryRet);
                LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}] for index[{2}] for future query[{3}]",
                           new Object[]{cacheKey, getName(), logMsgBuilder, futureQuery.toString()});
            }
        }
    }

    /**
     * Updates.
     * 
     * @param id the specified id
     * @param jsonObject the specified json object
     * @param parentKeyKind the specified parent key kind
     * @param parentKeyName the specified parent key name
     * @throws RepositoryException repository exception
     */
    private void update(final String id, final JSONObject jsonObject, final String parentKeyKind, final String parentKeyName)
            throws RepositoryException {
        try {
            jsonObject.put(Keys.OBJECT_ID, id);

            final Key parentKey = KeyFactory.createKey(parentKeyKind, parentKeyName);
            final Entity entity = new Entity(getName(), id, parentKey);
            setProperties(entity, jsonObject);

            datastoreService.put(entity);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.FINER, "Updated an object[oId={0}] in repository[name={1}]", new Object[]{id, getName()});
    }

    /**
     * Removes a json object by the specified id with the {@linkplain #DEFAULT_PARENT_KEY default parent key}.
     *
     * @param id the specified id
     * @throws RepositoryException repository exception
     */
    @Override
    public void remove(final String id) throws RepositoryException {
        if (Strings.isEmptyOrNull(id)) {
            return;
        }

        final GAETransaction currentTransaction = TX.get();

        if (null == currentTransaction) {
            throw new RepositoryException("Invoking remove() outside a transaction");
        }

        remove(id, DEFAULT_PARENT_KEY.getKind(), DEFAULT_PARENT_KEY.getName());

        currentTransaction.putUncommitted(id, null);
    }

    /**
     * Removes.
     * 
     * @param id the specified id
     * @param parentKeyKind the specified parent key kind
     * @param parentKeyName the specified parent key name
     * @throws RepositoryException repository exception
     */
    private void remove(final String id, final String parentKeyKind, final String parentKeyName) throws RepositoryException {

        final Key parentKey = KeyFactory.createKey(parentKeyKind, parentKeyName);
        final Key key = KeyFactory.createKey(parentKey, getName(), id);
        datastoreService.delete(key);
        LOGGER.log(Level.FINER, "Removed an object[oId={0}] from repository[name={1}]", new Object[]{id, getName()});
    }

    /**
     * Gets a json object by the specified id with the {@linkplain #DEFAULT_PARENT_KEY default parent key}.
     *
     * @param id the specified id
     * @return a json object, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    @Override
    public JSONObject get(final String id) throws RepositoryException {
        LOGGER.log(Level.FINEST, "Getting with id[{0}]", id);

        if (Strings.isEmptyOrNull(id)) {
            return null;
        }

        final GAETransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            // Gets outside a transaction
            return get(DEFAULT_PARENT_KEY, id);
        }

        // Works in a transaction....

        if (!currentTransaction.hasUncommitted(id)) {
            // Has not mainipulate the object in the current transaction
            // Gets from transaction snapshot view
            return get(DEFAULT_PARENT_KEY, id);
        }

        // The returned value may be null if it has been set to null in the 
        // current transaction
        return currentTransaction.getUncommitted(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        LOGGER.log(Level.FINEST, "Getting with ids[{0}]", ids);

        final GAETransaction currentTransaction = TX.get();

        if (null == currentTransaction || !currentTransaction.hasUncommitted(ids)) {
            Map<String, JSONObject> ret;

            if (cacheEnabled) {
                final String cacheKey = CACHE_KEY_PREFIX + ids.hashCode();
                ret = (Map<String, JSONObject>) CACHE.get(cacheKey);
                if (null != ret) {
                    LOGGER.log(Level.FINER, "Got objects[cacheKey={0}] from repository cache[name={1}]", new Object[]{cacheKey, getName()});
                    return ret;
                }
            }

            final Set<Key> keys = new HashSet<Key>();
            for (final String id : ids) {
                final Key key = KeyFactory.createKey(DEFAULT_PARENT_KEY, getName(), id);

                keys.add(key);
            }

            ret = new HashMap<String, JSONObject>();

            final Map<Key, Entity> map = datastoreService.get(keys);

            for (final Entry<Key, Entity> entry : map.entrySet()) {
                ret.put(entry.getKey().getName(), entity2JSONObject(entry.getValue()));
            }

            LOGGER.log(Level.FINER, "Got objects[oIds={0}] from repository[name={1}]", new Object[]{ids, getName()});

            if (cacheEnabled) {
                final String cacheKey = CACHE_KEY_PREFIX + ids.hashCode();
                CACHE.putAsync(cacheKey, (Serializable) ret);
                LOGGER.log(Level.FINER, "Added objects[cacheKey={0}] in repository cache[{1}]", new Object[]{cacheKey, getName()});
            }

            return ret;
        }

        // The returned value may be null if it has been set to null in the 
        // current transaction
        return currentTransaction.getUncommitted(ids);
    }

    /**
     * Gets a json object with the specified parent key and id.
     * 
     * @param parentKey the specified parent key
     * @param id the specified id
     * @return a json object, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    private JSONObject get(final Key parentKey, final String id) throws RepositoryException {
        JSONObject ret;

        if (cacheEnabled) {
            final String cacheKey = CACHE_KEY_PREFIX + id;
            ret = (JSONObject) CACHE.get(cacheKey);
            if (null != ret) {
                LOGGER.log(Level.FINER, "Got an object[cacheKey={0}] from repository cache[name={1}]", new Object[]{cacheKey, getName()});
                return ret;
            }
        }

        final Key key = KeyFactory.createKey(parentKey, getName(), id);
        try {
            final Entity entity = datastoreService.get(key);
            ret = entity2JSONObject(entity);

            LOGGER.log(Level.FINER, "Got an object[oId={0}] from repository[name={1}]", new Object[]{id, getName()});

            if (cacheEnabled) {
                final String cacheKey = CACHE_KEY_PREFIX + id;
                CACHE.putAsync(cacheKey, ret);
                LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}]", new Object[]{cacheKey, getName()});
            }
        } catch (final EntityNotFoundException e) {
            LOGGER.log(Level.WARNING, "Not found an object[oId={0}] in repository[name={1}]", new Object[]{id, getName()});
            return null;
        }

        return ret;
    }

    @Override
    public boolean has(final String id) throws RepositoryException {
        return null != get(id);
    }

    @Override
    public JSONObject get(final org.b3log.latke.repository.Query query) throws RepositoryException {
        JSONObject ret;

        final String cacheKey = CACHE_KEY_PREFIX + query.getCacheKey() + "_" + getName();
        LOGGER.log(Level.FINEST, "Executing a query[cacheKey={0}, query=[{1}]]", new Object[]{cacheKey, query.toString()});

        if (cacheEnabled) {
            ret = (JSONObject) CACHE.get(cacheKey);
            if (null != ret) {
                LOGGER.log(Level.FINER, "Got query result[cacheKey={0}] from repository cache[name={1}]",
                           new Object[]{cacheKey, getName()});
                return ret;
            }
        }

        final int currentPageNum = query.getCurrentPageNum();
        final Set<Projection> projections = query.getProjections();
        final Filter filter = query.getFilter();
        final int pageSize = query.getPageSize();
        final Map<String, SortDirection> sorts = query.getSorts();
        // Asssumes the application call need to count page
        int pageCount = -1;
        // If the application caller need not to count page, gets the page count the caller specified 
        if (null != query.getPageCount()) {
            pageCount = query.getPageCount();
        }

        ret = get(currentPageNum, pageSize, pageCount, projections, sorts, filter, cacheKey);

        if (cacheEnabled) {
            CACHE.putAsync(cacheKey, ret);
            LOGGER.log(Level.FINER, "Added query result[cacheKey={0}] in repository cache[{1}]", new Object[]{cacheKey, getName()});

            try {
                cacheQueryResults(ret.optJSONArray(Keys.RESULTS), query);
            } catch (final JSONException e) {
                LOGGER.log(Level.WARNING, "Caches query results failed", e);
            }
        }

        return ret;
    }

    /**
     * Gets the result object by the specified current page number, page size, page count, sorts, filter and query cache 
     * key.
     *
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @param pageCount the specified page count
     * @param projections the specified projections
     * @param sorts the specified sorts
     * @param filter the specified filter
     * @param cacheKey the specified cache key of a query
     * @return the result object, see return of
     * {@linkplain #get(org.b3log.latke.repository.Query)} for details
     * @throws RepositoryException repository exception
     */
    private JSONObject get(final int currentPageNum, final int pageSize, final int pageCount, final Set<Projection> projections,
                           final Map<String, SortDirection> sorts, final Filter filter, final String cacheKey)
            throws RepositoryException {
        final Query query = new Query(getName());

        // 1. Filters
        if (null != filter) {
            if (filter instanceof PropertyFilter) {
                final FilterPredicate filterPredicate = processPropertyFiler((PropertyFilter) filter);
                query.setFilter(filterPredicate);
            } else { // CompositeFiler
                final CompositeFilter compositeFilter = (CompositeFilter) filter;
                final Query.CompositeFilter queryCompositeFilter = processCompositeFilter(compositeFilter);
                query.setFilter(queryCompositeFilter);
            }
        }

        // 2. Sorts
        for (final Map.Entry<String, SortDirection> sort : sorts.entrySet()) {
            Query.SortDirection querySortDirection;
            if (sort.getValue().equals(SortDirection.ASCENDING)) {
                querySortDirection = Query.SortDirection.ASCENDING;
            } else {
                querySortDirection = Query.SortDirection.DESCENDING;
            }

            query.addSort(sort.getKey(), querySortDirection);
        }

        // 3. Projections
        for (final Projection projection : projections) {
            query.addProjection(new PropertyProjection(projection.getKey(), projection.getType()));
        }

        return get(query, currentPageNum, pageSize, pageCount, cacheKey);
    }

    /**
     * Converts the specified composite filter to a GAE composite filter.
     * 
     * @param compositeFilter the specified composite filter
     * @return GAE composite filter
     * @throws RepositoryException repository exception
     */
    private Query.CompositeFilter processCompositeFilter(final CompositeFilter compositeFilter) throws RepositoryException {
        Query.CompositeFilter ret;

        final Collection<Query.Filter> filters = new ArrayList<Query.Filter>();
        final List<Filter> subFilters = compositeFilter.getSubFilters();
        for (final Filter subFilter : subFilters) {
            if (subFilter instanceof PropertyFilter) {
                final FilterPredicate filterPredicate = processPropertyFiler((PropertyFilter) subFilter);
                filters.add(filterPredicate);
            } else { // CompositeFilter
                final Query.CompositeFilter queryCompositeFilter = processCompositeFilter((CompositeFilter) subFilter);
                filters.add(queryCompositeFilter);
            }
        }

        switch (compositeFilter.getOperator()) {
            case AND:
                ret = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters);
                break;
            case OR:
                ret = new Query.CompositeFilter(Query.CompositeFilterOperator.OR, filters);
                break;
            default:
                throw new RepositoryException("Unsupported composite filter[operator=" + compositeFilter.getOperator() + "]");
        }

        return ret;
    }

    /**
     * Converts the specified property filter to a GAE filter predicate.
     * 
     * @param propertyFilter the specified property filter
     * @return GAE filter predicate
     * @throws RepositoryException repository exception
     */
    private Query.FilterPredicate processPropertyFiler(final PropertyFilter propertyFilter) throws RepositoryException {
        Query.FilterPredicate ret;

        Query.FilterOperator filterOperator = null;
        switch (propertyFilter.getOperator()) {
            case EQUAL:
                filterOperator = Query.FilterOperator.EQUAL;
                break;
            case GREATER_THAN:
                filterOperator = Query.FilterOperator.GREATER_THAN;
                break;
            case GREATER_THAN_OR_EQUAL:
                filterOperator = Query.FilterOperator.GREATER_THAN_OR_EQUAL;
                break;
            case LESS_THAN:
                filterOperator = Query.FilterOperator.LESS_THAN;
                break;
            case LESS_THAN_OR_EQUAL:
                filterOperator = Query.FilterOperator.LESS_THAN_OR_EQUAL;
                break;
            case NOT_EQUAL:
                filterOperator = Query.FilterOperator.NOT_EQUAL;
                break;
            case IN:
                filterOperator = Query.FilterOperator.IN;
                break;
            default:
                throw new RepositoryException("Unsupported filter operator[" + propertyFilter.getOperator() + "]");
        }

        if (FilterOperator.IN != propertyFilter.getOperator()) {
            ret = new Query.FilterPredicate(propertyFilter.getKey(), filterOperator, propertyFilter.getValue());
        } else {
            final Set<Object> values = new HashSet<Object>();

            final StringBuilder logMsgBuilder = new StringBuilder();
            logMsgBuilder.append("In operation[");
            @SuppressWarnings("unchecked")
            final Collection<?> inValues = (Collection<?>) propertyFilter.getValue();

            for (final Object inValue : inValues) {
                values.add(inValue);
                logMsgBuilder.append(inValue).append(",");
            }
            logMsgBuilder.deleteCharAt(logMsgBuilder.length() - 1);

            logMsgBuilder.append("]");
            LOGGER.log(Level.FINEST, logMsgBuilder.toString());

            ret = new Query.FilterPredicate(propertyFilter.getKey(), Query.FilterOperator.IN, values);
        }

        return ret;
    }

    @Override // XXX: performance issue
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final Query query = new Query(getName());
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final int count = (int) count();

        if (0 == count) {
            return ret;
        }

        final Iterable<Entity> entities = preparedQuery.asIterable();

        if (fetchSize >= count) {
            for (final Entity entity : entities) {
                final JSONObject jsonObject = entity2JSONObject(entity);
                ret.add(jsonObject);
            }

            return ret;
        }

        final List<Integer> fetchIndexes = CollectionUtils.getRandomIntegers(0, count - 1, fetchSize);

        int index = 0;
        for (final Entity entity : entities) {
            index++;

            if (fetchIndexes.contains(index)) {
                final JSONObject jsonObject = entity2JSONObject(entity);
                ret.add(jsonObject);
            }
        }

        return ret;
    }

    @Override
    public long count() {
        final String cacheKey = CACHE_KEY_PREFIX + getName() + REPOSITORY_CACHE_COUNT;
        if (cacheEnabled) {
            final Object o = CACHE.get(cacheKey);
            if (null != o) {
                LOGGER.log(Level.FINER, "Got an object[cacheKey={0}] from repository cache[name={1}]",
                           new Object[]{cacheKey, getName()});
                try {
                    return (Long) o;
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);

                    return -1;
                }
            }
        }

        final Query query = new Query(getName());
        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        final long ret = preparedQuery.countEntities(FetchOptions.Builder.withDefaults());

        if (cacheEnabled) {
            CACHE.putAsync(cacheKey, ret);
            LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}]",
                       new Object[]{cacheKey, getName()});
        }

        return ret;
    }

    /**
     * Converts the specified {@link Entity entity} to a {@link JSONObject
     * json object}.
     *
     * @param entity the specified entity
     * @return converted json object
     */
    public static JSONObject entity2JSONObject(final Entity entity) {
        final Map<String, Object> properties = entity.getProperties();
        final Map<String, Object> jsonMap = new HashMap<String, Object>();

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            final String k = property.getKey();
            final Object v = property.getValue();
            if (v instanceof Text) {
                final Text valueText = (Text) v;
                jsonMap.put(k, valueText.getValue());
            } else if (v instanceof com.google.appengine.api.datastore.Blob) {
                final com.google.appengine.api.datastore.Blob blob = (com.google.appengine.api.datastore.Blob) v;
                jsonMap.put(k, new Blob(blob.getBytes()));
            } else {
                jsonMap.put(k, v);
            }
        }

        return new JSONObject(jsonMap);
    }

    /**
     * Sets the properties of the specified entity by the specified json object.
     *
     * @param entity the specified entity
     * @param jsonObject the specified json object
     * @throws JSONException json exception
     */
    public static void setProperties(final Entity entity, final JSONObject jsonObject) throws JSONException {
        final Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = jsonObject.get(key);

            if (!GAE_SUPPORTED_TYPES.contains(value.getClass()) && !(value instanceof Blob)) {
                throw new RuntimeException("Unsupported type[class=" + value.getClass().getName() + "] in Latke GAE repository");
            }

            if (value instanceof String) {
                final String valueString = (String) value;
                if (valueString.length() > DataTypeUtils.MAX_STRING_PROPERTY_LENGTH) {
                    final Text text = new Text(valueString);
                    entity.setProperty(key, text);
                } else {
                    entity.setProperty(key, value);
                }
            } else if (value instanceof Number
                       || value instanceof Date
                       || value instanceof Boolean
                       || GAE_SUPPORTED_TYPES.contains(value.getClass())) {
                entity.setProperty(key, value);
            } else if (value instanceof Blob) {
                final Blob blob = (Blob) value;
                entity.setProperty(key, new com.google.appengine.api.datastore.Blob(blob.getBytes()));
            }
        }
    }

    /**
     * Gets result json object by the specified query, current page number,
     * page size, page count and cache key.
     * 
     * <p>
     * If the specified page count equals to {@code -1}, this method will calculate the page count.  
     * </p>
     *
     * @param query the specified query
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @param pageCount the specified page count
     * @param cacheKey the specified cache key of a query
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "...."
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    private JSONObject get(final Query query, final int currentPageNum, final int pageSize, final int pageCount, final String cacheKey)
            throws RepositoryException {
        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        int pageCnt = pageCount;
        if (-1 == pageCnt) { // Application caller dose not specify the page count
            // Calculates the page count
            long count = -1;
            final String countCacheKey = cacheKey + REPOSITORY_CACHE_COUNT;
            if (cacheEnabled) {
                final Object o = CACHE.get(countCacheKey);
                if (null != o) {
                    LOGGER.log(Level.FINER, "Got an object[cacheKey={0}] from repository cache[name={1}]",
                               new Object[]{countCacheKey, getName()});
                    count = (Long) o;
                }
            }

            if (-1 == count) {
                count = preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
                LOGGER.log(Level.WARNING, "Invoked countEntities() for repository[name={0}, count={1}]",
                           new Object[]{getName(), count});

                if (cacheEnabled) {
                    CACHE.putAsync(countCacheKey, count);
                    LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}]",
                               new Object[]{countCacheKey, getName()});
                }
            }

            pageCnt = (int) Math.ceil((double) count / (double) pageSize);
        }

        final JSONObject ret = new JSONObject();
        try {
            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);

            QueryResultList<Entity> queryResultList;
            if (1 != currentPageNum) {
                final Cursor startCursor = getStartCursor(currentPageNum, pageSize, preparedQuery);

                queryResultList = preparedQuery.asQueryResultList(withStartCursor(
                        startCursor).limit(pageSize).chunkSize(QUERY_CHUNK_SIZE));
            } else { // The first page
                queryResultList = preparedQuery.asQueryResultList(withLimit(pageSize).chunkSize(QUERY_CHUNK_SIZE));
            }

            // Converts entities to json objects
            final JSONArray results = new JSONArray();
            ret.put(Keys.RESULTS, results);
            for (final Entity entity : queryResultList) {
                final JSONObject jsonObject = entity2JSONObject(entity);

                results.put(jsonObject);
            }

            LOGGER.log(Level.FINER, "Found objects[size={0}] at page[currentPageNum={1}, pageSize={2}] in repository[{3}]",
                       new Object[]{results.length(), currentPageNum, pageSize, getName()});
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * Gets current date time string.
     *
     * @return a time millisecond string
     */
    public static String genTimeMillisId() {
        final String timeMillisId = Ids.genTimeMillisId();
        final long inc = CACHE.inc("id-step-generator", 1);

        LOGGER.log(Level.FINEST, "[timeMillisId={0}, inc={1}]",
                   new Object[]{timeMillisId, inc});

        return String.valueOf(Long.parseLong(timeMillisId) + inc);
    }

    @Override
    public GAETransaction beginTransaction() {
        GAETransaction ret = TX.get();
        if (null != ret) {
            LOGGER.log(Level.FINER, "There is a transaction[isActive={0}] in current thread", ret.isActive());
            if (ret.isActive()) {
                return TX.get(); // Using 'the current transaction'
            }
        }

        final com.google.appengine.api.datastore.Transaction gaeTx = datastoreService.beginTransaction();

        ret = new GAETransaction(gaeTx);
        TX.set(ret);

        return ret;
    }

    @Override
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    @Override
    public void setCacheEnabled(final boolean isCacheEnabled) {
        this.cacheEnabled = isCacheEnabled;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

    @Override
    public Cache<String, Serializable> getCache() {
        return CACHE;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the end cursor of the specified current page number, page size and 
     * the prepared query.
     * 
     * @param currentPageNum the specified current page number, MUST larger 
     * then 1
     * @param pageSize the specified page size
     * @param preparedQuery the specified prepared query
     * @return the start cursor
     */
    private Cursor getStartCursor(final int currentPageNum, final int pageSize, final PreparedQuery preparedQuery) {
        int i = currentPageNum - 1;
        Cursor ret = null;
        for (; i > 0; i--) {
            final String cacheKey = CACHE_KEY_PREFIX + getName() + REPOSITORY_CACHE_QUERY_CURSOR + '(' + i + ')';
            ret = (Cursor) CACHE.get(cacheKey);
            if (null != ret) {
                LOGGER.log(Level.FINEST, "Found a query cursor[{0}] in repository cache[name={1}]",
                           new Object[]{i, getName()});
                // Found the nearest cursor
                break;
            }
        }

        int emptyCursorIndex = i;
        QueryResultList<Entity> results;
        String cacheKey;
        if (null == ret) { // No cache at all
            LOGGER.log(Level.INFO, "No query cursor at all");
            // For the first page
            results = preparedQuery.asQueryResultList(withLimit(pageSize).
                    chunkSize(QUERY_CHUNK_SIZE));
            ret = results.getCursor(); // The end cursor of page 1, also the start cursor of page 2
            cacheKey = CACHE_KEY_PREFIX + getName() + REPOSITORY_CACHE_QUERY_CURSOR + "(2)";
            CACHE.putAsync(cacheKey, ret);

            emptyCursorIndex = 2;
        }

        // For the remains pages
        for (; emptyCursorIndex < currentPageNum; emptyCursorIndex++) {
            results = preparedQuery.asQueryResultList(withStartCursor(ret).limit(pageSize).chunkSize(QUERY_CHUNK_SIZE));

            ret = results.getCursor();
            cacheKey = CACHE_KEY_PREFIX + getName() + REPOSITORY_CACHE_QUERY_CURSOR + '(' + (emptyCursorIndex + 1) + ')';
            CACHE.putAsync(cacheKey, ret);
        }

        return ret;
    }
}
