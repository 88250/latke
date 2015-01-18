/*
 * Copyright (c) 2015, b3log.org
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
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
 * The invocation of {@link #add(org.json.JSONObject) add}, {@link #update(java.lang.String, org.json.JSONObject) update} and
 * {@link #remove(java.lang.String) remove} MUST in a transaction. Invocation of method {@link #get(java.lang.String) get} (by id) in a
 * transaction will try to get object from transaction snapshot; if the invocation made is not in a transaction, retrieve object from
 * datastore directly. See <a href="http://88250.b3log.org/transaction_isolation.html">GAE 事务隔离</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.7.2, Mar 28, 2014
 * @see Query
 * @see GAETransaction
 */
@SuppressWarnings("unchecked")
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
     * Writable?
     */
    private boolean writable = true;

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

        return add(jsonObject, DEFAULT_PARENT_KEY.getKind(), DEFAULT_PARENT_KEY.getName());
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
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.DEBUG, "Added an object[oId={0}] in repository[{1}]", new Object[] {ret, getName()});

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
     * <ol>
     * <li>Sets the specified id into the specified new json object</li>
     * <li>Creates a new entity with the specified id</li>
     * <li>Puts the entity into database</li>
     * </ol>
     * </p>
     *
     * <p>
     * <b>Note</b>: the specified id is NOT the key of a database record, but
     * the value of "oId" stored in database value entry of a record.
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
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.DEBUG, "Updated an object[oId={0}] in repository[name={1}]", new Object[] {id, getName()});
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
        LOGGER.log(Level.DEBUG, "Removed an object[oId={0}] from repository[name={1}]", new Object[] {id, getName()});
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
        LOGGER.log(Level.TRACE, "Getting with id[{0}]", id);

        if (Strings.isEmptyOrNull(id)) {
            return null;
        }

        return get(DEFAULT_PARENT_KEY, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        LOGGER.log(Level.TRACE, "Getting with ids[{0}]", ids);

        Map<String, JSONObject> ret;

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

        LOGGER.log(Level.DEBUG, "Got objects[oIds={0}] from repository[name={1}]", new Object[] {ids, getName()});

        return ret;
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

        final Key key = KeyFactory.createKey(parentKey, getName(), id);

        try {
            final Entity entity = datastoreService.get(key);

            ret = entity2JSONObject(entity);

            LOGGER.log(Level.DEBUG, "Got an object[oId={0}] from repository[name={1}]", new Object[] {id, getName()});
        } catch (final EntityNotFoundException e) {
            LOGGER.log(Level.WARN, "Not found an object[oId={0}] in repository[name={1}]", new Object[] {id, getName()});
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
        LOGGER.log(Level.TRACE, "Executing a query[query=[{0}]]", new Object[] {query.toString()});

        final int currentPageNum = query.getCurrentPageNum();
        final Set<Projection> projections = query.getProjections();
        final Filter filter = query.getFilter();
        final int pageSize = query.getPageSize();
        final Map<String, SortDirection> sorts = query.getSorts();
        // Asssumes the application call need to count page
        int pageCount = -1;

        // If the application caller dose NOT want to count page, gets the page count the caller specified
        if (null != query.getPageCount()) {
            pageCount = query.getPageCount();
        }

        return get(currentPageNum, pageSize, pageCount, projections, sorts, filter);
    }

    /**
     * Gets the result object by the specified current page number, page size, page count, sorts, filter.
     *
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @param pageCount if the pageCount specified with {@code -1}, the returned (pageCnt, recordCnt) value will be calculated, otherwise,
     * the returned pageCnt will be this pageCount, and recordCnt will be {@code 0}, means these values will not be calculated
     * @param projections the specified projections
     * @param sorts the specified sorts
     * @param filter the specified filter
     * @return the result object, see return of
     * {@linkplain #get(org.b3log.latke.repository.Query)} for details
     * @throws RepositoryException repository exception
     */
    private JSONObject get(final int currentPageNum, final int pageSize, final int pageCount, final Set<Projection> projections,
        final Map<String, SortDirection> sorts, final Filter filter)
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

        return get(query, currentPageNum, pageSize, pageCount);
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
            LOGGER.log(Level.TRACE, logMsgBuilder.toString());

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
        final Query query = new Query(getName());
        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        return preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
    }

    @Override
    public long count(final org.b3log.latke.repository.Query query) throws RepositoryException {
        final Filter filter = query.getFilter();

        final Query q = new Query(getName());

        if (null != filter) {
            if (filter instanceof PropertyFilter) {
                final FilterPredicate filterPredicate = processPropertyFiler((PropertyFilter) filter);

                q.setFilter(filterPredicate);
            } else { // CompositeFiler
                final CompositeFilter compositeFilter = (CompositeFilter) filter;
                final Query.CompositeFilter queryCompositeFilter = processCompositeFilter(compositeFilter);

                q.setFilter(queryCompositeFilter);
            }
        }

        final PreparedQuery preparedQuery = datastoreService.prepare(q);

        return preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
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
            } else if (value instanceof Number || value instanceof Date || value instanceof Boolean
                || GAE_SUPPORTED_TYPES.contains(value.getClass())) {
                entity.setProperty(key, value);
            } else if (value instanceof Blob) {
                final Blob blob = (Blob) value;

                entity.setProperty(key, new com.google.appengine.api.datastore.Blob(blob.getBytes()));
            }
        }
    }

    /**
     * Gets result json object by the specified query, current page number, page size, page count.
     *
     * <p>
     * If the specified page count equals to {@code -1}, this method will calculate the page count.
     * </p>
     *
     * @param query the specified query
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @param pageCount if the pageCount specified with {@code -1}, the returned (pageCnt, recordCnt) value will be calculated, otherwise,
     * the returned pageCnt will be this pageCount, and recordCnt will be {@code 0}, means these values will not be calculated
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 10, // May be specified by the specified query.pageCount
     *       "paginationRecordCount": "100" // If query.pageCount has been specified with not {@code -1} or {@code null}, this value will
     *                                         be {@code 0} also
     *     },
     *     "rslts": [{
     *         "oId": "...."
     *     }, ....]
     * }
     * </pre>
     *
     * @throws RepositoryException repository exception
     */
    private JSONObject get(final Query query, final int currentPageNum, final int pageSize, final int pageCount)
        throws RepositoryException {
        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        int pageCnt = pageCount;
        int recordCnt = 0;

        if (-1 == pageCnt) { // Application caller dose not specify the page count
            // Calculates the page count
            long count = -1;

            if (-1 == count) {
                count = preparedQuery.countEntities(FetchOptions.Builder.withDefaults());
                LOGGER.log(Level.WARN, "Invoked countEntities() for repository[name={0}, count={1}]", new Object[] {getName(), count});
            }

            recordCnt = (int) count;
            pageCnt = (int) Math.ceil((double) count / (double) pageSize);
        }

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject pagination = new JSONObject();

            ret.put(Pagination.PAGINATION, pagination);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
            pagination.put(Pagination.PAGINATION_RECORD_COUNT, recordCnt);

            QueryResultList<Entity> queryResultList;

            if (1 != currentPageNum) {
                final Cursor startCursor = getStartCursor(currentPageNum, pageSize, preparedQuery);

                queryResultList = preparedQuery.asQueryResultList(withStartCursor(startCursor).limit(pageSize).chunkSize(QUERY_CHUNK_SIZE));
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

            LOGGER.log(Level.DEBUG, "Found objects[size={0}] at page[currentPageNum={1}, pageSize={2}] in repository[{3}]",
                new Object[] {results.length(), currentPageNum, pageSize, getName()});
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
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

        LOGGER.log(Level.TRACE, "[timeMillisId={0}]", new Object[] {timeMillisId});

        return String.valueOf(Long.parseLong(timeMillisId));
    }

    @Override
    public GAETransaction beginTransaction() {
        GAETransaction ret = TX.get();

        if (null != ret) {
            LOGGER.log(Level.DEBUG, "There is a transaction[isActive={0}] in current thread", ret.isActive());
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
    public boolean hasTransactionBegun() {
        return null != TX.get();
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
        Cursor ret = null;

        int emptyCursorIndex = currentPageNum - 1;
        QueryResultList<Entity> results;

        if (null == ret) {
            LOGGER.log(Level.INFO, "No query cursor at all");
            // For the first page
            results = preparedQuery.asQueryResultList(withLimit(pageSize).chunkSize(QUERY_CHUNK_SIZE));
            ret = results.getCursor(); // The end cursor of page 1, also the start cursor of page 2

            emptyCursorIndex = 2;
        }

        // For the remains pages
        for (; emptyCursorIndex < currentPageNum; emptyCursorIndex++) {
            results = preparedQuery.asQueryResultList(withStartCursor(ret).limit(pageSize).chunkSize(QUERY_CHUNK_SIZE));

            ret = results.getCursor();
        }

        return ret;
    }
}
