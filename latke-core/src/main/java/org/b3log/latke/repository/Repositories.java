/*
 * Copyright (c) 2009-present, b3log.org
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Nov 4, 2018
 */
public final class Repositories {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Repositories.class);

    /**
     * Repository holder.
     *
     * <p>
     * &lt;repositoryName, {@link Repository repository}&gt;
     * <p>
     */
    private static final Map<String, Repository> REPOS_HOLDER = new ConcurrentHashMap<>();

    /**
     * Repositories description (repository.json).
     */
    private static JSONObject repositoriesDescription;

    /**
     * Whether all repositories is writable.
     */
    private static boolean repositoryiesWritable = true;

    static {
        loadRepositoryDescription();
    }

    /**
     * Whether all repositories is writable.
     *
     * @return {@code true} if they are writable, returns {@code false} otherwise
     */
    public static boolean getReposirotiesWritable() {
        return repositoryiesWritable;
    }

    /**
     * Sets all repositories whether is writable with the specified flag.
     *
     * @param writable the specified flat, {@code true} for writable, {@code false} otherwise
     */
    public static void setRepositoriesWritable(final boolean writable) {
        for (final Map.Entry<String, Repository> entry : REPOS_HOLDER.entrySet()) {
            final String repositoryName = entry.getKey();
            final Repository repository = entry.getValue();

            repository.setWritable(writable);

            LOGGER.log(Level.INFO, "Sets repository[name={0}] writable[{1}]", new Object[]{repositoryName, writable});
        }

        repositoryiesWritable = writable;
    }

    /**
     * Gets repository names.
     *
     * @return repository names, for example,
     * <pre>
     * [
     *     "repository1", "repository2", ....
     * ]
     * </pre>
     */
    public static JSONArray getRepositoryNames() {
        final JSONArray ret = new JSONArray();

        if (null == repositoriesDescription) {
            LOGGER.log(Level.INFO, "Not found repository description[repository.json] file under classpath");

            return ret;
        }

        final JSONArray repositories = repositoriesDescription.optJSONArray("repositories");

        for (int i = 0; i < repositories.length(); i++) {
            final JSONObject repository = repositories.optJSONObject(i);

            ret.put(repository.optString("name"));
        }

        return ret;
    }

    /**
     * Gets repositories description.
     *
     * @return repositories description, returns {@code null} if not found or
     * parse the description failed
     */
    public static JSONObject getRepositoriesDescription() {
        return repositoriesDescription;
    }

    /**
     * Determines whether the specified json object can not be persisted (add or update) into an repository which specified
     * by the given repository name.
     *
     * <p>
     * A valid json object to persist must match keys definitions (including type and length if had) in the repository description
     * (repository.json) with the json object names itself.
     * </p>
     *
     * <p>
     * The specified keys to ignore will be bypassed, regardless of matching keys definitions.
     * </p>
     *
     * @param repositoryName the given repository name (maybe with table name prefix)
     * @param jsonObject     the specified json object
     * @param ignoredKeys    the specified keys to ignore
     * @throws RepositoryException if the specified json object can not be persisted
     * @see Repository#add(org.json.JSONObject)
     * @see Repository#update(java.lang.String, org.json.JSONObject)
     */
    public static void check(final String repositoryName, final JSONObject jsonObject, final String... ignoredKeys)
            throws RepositoryException {
        if (null == jsonObject) {
            throw new RepositoryException("Null to persist to repository [" + repositoryName + "]");
        }

        final JSONObject repositoryDef = getRepositoryDef(repositoryName);
        if (!repositoryDef.optBoolean("fieldcheck")) { // 默认不启用字段检查 https://github.com/b3log/latke/issues/103
            return;
        }

        final boolean needIgnoreKeys = null != ignoredKeys && 0 < ignoredKeys.length;
        final JSONArray names = jsonObject.names();
        final Set<Object> nameSet = CollectionUtils.jsonArrayToSet(names);

        final JSONArray keysDef = repositoryDef.optJSONArray("keys");
        if (null == keysDef) {
            return;
        }

        final Set<String> keySet = new HashSet<>();

        // Checks whether the specified json object has all keys defined, and whether the type of its value is appropriate
        for (int i = 0; i < keysDef.length(); i++) {
            final JSONObject keyDescription = keysDef.optJSONObject(i);

            final String key = keyDescription.optString("name");
            keySet.add(key);

            if (needIgnoreKeys && Strings.containsIgnoreCase(key, ignoredKeys)) {
                continue;
            }

            if (!keyDescription.optBoolean("nullable") && !nameSet.contains(key)) {
                throw new RepositoryException("A json object to persist to repository [name=" + repositoryName + "] does not contain a key [" + key + "]");
            }

            // TODO: 88250, type and length validation
            /*
             * final String type = keyDescription.optString("type"); final
             * Object value = jsonObject.opt(key);
             *
             * if (("String".equals(type) && !(value instanceof String)) ||
             * ("int".equals(type) && !(value instanceof Integer)) ||
             * ("long".equals(type) && !(value instanceof Long)) ||
             * ("double".equals(type) && !(value instanceof Double)) ||
             * ("boolean".equals(type) && !(value instanceof Boolean))) {
             * LOGGER.log(Level.WARNING,
             * "A json object to persist to repository[name={0}] has " +
             * "a wrong value type[definedType={1}, currentType={2}] with key["
             * + key + "]", new Object[]{repositoryName, type,
             * value.getClass()});
             *
             * return true; }
             */
        }

        // Checks whether the specified json object has an redundant (undefined) key
        for (int i = 0; i < names.length(); i++) {
            final String name = names.optString(i);
            if (!keySet.contains(name)) {
                throw new RepositoryException("A json object to persist to repository [name=" + repositoryName + "] contains an redundant key [" + name + "]");
            }
        }
    }

    /**
     * Gets the repository definition of an repository specified by the given repository name.
     *
     * @param repositoryName the given repository name (maybe with table name prefix)
     * @return repository definition, returns {@code null} if not found
     */
    public static JSONObject getRepositoryDef(final String repositoryName) {
        if (StringUtils.isBlank(repositoryName)) {
            return null;
        }

        if (null == repositoriesDescription) {
            return null;
        }

        final JSONArray repositories = repositoriesDescription.optJSONArray("repositories");
        for (int i = 0; i < repositories.length(); i++) {
            final JSONObject repository = repositories.optJSONObject(i);
            if (repositoryName.equals(repository.optString("name"))) {
                return repository;
            }
        }

        throw new RuntimeException("Not found the repository [name=" + repositoryName + "] definition, please define it in repositories.json");
    }

    /**
     * Gets the keys definition of an repository specified by the given repository name.
     *
     * @param repositoryName the given repository name (maybe with table name prefix)
     * @return keys definition, returns {@code null} if not found
     */
    public static JSONArray getRepositoryKeysDef(final String repositoryName) {
        if (StringUtils.isBlank(repositoryName)) {
            return null;
        }

        if (null == repositoriesDescription) {
            return null;
        }

        final JSONArray repositories = repositoriesDescription.optJSONArray("repositories");
        for (int i = 0; i < repositories.length(); i++) {
            final JSONObject repository = repositories.optJSONObject(i);
            if (repositoryName.equals(repository.optString("name"))) {
                return repository.optJSONArray("keys");
            }
        }

        throw new RuntimeException("Not found the repository [name=" + repositoryName + "] definition, please define it in repositories.json");
    }

    /**
     * Gets a repository with the specified repository name.
     *
     * @param repositoryName the specified repository name
     * @return repository, returns {@code null} if not found
     */
    public static Repository getRepository(final String repositoryName) {
        return REPOS_HOLDER.get(repositoryName);
    }

    /**
     * Adds the specified repository.
     *
     * @param repository the specified repository
     */
    public static void addRepository(final Repository repository) {
        REPOS_HOLDER.put(repository.getName(), repository);
    }

    /**
     * Loads repository description.
     */
    private static void loadRepositoryDescription() {
        LOGGER.log(Level.INFO, "Loading repository description....");

        final InputStream inputStream = AbstractRepository.class.getResourceAsStream("/repository.json");

        if (null == inputStream) {
            LOGGER.log(Level.INFO, "Not found repository description [repository.json] file under classpath");
            return;
        }

        LOGGER.log(Level.INFO, "Parsing repository description....");

        try {
            final String description = IOUtils.toString(inputStream, "UTF-8");

            LOGGER.log(Level.DEBUG, "{0}{1}", new Object[]{Strings.LINE_SEPARATOR, description});

            repositoriesDescription = new JSONObject(description);

            // Repository name prefix
            final String tableNamePrefix = StringUtils.isNotBlank(Latkes.getLocalProperty("jdbc.tablePrefix"))
                    ? Latkes.getLocalProperty("jdbc.tablePrefix") + "_"
                    : "";

            final JSONArray repositories = repositoriesDescription.optJSONArray("repositories");

            for (int i = 0; i < repositories.length(); i++) {
                final JSONObject repository = repositories.optJSONObject(i);

                repository.put("name", tableNamePrefix + repository.optString("name"));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Parses repository description failed", e);
        } finally {
            try {
                inputStream.close();
            } catch (final IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Private constructor.
     */
    private Repositories() {
    }
}
