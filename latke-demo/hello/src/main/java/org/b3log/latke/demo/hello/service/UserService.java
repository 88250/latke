/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.demo.hello.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.b3log.latke.demo.hello.repository.UserRepository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.json.JSONObject;

/**
 * User service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 2, 2013
 */
@Service
public final class UserService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    public void saveUser(final String name, final int age) {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            final JSONObject user = new JSONObject();
            user.put("name", name);
            user.put("age", age);

            final String userId = userRepository.add(user);

            transaction.commit();

            LOGGER.log(Level.INFO, "Saves a user successfully [userId={0}]", userId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Can not save user[name=" + name + ']', e);
        }
    }
}
