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


import java.lang.reflect.Method;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.repository.impl.UserRepositoryImpl;


/**
 * Javassist method handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Sep 2, 2013
 */
public final class JavassistMethodHandler implements MethodHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavassistMethodHandler.class.getName());

    /**
     * Method filter.
     */
    private MethodFilter methodFilter = new MethodFilter() {
        @Override
        public boolean isHandled(final Method method) {
            return method.isAnnotationPresent(Transactional.class);
        }
    };

    @Override
    public Object invoke(final Object proxy, final Method method, final Method proceed, final Object[] params) {
        LOGGER.trace("Processing invocation: " + method.toString());

        final boolean withTransaction = method.isAnnotationPresent(Transactional.class);
        final boolean alreadyInTransaction = UserRepositoryImpl.getInstance().hasTransactionBegun();

        Transaction transaction = null;
        
        // Transaction Propagation: REQUIRED (Support a current transaction, create a new one if none exists)
        if (withTransaction && !alreadyInTransaction) {
            transaction = UserRepositoryImpl.getInstance().beginTransaction();
        }

        Object ret = null;

        try {
            ret = proceed.invoke(proxy, params);

            if (withTransaction && !alreadyInTransaction) {
                transaction.commit();
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Invoke [" + method.toString() + "] failed", e);

            if (withTransaction && !alreadyInTransaction) {
                if (null != transaction && transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }

        return ret;
    }

    /**
     * Gets the method filter.
     * 
     * @return method filter
     */
    public MethodFilter getMethodFilter() {
        return methodFilter;
    }
}
