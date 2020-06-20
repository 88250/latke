/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Abstract repository.
 * <p>
 * This is a base adapter for wrapped {@link #repository repository}, the underlying repository will be instantiated in
 * the {@link #AbstractRepository(java.lang.String) constructor}..
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.3.0.4, Jun 20, 2020
 */
public abstract class AbstractRepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(AbstractRepository.class);

    /**
     * Repository.
     */
    private final Repository repository;

    /**
     * Debug flag.
     */
    private boolean debug;

    /**
     * Constructs a repository with the specified name.
     *
     * @param name the specified name
     */
    public AbstractRepository(final String name) {
        try {
            Class<Repository> repositoryClass;
            final Latkes.RuntimeDatabase runtimeDatabase = Latkes.getRuntimeDatabase();
            switch (runtimeDatabase) {
                case MYSQL:
                case H2:
                    repositoryClass = (Class<Repository>) Class.forName("org.b3log.latke.repository.jdbc.JdbcRepository");
                    break;
                case NONE:
                    repositoryClass = (Class<Repository>) Class.forName("org.b3log.latke.repository.NoneRepository");
                    break;
                default:
                    throw new RuntimeException("The runtime database [" + runtimeDatabase + "] is not support NOW!");
            }

            final Constructor<Repository> constructor = repositoryClass.getConstructor(String.class);
            repository = constructor.newInstance(name);
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize repository!", e);
        }

        Repositories.addRepository(repository);
        LOGGER.log(Level.DEBUG, "Constructed repository [name={}]", name);
    }

    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        if (!isWritable()) {
            throw new RepositoryException("The repository [name=" + getName() + "] is not writable at present");
        }
        Repositories.check(getName(), jsonObject, Keys.OBJECT_ID);
        return repository.add(jsonObject);
    }

    @Override
    public void update(final String id, final JSONObject jsonObject, final String... propertyNames) throws RepositoryException {
        if (!isWritable()) {
            throw new RepositoryException("The repository [name=" + getName() + "] is not writable at present");
        }

        Repositories.check(getName(), jsonObject, Keys.OBJECT_ID);
        repository.update(id, jsonObject, propertyNames);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        if (!isWritable()) {
            throw new RepositoryException("The repository [name=" + getName() + "] is not writable at present");
        }
        repository.remove(id);
    }

    @Override
    public void remove(final Query query) throws RepositoryException {
        if (!isWritable()) {
            throw new RepositoryException("The repository [name=" + getName() + "] is not writable at present");
        }
        repository.remove(query);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        return repository.get(id);
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        return repository.get(ids);
    }

    @Override
    public boolean has(final String id) throws RepositoryException {
        return repository.has(id);
    }

    @Override
    public JSONObject get(final Query query) throws RepositoryException {
        return repository.get(query);
    }

    @Override
    public List<JSONObject> select(final String statement, final Object... params) throws RepositoryException {
        return repository.select(statement, params);
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        return repository.getRandomly(fetchSize);
    }

    @Override
    public long count() throws RepositoryException {
        return repository.count();
    }

    @Override
    public long count(final Query query) throws RepositoryException {
        return repository.count(query);
    }

    @Override
    public Transaction beginTransaction() {
        return repository.beginTransaction();
    }

    @Override
    public boolean hasTransactionBegun() {
        return repository.hasTransactionBegun();
    }

    @Override
    public String getName() {
        return repository.getName();
    }

    @Override
    public boolean isWritable() {
        return repository.isWritable();
    }

    @Override
    public void setWritable(final boolean writable) {
        repository.setWritable(writable);
    }

    @Override
    public void setDebug(final boolean debugEnabled) {
        repository.setDebug(debugEnabled);
    }

    /**
     * Gets the underlying repository.
     *
     * @return underlying repository
     */
    protected Repository getUnderlyingRepository() {
        return repository;
    }
}
