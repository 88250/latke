/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.2.2, Aug 13, 2017
 */
public final class Sessions {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Sessions.class);

    /**
     * Cookie expiry in 30 days.
     */
    private static final int COOKIE_EXPIRY = 60 * 60 * 24 * 30;

    /**
     * Cookie name.
     */
    public static final String COOKIE_NAME;

    /**
     * Cookie secret.
     */
    public static final String COOKIE_SECRET;

    /**
     * Cookie HTTP only.
     */
    public static final boolean COOKIE_HTTP_ONLY;

    static {
        String cookieNameConf = Latkes.getLatkeProperty("cookieName");
        if (StringUtils.isBlank(cookieNameConf)) {
            cookieNameConf = "b3log-latke";
        }
        COOKIE_NAME = cookieNameConf;

        String cookieSecret = Latkes.getLatkeProperty("cookieSecret");
        if (StringUtils.isBlank(cookieSecret)) {
            cookieSecret = "Beyond";
        }
        COOKIE_SECRET = cookieSecret;

        COOKIE_HTTP_ONLY = Boolean.valueOf(Latkes.getLocalProperty("cookieHttpOnly"));
    }

    /**
     * Private default constructor.
     */
    private Sessions() {
    }

    /**
     * Logins the specified user from the specified request.
     * <p>
     * If no session of the specified request, do nothing.
     * </p>
     *
     * @param request  the specified request
     * @param response the specified response
     * @param user     the specified user, for example,
     *                 {
     *                 "userEmail": "",
     *                 "userPassword": ""
     *                 }
     */
    public static void login(final HttpServletRequest request, final HttpServletResponse response, final JSONObject user) {
        final HttpSession session = request.getSession(false);
        if (null == session) {
            LOGGER.warn("The session is null");
            return;
        }

        session.setAttribute(User.USER, user);

        try {
            final JSONObject cookieJSONObject = new JSONObject();
            cookieJSONObject.put(Keys.OBJECT_ID, user.optString(Keys.OBJECT_ID));
            cookieJSONObject.put(User.USER_PASSWORD, user.optString(User.USER_PASSWORD));

            final String random = RandomStringUtils.random(16);
            cookieJSONObject.put(Keys.TOKEN, user.optString(User.USER_PASSWORD) + ":" + random);

            final String cookieValue = Crypts.encryptByAES(cookieJSONObject.toString(), COOKIE_SECRET);
            final Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_EXPIRY);
            cookie.setHttpOnly(COOKIE_HTTP_ONLY);

            response.addCookie(cookie);
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Can not write cookie", e);
        }
    }

    /**
     * Logouts a user with the specified request.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return {@code true} if succeed, otherwise returns {@code false}
     */
    public static boolean logout(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession session = request.getSession(false);
        if (null != session) {
            final Cookie cookie = new Cookie(COOKIE_NAME, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);

            session.invalidate();

            return true;
        }

        return false;
    }

    /**
     * Gets the current user with the specified request.
     *
     * @param request the specified request
     * @return the current user, returns {@code null} if not logged in
     */
    public static JSONObject currentUser(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (null != session) {
            return (JSONObject) session.getAttribute(User.USER);
        }

        return null;
    }

    /**
     * Gets the current logged in user password with the specified request.
     *
     * @param request the specified request
     * @return the current user password or {@code null}
     */
    public static String currentUserPwd(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (null != session) {
            final JSONObject user = (JSONObject) session.getAttribute(User.USER);

            return user.optString(User.USER_PASSWORD);
        }

        return null;
    }

    /**
     * Gets the current logged in user name with the specified request.
     *
     * @param request the specified request
     * @return the current user name or {@code null}
     */
    public static String currentUserName(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (null != session) {
            final JSONObject user = (JSONObject) session.getAttribute(User.USER);

            return user.optString(User.USER_NAME);
        }

        return null;
    }

    /**
     * Gets the current logged in user email with the specified request.
     *
     * @param request the specified request
     * @return the current user name or {@code null}
     */
    public static String currentUserEmail(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (null != session) {
            final JSONObject user = (JSONObject) session.getAttribute(User.USER);

            return user.optString(User.USER_EMAIL);
        }

        return null;
    }
}
