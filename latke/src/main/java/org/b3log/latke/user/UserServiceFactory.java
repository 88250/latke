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
package org.b3log.latke.user;

import freemarker.log.Logger;
import org.b3log.latke.Latkes;

/**
 * User service factory.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 29, 2011
 */
@SuppressWarnings("unchecked")
public final class UserServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserServiceFactory.class.getName());
    /**
     * User service.
     */
    private static final UserService USER_SERVICE;

    static {
        LOGGER.info("Constructing User Service....");

        try {
            Class<UserService> serviceClass = null;

            switch (Latkes.getRuntime("userService")) {
                case GAE:
                    serviceClass = (Class<UserService>) Class.forName("org.b3log.latke.user.gae.GAEUserService");
                    USER_SERVICE = serviceClass.newInstance();
                    break;
                case LOCAL:
                    serviceClass = (Class<UserService>) Class.forName("org.b3log.latke.user.local.LocalUserService");
                    USER_SERVICE = serviceClass.newInstance();
                    break;
                default:
                    throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize User Service!", e);
        }

        LOGGER.info("Constructed User Service");
    }

    /**
     * Gets user service (always be an instance of 
     * {@link org.b3log.latke.user.local.LocalUserService}).
     * 
     * @return user service
     */
    public static UserService getUserService() {
        return USER_SERVICE;
    }

    /**
     * Private default constructor.
     */
    private UserServiceFactory() {
    }
}
