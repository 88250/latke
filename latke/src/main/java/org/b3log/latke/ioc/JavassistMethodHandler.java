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
package org.b3log.latke.ioc;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.intercept.annotation.AfterMethod;
import org.b3log.latke.intercept.annotation.BeforeMethod;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.repository.jdbc.JdbcTransaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Javassist method handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.6, Oct 31, 2018
 * @since 2.4.18
 */
final class JavassistMethodHandler implements MethodHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavassistMethodHandler.class);

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    /**
     * Call count in the current thread.
     */
    private static final ThreadLocal<AtomicInteger> CALLS = new ThreadLocal();

    /**
     * Method filter.
     */
    private MethodFilter methodFilter = method -> {
        final String pkg = method.getDeclaringClass().getPackage().getName();
        if (StringUtils.startsWithAny(pkg, new String[]{"org.b3log.latke", "java.", "javax."})) {
            return false;
        }

        final String name = method.getName();
        return !"invoke".equals(name) &&
                !"beginTransaction".equals(name) && !"hasTransactionBegun".equals(name);
    };

    /**
     * Constructs a method handler with the specified bean manager.
     *
     * @param beanManager the specified bean manager
     */
    JavassistMethodHandler(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Method proceed, final Object[] params) throws Throwable {
        LOGGER.trace("Processing invocation [" + method.toString() + "]");

        AtomicInteger calls = CALLS.get();
        if (null == calls) {
            synchronized (this) {
                if (null == calls) {
                    calls = new AtomicInteger(0);
                    CALLS.set(calls);
                }
            }
        }
        calls.incrementAndGet();

        final Class<?> declaringClass = method.getDeclaringClass();
        final String invokingMethodName = declaringClass.getName() + '#' + method.getName();

        // 1. @BeforeMethod handle
        handleInterceptor(invokingMethodName, params, BeforeMethod.class);

        // 2. Invocation with transaction handle
        final boolean withTransactionalAnno = method.isAnnotationPresent(Transactional.class);
        JdbcTransaction transaction = JdbcRepository.TX.get();
        final boolean alreadyInTransaction = null != transaction;
        final boolean needHandleTrans = withTransactionalAnno && !alreadyInTransaction;

        // Transaction Propagation: REQUIRED (Support a current transaction, create a new one if none exists)
        if (needHandleTrans) {
            try {
                transaction = new JdbcTransaction();
            } catch (final SQLException e) {
                LOGGER.log(Level.ERROR, "Failed to initialize JDBC transaction", e);

                throw new IllegalStateException("Begin a transaction failed");
            }

            JdbcRepository.TX.set(transaction);
        }

        Object ret;
        try {
            ret = proceed.invoke(proxy, params);

            if (needHandleTrans) {
                transaction.commit();
            }
        } catch (final InvocationTargetException e) {
            if (needHandleTrans) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }

            throw e.getTargetException();
        }

        // 3. @AfterMethod handle
        handleInterceptor(invokingMethodName, params, AfterMethod.class);

        if (0 == calls.decrementAndGet()) {
            CALLS.set(null);
            final Connection connection = JdbcRepository.CONN.get();
            if (null != connection) {
                connection.close();
                JdbcRepository.CONN.set(null);
            }
        }

        return ret;
    }

    /**
     * Interceptor handle with the specified invoking method name, invoking method parameters and intercept annotation
     * class.
     *
     * @param invokingMehtodName the specified invoking method name
     * @param params             the specified invoking method parameters
     * @param interceptAnnClass  the specified intercept annotation class
     */
    private void handleInterceptor(final String invokingMehtodName, final Object[] params,
                                   final Class<? extends Annotation> interceptAnnClass) {
        final Set<Interceptor> interceptors = InterceptorHolder.getInterceptors(invokingMehtodName, interceptAnnClass);
        for (final Interceptor interceptor : interceptors) {
            final Method interceptMethod = interceptor.getInterceptMethod();
            final Class<?> interceptMethodClass = interceptMethod.getDeclaringClass();

            try {
                final Object reference = beanManager.getReference(interceptMethodClass);

                interceptMethod.invoke(reference, params);
            } catch (final Exception e) {
                final String errMsg = "Interception[" + interceptor.toString() + "] execute failed";
                LOGGER.log(Level.ERROR, errMsg, e);

                throw new RuntimeException(errMsg);
            }
        }
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
