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
 * This class defines all role/group model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Jul 23, 2010
 */
public final class Role {

    /**
     * Role.
     */
    public static final String ROLE = "role";

    /**
     * Default role.
     */
    public static final String DEFAULT_ROLE = "defaultRole";

    /**
     * Visitor role.
     */
    public static final String VISITOR_ROLE = "visitorRole";

    /**
     * Administrator role.
     */
    public static final String ADMIN_ROLE = "adminRole";

    /**
     * Roles.
     */
    public static final String ROLES = "roles";

    /**
     * Role id.
     */
    public static final String ROLE_ID = "roleId";

    /**
     * Role name.
     */
    public static final String ROLE_NAME = "roleName";

    /**
     * Update time of this role.
     */
    public static final String ROLE_UPDATE_TIME = "roleUpdateTime";

    /**
     * Role permission set.
     */
    public static final String ROLE_PERMISSION_SET = "rolePermissionSet";

    /**
     * Role permission set relation role id.
     */
    public static final String ROLE_PERMISSION_SET_RELATION_ROLE_ID = "rolePermissionSetRelationRoleId";

    /**
     * Role user id.
     */
    public static final String ROLE_USER_ID = "roleUserId";

    /**
     * Private constructor.
     */
    private Role() {
    }
}
