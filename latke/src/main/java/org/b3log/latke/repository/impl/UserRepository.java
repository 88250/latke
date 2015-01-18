/*
 * Copyright (c) 2009-2015, b3log.org
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


import javax.inject.Named;
import javax.inject.Singleton;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jun 27, 2012
 */
@Named("LakteBuiltInUserRepository")
@Singleton
public class UserRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    /**
     * Public constructor.
     */
    public UserRepository() {
        super("LakteBuiltInUserRepository");
    }

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
            LOGGER.log(Level.ERROR, e.getMessage(), e);

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
            LOGGER.log(Level.ERROR, e.getMessage(), e);

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
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            throw new RepositoryException(e);
        }
    }
}
