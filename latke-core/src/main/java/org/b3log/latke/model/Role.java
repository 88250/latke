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
