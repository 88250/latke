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
package org.b3log.latke.user.local;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;
import org.b3log.latke.util.Sessions;
import org.json.JSONObject;

/**
 * Local user service.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, May 4, 2012
 */
public final class LocalUserService implements UserService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalUserService.class.getName());

    @Override
    public GeneralUser getCurrentUser(final HttpServletRequest request) {
        final JSONObject currentUser = Sessions.currentUser(request);
        if (null == currentUser) {
            return null;
        }

        final GeneralUser ret = new GeneralUser();
        ret.setEmail(currentUser.optString(User.USER_EMAIL));
        ret.setId(currentUser.optString(Keys.OBJECT_ID));
        ret.setNickname(currentUser.optString(User.USER_NAME));

        return ret;
    }

    @Override
    public boolean isUserLoggedIn(final HttpServletRequest request) {
        return null != Sessions.currentUser(request);
    }

    @Override
    public boolean isUserAdmin(final HttpServletRequest request) {
        final JSONObject currentUser = Sessions.currentUser(request);

        if (null == currentUser) {
            return false;
        }

        return Role.ADMIN_ROLE.equals(currentUser.optString(User.USER_ROLE));
    }

    @Override
    public String createLoginURL(final String destinationURL) {
        String to = Latkes.getServePath();

        try {
            to = URLEncoder.encode(to + destinationURL, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "URL encode[string={0}]", destinationURL);
        }

        return Latkes.getContextPath() + "/login?goto=" + to;
    }

    @Override
    public String createLogoutURL(final String destinationURL) {
        String to = Latkes.getServePath();

        try {
            to = URLEncoder.encode(to + destinationURL, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "URL encode[string={0}]", destinationURL);
        }

        return Latkes.getContextPath() + "/logout?goto=" + to;
    }
}
