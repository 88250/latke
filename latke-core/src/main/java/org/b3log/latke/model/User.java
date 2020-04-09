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
package org.b3log.latke.model;


/**
 * This class defines all user model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.8, Feb 7, 2011
 */
public final class User {

    /**
     * User.
     */
    public static final String USER = "user";

    /**
     * Users.
     */
    public static final String USERS = "users";

    /**
     * Key of user name.
     */
    public static final String USER_NAME = "userName";

    /**
     * Key of user email.
     */
    public static final String USER_EMAIL = "userEmail";

    /**
     * Key of user URL.
     */
    public static final String USER_URL = "userURL";

    /**
     * Key of user password.
     */
    public static final String USER_PASSWORD = "userPassword";

    /**
     * Key of user new password.
     */
    public static final String USER_NEW_PASSWORD = "userNewPassword";

    /**
     * Key of update time of this user.
     */
    public static final String USER_UPDATE_TIME = "userUpdateTime";

    /**
     * Key of user role.
     */
    public static final String USER_ROLE = "userRole";
    // Relations ///////////////////////////////////////////////////////////////
    /**
     * {@linkplain Role#ROLE_ID Role id} of this user.
     */
    public static final String USER_ROLE_ID = "userRoleId";
    // End of Relations ////////////////////////////////////////////////////////

    /**
     * Private constructor.
     */
    private User() {
    }
}
