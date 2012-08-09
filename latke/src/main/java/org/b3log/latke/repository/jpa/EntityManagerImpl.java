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
package org.b3log.latke.repository.jpa;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.json.JSONObject;

/**
 * Entity manager implementation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 10, 2011
 */
public final class EntityManagerImpl implements EntityManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(EntityManagerImpl.class.getName());

    @Override
    public void persist(final Object entity) {
        final Class<? extends Object> clazz = entity.getClass();
        final MetaEntity metaEntity = Entities.getMetaEntity(clazz);
        if (null == metaEntity) {
            throw new IllegalArgumentException("The specified object[class=" + clazz + ",toString="
                                               + entity.toString() + "] is not an entity");
        }

        final String repositoryName = metaEntity.getRepositoryName();
        final Repository repository = Repositories.getRepository(repositoryName);
        if (null == repository) {
            throw new IllegalArgumentException("The specified object[class=" + clazz + ",toString="
                                               + entity.toString() + "] is not an entity");
        }

        try {
            final JSONObject jsonObject = toJSONObject(entity);
            repository.add(jsonObject);
        } catch (final Exception e) {
            // XXX: maybe a transaction required exception....
            final String errMsg = "Can not persist entity[class=" + clazz + ",toString=" + entity.toString() + "]";
            LOGGER.log(Level.SEVERE, errMsg, e);

            throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * Converts the specified entity to a json object.
     * 
     * @param entity the specified entity
     * @return json object
     * @throws Exception exception
     */
    private JSONObject toJSONObject(final Object entity) throws Exception {
        final JSONObject ret = new JSONObject();

        final Class<? extends Object> clazz = entity.getClass();
        final MetaEntity metaEntity = Entities.getMetaEntity(clazz);

        final Map<String, Field> fields = metaEntity.getFields();
        for (final Map.Entry<String, Field> entityField : fields.entrySet()) {
            final String fieldName = entityField.getKey();
            final Field field = entityField.getValue();
            final Class<?> fieldType = field.getType();
            field.setAccessible(true);
            final Object fieldValue = field.get(entity);

            if (field.isAnnotationPresent(Id.class)) {
                if (null != fieldValue) {
                    ret.put(Keys.OBJECT_ID, fieldValue);
                }

                continue;
            }

            if (fieldType.isPrimitive() || String.class.equals(fieldType)) {
                ret.put(fieldName, fieldValue);
            }

            // TODO: relationships handling
        }

        return ret;
    }

    @Override
    public <T> T merge(final T entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(final Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getReference(final Class<T> entityClass,
                              final Object primaryKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFlushMode(final FlushModeType flushMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FlushModeType getFlushMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh(final Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(final Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createQuery(final String qlString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createNamedQuery(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createNativeQuery(final String sqlString,
                                   final Class resultClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createNativeQuery(final String sqlString,
                                   final String resultSetMapping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void joinTransaction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getDelegate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Invokes this method to begin a transaction, this method MUST be invoked
     * once before invoking {@link #persist(java.lang.Object) persist(entity)}, 
     * {@link #merge(java.lang.Object) merge(entity)} or 
     * {@link #remove(java.lang.Object) remove(entity)}, and 
     * {@link EntityTransaction#commit() commit()} or 
     * {@link EntityTransaction#rollback() rollback()} this transaction after
     * operations.
     * </p>
     * 
     * <p>
     * <b>Note</b>: The returned entity transaction has been began, so should 
     * not invoke it's method {@link EntityTransaction#begin() begin()} again.
     * </p>
     * 
     * @return 
     */
    @Override
    public EntityTransaction getTransaction() {
        // The user repository is a Latke built-in repository
        final Repository userRepository = Repositories.getRepository(User.USER);
        final Transaction transaction = userRepository.beginTransaction();

        return new EntityTransactionImpl(transaction);
    }
}
