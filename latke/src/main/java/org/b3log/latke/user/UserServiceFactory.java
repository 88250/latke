/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.user;

import org.b3log.latke.logging.Logger;

/**
 * User service factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.4, Jul 5, 2017
 */
public final class UserServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserServiceFactory.class);

    /**
     * User service.
     */
    private static final UserService USER_SERVICE;

    static {
        LOGGER.info("Constructing user service....");

        try {
            final Class<UserService> serviceClass = (Class<UserService>) Class.forName(
                    "org.b3log.latke.user.local.LocalUserService");
            USER_SERVICE = serviceClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize user service!", e);
        }

        LOGGER.info("Constructed user service");
    }

    /**
     * Private default constructor.
     */
    private UserServiceFactory() {
    }

    /**
     * Gets user service (always be an instance of {@link org.b3log.latke.user.local.LocalUserService}).
     *
     * @return user service
     */
    public static UserService getUserService() {
        return USER_SERVICE;
    }
}
