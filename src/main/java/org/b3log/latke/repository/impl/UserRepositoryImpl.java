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
package org.b3log.latke.repository.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User repository implementation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jun 27, 2012
 */
public final class UserRepositoryImpl extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class.getName());

    /**
     * Gets user by the specified email.
     * 
     * @param email the specified email
     * @return user, returns {@code null} if not found
     */
    public JSONObject getByEmail(final String email) {
        final Query query = new Query();
        query.setFilter(new PropertyFilter(User.USER_EMAIL, FilterOperator.EQUAL, email.toLowerCase().trim()));

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    /**
     * Gets the administrator.
     * 
     * @return administrator, returns {@code null} if not found
     */
    public JSONObject getAdmin() {
        final Query query = new Query();
        query.setFilter(new PropertyFilter(User.USER_ROLE, FilterOperator.EQUAL, Role.ADMIN_ROLE));

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    /**
     * Determines the specified email is administrator's or not.
     * 
     * @param email the specified email
     * @return {@code true} if it is, returns {@code false} otherwise
     * @throws RepositoryException repository exception
     */
    public boolean isAdminEmail(final String email) throws RepositoryException {
        final JSONObject user = getByEmail(email);

        if (null == user) {
            return false;
        }

        try {
            return Role.ADMIN_ROLE.equals(user.getString(User.USER_ROLE));
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new RepositoryException(e);
        }
    }

    /**
     * Gets the {@link UserRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static UserRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private UserRepositoryImpl(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final UserRepositoryImpl SINGLETON =
                new UserRepositoryImpl(User.USER);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
