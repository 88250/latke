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
package org.b3log.latke.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Call stack utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Oct 31, 2018
 */
public final class Callstacks {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Callstacks.class);

    /**
     * Checks the current method is whether invoked by a caller specified by the given class name and method name.
     *
     * @param className  the given class name
     * @param methodName the given method name, "*" for matching all methods
     * @return {@code true} if it is invoked by the specified caller, returns {@code false} otherwise
     */
    public static boolean isCaller(final String className, final String methodName) {
        final Throwable throwable = new Throwable();
        final StackTraceElement[] stackElements = throwable.getStackTrace();

        if (null == stackElements) {
            LOGGER.log(Level.WARN, "Empty call stack");

            return false;
        }

        final boolean matchAllMethod = "*".equals(methodName);

        for (int i = 1; i < stackElements.length; i++) {
            if (stackElements[i].getClassName().equals(className)) {
                return matchAllMethod || stackElements[i].getMethodName().equals(methodName);
            }
        }

        return false;
    }

    /**
     * Prints call stack with the specified logging level.
     *
     * @param logLevel           the specified logging level
     * @param carePackages       the specified packages to print, for example, ["org.b3log.latke", "org.b3log.solo"], {@code null} to care
     *                           nothing
     * @param exceptablePackages the specified packages to skip, for example, ["com.sun", "java.io", "org.b3log.solo.filter"],
     *                           {@code null} to skip nothing
     */
    public static void printCallstack(final Level logLevel, final String[] carePackages, final String[] exceptablePackages) {
        if (null == logLevel) {
            LOGGER.log(Level.WARN, "Requires parameter [logLevel]");
            return;
        }

        final Throwable throwable = new Throwable();
        final StackTraceElement[] stackElements = throwable.getStackTrace();

        if (null == stackElements) {
            LOGGER.log(Level.WARN, "Empty call stack");
            return;
        }

        final long tId = Thread.currentThread().getId();
        final StringBuilder stackBuilder = new StringBuilder("CallStack [tId=").append(tId).append(Strings.LINE_SEPARATOR);

        for (int i = 1; i < stackElements.length; i++) {
            final String stackElemClassName = stackElements[i].getClassName();

            if (!StringUtils.startsWithAny(stackElemClassName, carePackages)
                    || StringUtils.startsWithAny(stackElemClassName, exceptablePackages)) {
                continue;
            }

            stackBuilder.append("    [className=").append(stackElements[i].getClassName()).append(", fileName=").append(stackElements[i].getFileName()).append(", lineNumber=").append(stackElements[i].getLineNumber()).append(", methodName=").append(stackElements[i].getMethodName()).append(']').append(
                    Strings.LINE_SEPARATOR);
        }
        stackBuilder.append("], full depth [").append(stackElements.length).append("]");

        LOGGER.log(logLevel, stackBuilder.toString());
    }

    /**
     * Private constructor.
     */
    private Callstacks() {
    }
}
