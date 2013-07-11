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
package org.b3log.latke.ioc.bean;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.repository.impl.UserRepositoryImpl;


/**
 * JDK invocation handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 8, 2013
 */
public final class JDKInvocationHandler implements InvocationHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JDKInvocationHandler.class.getName());

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        LOGGER.trace("Processing invocation: " + method.toString());

        final boolean withTransaction = method.isAnnotationPresent(Transactional.class);

        Transaction transaction = null;

        if (withTransaction) {
            transaction = UserRepositoryImpl.getInstance().beginTransaction();
        }

        Object ret = null;

        try {
            ret = method.invoke(proxy, args);

            if (withTransaction) {
                transaction.commit();
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Invoke [" + method.toString() + "] failed", e);

            if (withTransaction) {
                if (null != transaction && transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }

        return ret;
    }
}
