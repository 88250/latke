/*
 * Copyright (c) 2009-present, b3log.org
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

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Latke logger.
 *
 * <p>
 * The logging will delegate to slf4j.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.1, Jan 4, 2016
 * @see Level
 */
public final class Logger {

    /**
     * The fully qualified class name.
     */
    private static final String FQCN = Logger.class.getName();

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
     * Logs the specified message at the ERROR level.
     *
     * @param msg the specified message
     */
    public void error(final String msg) {
        if (proxy.isErrorEnabled()) {
            if (proxy instanceof LocationAwareLogger) {
                ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
            } else {
                proxy.error(msg);
            }
        }
    }

    /**
     * Logs the specified message at the WARN level.
     *
     * @param msg the specified message
     */
    public void warn(final String msg) {
        if (proxy.isWarnEnabled()) {
            if (proxy instanceof LocationAwareLogger) {
                ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, null);
            } else {
                proxy.warn(msg);
            }
        }
    }

    /**
     * Logs the specified message at the INFO level.
     *
     * @param msg the specified message
     */
    public void info(final String msg) {
        if (proxy.isInfoEnabled()) {
            if (proxy instanceof LocationAwareLogger) {
                ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
            } else {
                proxy.info(msg);
            }
        }
    }

    /**
     * Logs the specified message at the DEBUG level.
     *
     * @param msg the specified message
     */
    public void debug(final String msg) {
        if (proxy.isDebugEnabled()) {
            if (proxy instanceof LocationAwareLogger) {
                ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
            } else {
                proxy.debug(msg);
            }
        }
    }

    /**
     * Logs the specified message at the TRACE level.
     *
     * @param msg the specified message
     */
    public void trace(final String msg) {
        if (proxy.isTraceEnabled()) {
            if (proxy instanceof LocationAwareLogger) {
                ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, null);
            } else {
                proxy.trace(msg);
            }
        }
    }

    /**
     * Logs the specified message with the specified logging level and throwable.
     *
     * @param level     the specified logging level
     * @param msg       the specified message
     * @param throwable the specified throwable
     */
    public void log(final Level level, final String msg, final Throwable throwable) {
        switch (level) {
            case ERROR:
                if (proxy.isErrorEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, throwable);
                    } else {
                        proxy.error(msg, throwable);
                    }
                }

                break;
            case WARN:
                if (proxy.isWarnEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, throwable);
                    } else {
                        proxy.warn(msg, throwable);
                    }
                }

                break;
            case INFO:
                if (proxy.isInfoEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, throwable);
                    } else {
                        proxy.info(msg, throwable);
                    }
                }

                break;
            case DEBUG:
                if (proxy.isDebugEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, throwable);
                    } else {
                        proxy.debug(msg, throwable);
                    }
                }

                break;
            case TRACE:
                if (proxy.isTraceEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, throwable);
                    } else {
                        proxy.trace(msg, throwable);
                    }
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
     * @param msg   the specified message
     * @param args  the specified arguments
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
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
                    } else {
                        proxy.error(message);
                    }
                }

                break;
            case WARN:
                if (proxy.isWarnEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
                    } else {
                        proxy.warn(message);
                    }
                }

                break;
            case INFO:
                if (proxy.isInfoEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.INFO_INT, message, null, null);
                    } else {
                        proxy.info(message);
                    }
                }

                break;
            case DEBUG:
                if (proxy.isDebugEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
                    } else {
                        proxy.debug(message);
                    }
                }

                break;
            case TRACE:
                if (proxy.isTraceEnabled()) {
                    if (proxy instanceof LocationAwareLogger) {
                        ((LocationAwareLogger) proxy).log(null, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);
                    } else {
                        proxy.trace(message);
                    }
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
        if (proxy instanceof LocationAwareLogger) {
            return ((LocationAwareLogger) proxy).isErrorEnabled();
        } else {
            return proxy.isErrorEnabled();
        }
    }

    /**
     * Determines whether this logger enabled for WARN level.
     *
     * @return {@code true} if it is enabled for WARN level, return {@code false} otherwise
     */
    public boolean isWarnEnabled() {
        if (proxy instanceof LocationAwareLogger) {
            return ((LocationAwareLogger) proxy).isWarnEnabled();
        } else {
            return proxy.isWarnEnabled();
        }
    }

    /**
     * Determines whether this logger enabled for INFO level.
     *
     * @return {@code true} if it is enabled for INFO level, return {@code false} otherwise
     */
    public boolean isInfoEnabled() {
        if (proxy instanceof LocationAwareLogger) {
            return ((LocationAwareLogger) proxy).isInfoEnabled();
        } else {
            return proxy.isInfoEnabled();
        }
    }

    /**
     * Determines whether this logger enabled for DEBUG level.
     *
     * @return {@code true} if it is enabled for DEBUG level, return {@code false} otherwise
     */
    public boolean isDebugEnabled() {
        if (proxy instanceof LocationAwareLogger) {
            return ((LocationAwareLogger) proxy).isDebugEnabled();
        } else {
            return proxy.isDebugEnabled();
        }
    }

    /**
     * Determines whether this logger enabled for TRACE level.
     *
     * @return {@code true} if it is enabled for TRACE level, return {@code false} otherwise
     */
    public boolean isTraceEnabled() {
        if (proxy instanceof LocationAwareLogger) {
            return ((LocationAwareLogger) proxy).isTraceEnabled();
        } else {
            return proxy.isTraceEnabled();
        }
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
                return isTraceEnabled();
            case DEBUG:
                return isDebugEnabled();
            case INFO:
                return isInfoEnabled();
            case WARN:
                return isWarnEnabled();
            case ERROR:
                return isErrorEnabled();
            default:
                throw new IllegalStateException("Logging level [" + level + "] is invalid");
        }
    }
}
