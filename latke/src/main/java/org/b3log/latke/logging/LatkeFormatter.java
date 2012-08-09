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
package org.b3log.latke.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * A {@link Formatter} that may be customised in a {@code logging.properties}
 * file. The syntax of the property
 * {@code com.thinktankmaths.logging.TerseFormatter.format}
 * specifies the output. A newline will be appended to the string and the
 * following special characters will be expanded (case sensitive):-
 *
 * <ul>
 *   <li>{@code %m} - message</li>
 *   <li>{@code %L} - log level</li>
 *   <li>{@code %n} - name of the logger</li>
 *   <li>{@code %t} - a timestamp (in ISO-8601 "yyyy-MM-dd HH:mm:ss Z" format)</li>
 *   <li>{@code %M} - source method name (if available, otherwise "?")</li>
 *   <li>{@code %c} - source class name (if available, otherwise "?")</li>
 *   <li>{@code %C} - source simple class name (if available, otherwise "?")</li>
 *   <li>{@code %T} - thread ID</li>
 *   <li>{@code %ln} - line number</li>
 * </ul>
 *
 * The default format is {@value #DEFAULT_FORMAT}. Curly brace characters are not
 * allowed.
 *
 * <p>
 *   See the post <a href="http://javablog.co.uk/2008/07/12/logging-with-javautillogging/">
 *   Logging with `java.util.logging`</a> for more details.
 * </p>
 *
 * <p>
 * This customized logging formatter tested with Sun JDK, others JDK
 * implementation may not work fine.
 * </p>
 *
 * @author Samuel Halliday
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 2, 2010
 */
public final class LatkeFormatter extends Formatter {

    /**
     * Default format.
     */
    private static final String DEFAULT_FORMAT = "%L: %m [%c.%M %t]";
    /**
     * Message format.
     */
    private final MessageFormat messageFormat;
    /**
     * Simple date format, (yyyy-MM-dd HH:mm:ss Z).
     */
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    /**
     * Index of %m.
     */
    private static final int INDEX_MESSAGE = 1;
    /**
     * Index of %L.
     */
    private static final int INDEX_LEVEL = 0;
    /**
     * Index of %n.
     */
    private static final int INDEX_LOGGER_NAME = 6;
    /**
     * Index of %t.
     */
    private static final int INDEX_TIME = 3;
    /**
     * Index of %M.
     */
    private static final int INDEX_METHOD_NAME = 2;
    /**
     * Index of %c.
     */
    private static final int INDEX_CLASS_NAME = 4;
    /**
     * Index of %C.
     */
    private static final int INDEX_SIMPLE_CLASS_NAME = 7;
    /**
     * Index of %T.
     */
    private static final int INDEX_THREAD_ID = 5;
    /**
     * Index of %ln.
     */
    private static final int INDEX_LINE_NUM = 8;
    /**
     * Length of arguments.
     */
    private static final int ARGS_LENGTH = 9;

    /**
     * Public default constructor.
     */
    public LatkeFormatter() {
        // load the format from logging.properties
        final String propName = getClass().getName() + ".format";
        String format = LogManager.getLogManager().getProperty(propName);
        if (format == null || format.trim().length() == 0) {
            format = DEFAULT_FORMAT;
        }
        if (format.contains("{") || format.contains("}")) {
            throw new IllegalArgumentException("curly braces not allowed");
        }

        // convert it into the MessageFormat form
        format = format.replace("%L", "{0}").replace("%m", "{1}").replace("%M", "{2}").
                replace("%t", "{3}").replace("%c", "{4}").replace("%T", "{5}").
                replace("%n", "{6}").replace("%C", "{7}").replace("%ln", "{8}")
                 + System.getProperty("line.separator");

        messageFormat = new MessageFormat(format);
    }

    @Override
    public String format(final LogRecord record) {
        final String[] arguments = new String[ARGS_LENGTH];

        // %L
        arguments[INDEX_LEVEL] = record.getLevel().toString();
        arguments[INDEX_MESSAGE] = record.getMessage();
        // sometimes the message is empty, but there is a throwable
        if (null == arguments[INDEX_MESSAGE]
            || 0 == arguments[INDEX_MESSAGE].length()) {
            final Throwable thrown = record.getThrown();
            if (null != thrown) {
                arguments[INDEX_MESSAGE] = thrown.getMessage();
            }
        }

        // %m
        arguments[INDEX_MESSAGE] = formatMessage(record);

        // %M
        if (null != record.getSourceMethodName()) {
            arguments[INDEX_METHOD_NAME] = record.getSourceMethodName();
        } else {
            arguments[INDEX_METHOD_NAME] = "?";
        }

        // %t
        final Date date = new Date(record.getMillis());
        synchronized (dateFormat) {
            arguments[INDEX_TIME] = dateFormat.format(date);
        }

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int lastCallDepthInLoggerJavaFile = 5;
        int currentCallerDepth = lastCallDepthInLoggerJavaFile;
        String className = null;

        for (int i = currentCallerDepth; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            String fileName = stackTraceElement.getFileName();

            if ("Jdk14Log.java".equals(fileName)
                || "JDK14LoggerAdapter.java".equals(fileName)
                || "Logger.java".equals(fileName)
                || "DirectJDKLog.java".equals(fileName)) {
                currentCallerDepth = i;

                if (i < stackTrace.length - 1) { // look ahead one
                    stackTraceElement = stackTrace[i + 1];
                    fileName = stackTraceElement.getFileName();
                    stackTraceElement.getClassName();

                    if (!"Jdk14Log.java".equals(fileName)
                        && !"JDK14LoggerAdapter.java".equals(fileName)
                        && !"Logger.java".equals(fileName)
                        && !"DirectJDKLog.java".equals(fileName)) {
                        currentCallerDepth++;
                        className = stackTraceElement.getClassName();
                        break;
                    }
                }
            }
        }

        // %c
        if (null == className) {
            if (null != record.getSourceClassName()) {
                arguments[INDEX_CLASS_NAME] = record.getSourceClassName();
            } else {
                arguments[INDEX_CLASS_NAME] = "?";
            }
        } else {
            arguments[INDEX_CLASS_NAME] = className;
        }

        // %T
        arguments[INDEX_THREAD_ID] = Integer.valueOf(record.getThreadID()).
                toString();
        // %n
        arguments[INDEX_LOGGER_NAME] = record.getLoggerName();

        // %C
        final int start = arguments[INDEX_CLASS_NAME].lastIndexOf(".") + 1;
        if (start > 0 && start < arguments[INDEX_CLASS_NAME].length()) {
            arguments[INDEX_SIMPLE_CLASS_NAME] = arguments[INDEX_CLASS_NAME].substring(start);
        } else {
            arguments[INDEX_SIMPLE_CLASS_NAME] = arguments[INDEX_CLASS_NAME];
        }

        final int lineNumber =
                stackTrace[currentCallerDepth].getLineNumber();
        // %ln
        arguments[INDEX_LINE_NUM] = Integer.toString(lineNumber);

        synchronized (messageFormat) {
            final StringBuilder stringBuilder = new StringBuilder(
                    messageFormat.format(arguments));
            if (record.getThrown() != null) {
                try {
                    final StringWriter stringWriter = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(stringWriter);
                    record.getThrown().printStackTrace(printWriter);
                    printWriter.close();
                    stringBuilder.append(stringWriter.toString());
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return stringBuilder.toString();
        }
    }
}
