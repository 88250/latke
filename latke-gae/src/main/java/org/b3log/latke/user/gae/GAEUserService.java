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
package org.b3log.latke.user.gae;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;

/**
 * Google App Engine user service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Sep 27, 2011
 */
public final class GAEUserService implements UserService {

    /**
     * URL fetch service.
     */
    private static final com.google.appengine.api.users.UserService USER_SERVICE = UserServiceFactory.getUserService();

    @Override
    public GeneralUser getCurrentUser(final HttpServletRequest request) {
        final User currentUser = USER_SERVICE.getCurrentUser();

        if (null == currentUser) {
            return null;
        }

        return toGeneralUser(currentUser);
    }

    @Override
    public boolean isUserLoggedIn(final HttpServletRequest request) {
        return USER_SERVICE.isUserLoggedIn();
    }

    @Override
    public boolean isUserAdmin(final HttpServletRequest request) {
        return USER_SERVICE.isUserAdmin();
    }

    @Override
    public String createLoginURL(final String destinationURL) {
        return USER_SERVICE.createLoginURL(destinationURL);
    }

    @Override
    public String createLogoutURL(final String destinationURL) {
        return USER_SERVICE.createLogoutURL(destinationURL);
    }

    /**
     * Converts the specified Google App Engine user to a general user.
     * 
     * @param user the specified Google App Engine user
     * @return general user
     */
    private static GeneralUser toGeneralUser(final User user) {
        final GeneralUser ret = new GeneralUser();

        ret.setEmail(user.getEmail());
        ret.setId(user.getUserId());
        ret.setNickname(user.getNickname());

        return ret;
    }
}
