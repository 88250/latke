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
package org.b3log.latke.ioc;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.repository.jdbc.JdbcTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Javassist method handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.0, May 10, 2020
 * @since 2.4.18
 */
final class JavassistMethodHandler implements MethodHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(JavassistMethodHandler.class);

    /**
     * Bean manager.
     */
    private final BeanManager beanManager;

    /**
     * Call count in the current thread.
     */
    private static final ThreadLocal<AtomicInteger> CALLS = new ThreadLocal<>();

    /**
     * Method filter.
     */
    private final MethodFilter methodFilter = method -> {
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
            calls = new AtomicInteger(0);
            CALLS.set(calls);
        }
        calls.incrementAndGet();

        // Invocation with transaction handle
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
            transaction = JdbcRepository.TX.get();
            if (null != transaction && transaction.isActive()) {
                transaction.rollback();
            }

            throw e.getTargetException();
        } finally {
            if (0 == calls.decrementAndGet()) {
                CALLS.remove();
                final Connection connection = JdbcRepository.CONN.get();
                if (null != connection) {
                    connection.close();
                    JdbcRepository.CONN.remove();
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
