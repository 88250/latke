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
package org.b3log.latke.logging;


import static org.b3log.latke.logging.Level.DEBUG;
import static org.b3log.latke.logging.Level.ERROR;
import static org.b3log.latke.logging.Level.INFO;
import static org.b3log.latke.logging.Level.TRACE;
import static org.b3log.latke.logging.Level.WARN;
import org.slf4j.LoggerFactory;


/**
 * Latke logger.
 * 
 * <p>
 * The logging will delegate to slf4j.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 27, 2013
 * @see Level
 */
public final class Logger {

    /**
     * SLF4j logger.
     */
    private org.slf4j.Logger proxy;

    /**
     * Constructs a logger with the specified class name.
     * 
     * @param className the specified class name
     */
    private Logger(final String className) {
        proxy = LoggerFactory.getLogger(className);
    }

    /**
     * Gets a logger with the specified class name.
     * 
     * @param className the specified class name
     * @return logger
     */
    public static Logger getLogger(final String className) {
        return new Logger(className);
    }

    /**
     * Gets a logger with the specified class.
     * 
     * @param clazz the specified class
     * @return logger
     */
    public static Logger getLogger(final Class<?> clazz) {
        return new Logger(clazz.getName());
    }

    /**
     * Checks if a message of the given level would actually be logged by this logger.
     *
     * @param level the given level
     * @return {@code true} if it could, returns {@code false} if it couldn't
     */
    public boolean isLoggable(final Level level) {
        switch (level) {
        case TRACE:
            return proxy.isTraceEnabled();

        case DEBUG:
            return proxy.isDebugEnabled();

        case INFO:
            return proxy.isInfoEnabled();

        case WARN:
            return proxy.isWarnEnabled();

        case ERROR:
            proxy.isErrorEnabled();

        default:
            throw new IllegalStateException("Logging level [" + level + "] is invalid");
        }
    }

    /**
     * Logs the specified message at the ERROR level.
     * 
     * @param msg the specified message
     */
    public void error(final String msg) {
        if (proxy.isErrorEnabled()) {
            proxy.error(msg);
        }
    }

    /**
     * Logs the specified message at the WARN level.
     * 
     * @param msg the specified message
     */
    public void warn(final String msg) {
        if (proxy.isWarnEnabled()) {
            proxy.warn(msg);
        }
    }

    /**
     * Logs the specified message at the INFO level.
     * 
     * @param msg the specified message
     */
    public void info(final String msg) {
        if (proxy.isInfoEnabled()) {
            proxy.info(msg);
        }
    }

    /**
     * Logs the specified message at the DEBUG level.
     * 
     * @param msg the specified message
     */
    public void debug(final String msg) {
        if (proxy.isDebugEnabled()) {
            proxy.debug(msg);
        }
    }

    /**
     * Logs the specified message at the TRACE level.
     * 
     * @param msg the specified message
     */
    public void trace(final String msg) {
        if (proxy.isTraceEnabled()) {
            proxy.trace(msg);
        }
    }

    /**
     * Logs the specified message with the specified logging level and throwable.
     * 
     * @param level the specified logging level
     * @param msg the specified message
     * @param throwable the specified throwable
     */
    public void log(final Level level, final String msg, final Throwable throwable) {
        switch (level) {
        case ERROR:
            if (proxy.isErrorEnabled()) {
                proxy.error(msg, throwable);
            }

            break;

        case WARN:
            if (proxy.isWarnEnabled()) {
                proxy.warn(msg, throwable);
            }

            break;

        case INFO:
            if (proxy.isInfoEnabled()) {
                proxy.info(msg, throwable);
            }

            break;

        case DEBUG:
            if (proxy.isDebugEnabled()) {
                proxy.debug(msg, throwable);
            }

            break;

        case TRACE:
            if (proxy.isTraceEnabled()) {
                proxy.trace(msg, throwable);
            }

            break;

        default:
            throw new IllegalStateException("Logging level [" + level + "] is invalid");
        }
    }

    /**
     * Logs the specified message with the specified logging level and arguments.
     * 
     * @param level the specified logging level
     * @param msg the specified message
     * @param args the specified arguments 
     */
    public void log(final Level level, final String msg, final Object... args) {
        String message = msg;

        if (null != args && 0 < args.length) {
            // Is it a java.text style format?
            // Ideally we could match with Pattern.compile("\\{\\d").matcher(format).find())
            // However the cost is 14% higher, so we cheaply check for 1 of the first 4 parameters
            if (msg.indexOf("{0") >= 0 || msg.indexOf("{1") >= 0 || msg.indexOf("{2") >= 0 || msg.indexOf("{3") >= 0) {
                message = java.text.MessageFormat.format(msg, args);
            }
        }

        switch (level) {
        case ERROR:
            if (proxy.isErrorEnabled()) {
                proxy.error(message);
            }

            break;

        case WARN:
            if (proxy.isWarnEnabled()) {
                proxy.warn(message);
            }

            break;

        case INFO:
            if (proxy.isInfoEnabled()) {
                proxy.info(message);
            }

            break;

        case DEBUG:
            if (proxy.isDebugEnabled()) {
                proxy.debug(message);
            }

            break;

        case TRACE:
            if (proxy.isTraceEnabled()) {
                proxy.trace(message);
            }

            break;

        default:
            throw new IllegalStateException("Logging level [" + level + "] is invalid");
        }
    }

    /**
     * Determines whether this logger enabled for ERROR level.
     * 
     * @return {@code true} if it is enabled for ERROR level, return {@code false} otherwise
     */
    public boolean isErrorEnabled() {
        return proxy.isErrorEnabled();
    }

    /**
     * Determines whether this logger enabled for WARN level.
     * 
     * @return {@code true} if it is enabled for WARN level, return {@code false} otherwise
     */
    public boolean isWarnEnabled() {
        return proxy.isWarnEnabled();
    }

    /**
     * Determines whether this logger enabled for INFO level.
     * 
     * @return {@code true} if it is enabled for INFO level, return {@code false} otherwise
     */
    public boolean isInfoEnabled() {
        return proxy.isInfoEnabled();
    }

    /**
     * Determines whether this logger enabled for DEBUG level.
     * 
     * @return {@code true} if it is enabled for DEBUG level, return {@code false} otherwise
     */
    public boolean isDebugEnabled() {
        return proxy.isDebugEnabled();
    }

    /**
     * Determines whether this logger enabled for TRACE level.
     * 
     * @return {@code true} if it is enabled for TRACE level, return {@code false} otherwise
     */
    public boolean isTraceEnabled() {
        return proxy.isTraceEnabled();
    }
}
