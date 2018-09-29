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
package org.b3log.latke;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.cache.redis.RedisCache;
import org.b3log.latke.cron.CronService;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.servlet.AbstractServletListener;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Latke framework configuration utility facade.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.8.0.4, Sep 29, 2018
 * @see #initRuntimeEnv()
 * @see #shutdown()
 * @see #getServePath()
 * @see #getStaticServePath()
 */
public final class Latkes {

    /**
     * Executor service.
     */
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Version.
     */
    public static final String VERSION = "2.4.18";

    /**
     * User Agent.
     */
    public static String USER_AGENT = "Latke/" + VERSION + "; +https://github.com/b3log/latke";

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Latkes.class);

    /**
     * Local properties (local.properties).
     */
    private static final Properties LOCAL_PROPS = new Properties();

    /**
     * Latke configurations (latke.properties).
     */
    private static final Properties LATKE_PROPS = new Properties();

    /**
     * Latke remote interfaces configurations (remote.properties).
     */
    private static final Properties REMOTE_PROPS = new Properties();

    /**
     * Locale. Initializes this by {@link #setLocale(java.util.Locale)}.
     */
    private static Locale locale;

    /**
     * Which mode Latke runs in?
     */
    private static RuntimeMode runtimeMode;

    /**
     * Application startup time millisecond.
     */
    private static String startupTimeMillis = String.valueOf(System.currentTimeMillis());

    /**
     * Static resource version.
     */
    private static String staticResourceVersion;

    /**
     * Server scheme.
     */
    private static String serverScheme;

    /**
     * Static server scheme.
     */
    private static String staticServerScheme;

    /**
     * Server host.
     */
    private static String serverHost;

    /**
     * Static server host.
     */
    private static String staticServerHost;

    /**
     * Server port.
     */
    private static String serverPort;

    /**
     * Static server port.
     */
    private static String staticServerPort;

    /**
     * Server. (${serverScheme}://${serverHost}:${serverPort})
     */
    private static String server;

    /**
     * Serve path. (${server}${contextPath})
     */
    private static String servePath;

    /**
     * Static server. (${staticServerScheme}://${staticServerHost}:${staticServerPort})
     */
    private static String staticServer;

    /**
     * Static serve path. (${staticServer}${staticPath})
     */
    private static String staticServePath;

    /**
     * Context path.
     */
    private static String contextPath;

    /**
     * Static path.
     */
    private static String staticPath;

    /**
     * IoC scan path.
     */
    private static String scanPath;

    /**
     * H2 database TCP server.
     * <p>
     * If Latke is using {@link RuntimeDatabase#H2 H2} database and specified newTCPServer=true in local.properties,
     * creates a H2 TCP server and starts it.
     * </p>
     */
    private static org.h2.tools.Server h2;

    static {
        try {
            InputStream resourceAsStream;
            final String latkePropsEnv = System.getenv("LATKE_PROPS");
            if (StringUtils.isNotBlank(latkePropsEnv)) {
                LOGGER.debug("Loading latke.properties from env var [$LATKE_PROPS=" + latkePropsEnv + "]");
                resourceAsStream = new FileInputStream(latkePropsEnv);
            } else {
                LOGGER.debug("Loading latke.properties from classpath [/latke.properties]");
                resourceAsStream = Latkes.class.getResourceAsStream("/latke.properties");
            }

            if (null != resourceAsStream) {
                LATKE_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded latke.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Loads latke.properties failed", e);

            throw new RuntimeException("Loads latke.properties failed");
        }

        try {
            InputStream resourceAsStream;
            final String localPropsEnv = System.getenv("LATKE_LOCAL_PROPS");
            if (StringUtils.isNotBlank(localPropsEnv)) {
                LOGGER.debug("Loading local.properties from env var [$LATKE_LOCAL_PROPS=" + localPropsEnv + "]");
                resourceAsStream = new FileInputStream(localPropsEnv);
            } else {
                LOGGER.debug("Loading local.properties from classpath [/local.properties]");
                resourceAsStream = Latkes.class.getResourceAsStream("/local.properties");
            }

            if (null != resourceAsStream) {
                LOCAL_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded local.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.DEBUG, "Loads local.properties failed, ignored");
        }

        LOGGER.debug("Loading remote.properties");
        try {
            final InputStream resourceAsStream = Latkes.class.getResourceAsStream("/remote.properties");
            if (null != resourceAsStream) {
                REMOTE_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded remote.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.DEBUG, "Not found Latke remote.properties, ignored");
        }
    }

    /**
     * Private constructor.
     */
    private Latkes() {
    }

    /**
     * Gets static resource (JS, CSS files) version.
     * <p>
     * Returns the value of "staticResourceVersion" property in local.properties. Returns the
     * {@link #startupTimeMillis application startup millisecond} if not found the "staticResourceVersion" property in
     * local.properties.
     * </p>
     *
     * @return static resource version
     */
    public static String getStaticResourceVersion() {
        if (null == staticResourceVersion) {
            staticResourceVersion = getLatkeProperty("staticResourceVersion");
            if (null == staticResourceVersion) {
                staticResourceVersion = startupTimeMillis;
            }
        }

        return staticResourceVersion;
    }

    /**
     * Sets static resource version with the specified static resource version.
     *
     * @param staticResourceVersion the specified static resource version
     */
    public static void setStaticResourceVersion(final String staticResourceVersion) {
        Latkes.staticResourceVersion = staticResourceVersion;
    }

    /**
     * Gets server scheme.
     * <p>
     * Returns the value of "serverScheme" property in latke.properties.
     * </p>
     *
     * @return server scheme
     */
    public static String getServerScheme() {
        if (null == serverScheme) {
            serverScheme = getLatkeProperty("serverScheme");
            if (null == serverScheme) {
                throw new IllegalStateException("latke.properties [serverScheme] is empty");
            }
        }

        return serverScheme;
    }

    /**
     * Sets server scheme with the specified server scheme.
     *
     * @param serverScheme the specified server scheme
     */
    public static void setServerScheme(final String serverScheme) {
        Latkes.serverScheme = serverScheme;
    }

    /**
     * Gets server host.
     * <p>
     * Returns the value of "serverHost" property in latke.properties.
     * </p>
     *
     * @return server host
     */
    public static String getServerHost() {
        if (null == serverHost) {
            serverHost = getLatkeProperty("serverHost");
            if (null == serverHost) {
                throw new IllegalStateException("latke.properties [serverHost] is empty");
            }
        }

        return serverHost;
    }

    /**
     * Sets server host with the specified server host.
     *
     * @param serverHost the specified server host
     */
    public static void setServerHost(final String serverHost) {
        Latkes.serverHost = serverHost;
    }

    /**
     * Gets server port.
     * <p>
     * Returns the value of "serverPort" property in latke.properties.
     * </p>
     *
     * @return server port
     */
    public static String getServerPort() {
        if (null == serverPort) {
            serverPort = getLatkeProperty("serverPort");
        }

        return serverPort;
    }

    /**
     * Sets server port with the specified server port.
     *
     * @param serverPort the specified server port
     */
    public static void setServerPort(final String serverPort) {
        Latkes.serverPort = serverPort;
    }

    /**
     * Gets server.
     *
     * @return server, ${serverScheme}://${serverHost}:${serverPort}
     */
    public static String getServer() {
        if (null == server) {
            final StringBuilder serverBuilder = new StringBuilder(getServerScheme()).append("://").append(getServerHost());
            final String port = getServerPort();
            if (StringUtils.isNotBlank(port) && !port.equals("80")) {
                serverBuilder.append(':').append(port);
            }

            server = serverBuilder.toString();
        }

        return server;
    }

    /**
     * Gets serve path.
     *
     * @return serve path, ${server}${contextPath}
     */
    public static String getServePath() {
        if (null == servePath) {
            servePath = getServer() + getContextPath();
        }

        return servePath;
    }

    /**
     * Gets static server scheme.
     * <p>
     * Returns the value of "staticServerScheme" property in latke.properties, returns the value of "serverScheme" if
     * not found.
     * </p>
     *
     * @return static server scheme
     */
    public static String getStaticServerScheme() {
        if (null == staticServerScheme) {
            staticServerScheme = getLatkeProperty("staticServerScheme");
            if (null == staticServerScheme) {
                staticServerScheme = getServerScheme();
            }
        }

        return staticServerScheme;
    }

    /**
     * Sets static server scheme with the specified static server scheme.
     *
     * @param staticServerScheme the specified static server scheme
     */
    public static void setStaticServerScheme(final String staticServerScheme) {
        Latkes.staticServerScheme = staticServerScheme;
    }

    /**
     * Gets static server host.
     * <p>
     * Returns the value of "staticServerHost" property in latke.properties, returns the value of "serverHost" if not
     * found.
     * </p>
     *
     * @return static server host
     */
    public static String getStaticServerHost() {
        if (null == staticServerHost) {
            staticServerHost = getLatkeProperty("staticServerHost");
            if (null == staticServerHost) {
                staticServerHost = getServerHost();
            }
        }

        return staticServerHost;
    }

    /**
     * Sets static server host with the specified static server host.
     *
     * @param staticServerHost the specified static server host
     */
    public static void setStaticServerHost(final String staticServerHost) {
        Latkes.staticServerHost = staticServerHost;
    }

    /**
     * Gets static server port.
     * <p>
     * Returns the value of "staticServerPort" property in latke.properties, returns the value of "serverPort" if not
     * found.
     * </p>
     *
     * @return static server port
     */
    public static String getStaticServerPort() {
        if (null == staticServerPort) {
            staticServerPort = getLatkeProperty("staticServerPort");
            if (null == staticServerPort) {
                staticServerPort = getServerPort();
            }
        }

        return staticServerPort;
    }

    /**
     * Sets static server port with the specified static server port.
     *
     * @param staticServerPort the specified static server port
     */
    public static void setStaticServerPort(final String staticServerPort) {
        Latkes.staticServerPort = staticServerPort;
    }

    /**
     * Gets static server.
     *
     * @return static server, ${staticServerScheme}://${staticServerHost}:${staticServerPort}
     */
    public static String getStaticServer() {
        if (null == staticServer) {
            final StringBuilder staticServerBuilder = new StringBuilder(getStaticServerScheme()).append("://").append(getStaticServerHost());

            final String port = getStaticServerPort();
            if (StringUtils.isNotBlank(port) && !port.equals("80")) {
                staticServerBuilder.append(':').append(port);
            }

            staticServer = staticServerBuilder.toString();
        }

        return staticServer;
    }

    /**
     * Gets static serve path.
     *
     * @return static serve path, ${staticServer}${staticPath}
     */
    public static String getStaticServePath() {
        if (null == staticServePath) {
            staticServePath = getStaticServer() + getStaticPath();
        }

        return staticServePath;
    }

    /**
     * Gets context path.
     *
     * @return context path
     */
    public static String getContextPath() {
        if (null != contextPath) {
            return contextPath;
        }

        final String contextPathConf = getLatkeProperty("contextPath");
        if (null != contextPathConf) {
            contextPath = contextPathConf;

            return contextPath;
        }

        final ServletContext servletContext = AbstractServletListener.getServletContext();
        contextPath = servletContext.getContextPath();

        return contextPath;
    }

    /**
     * Sets context path with the specified context path.
     *
     * @param contextPath the specified context path
     */
    public static void setContextPath(final String contextPath) {
        Latkes.contextPath = contextPath;
    }

    /**
     * Gets static path.
     *
     * @return static path
     */
    public static String getStaticPath() {
        if (null == staticPath) {
            staticPath = getLatkeProperty("staticPath");

            if (null == staticPath) {
                staticPath = getContextPath();
            }
        }

        return staticPath;
    }

    /**
     * Sets static path with the specified static path.
     *
     * @param staticPath the specified static path
     */
    public static void setStaticPath(final String staticPath) {
        Latkes.staticPath = staticPath;
    }

    /**
     * Gets IoC scan path.
     *
     * @return scan path
     */
    public static String getScanPath() {
        if (null == scanPath) {
            scanPath = getLatkeProperty("scanPath");
        }

        return scanPath;
    }

    /**
     * Sets IoC scan path with the specified scan path.
     *
     * @param scanPath the specified scan path
     */
    public static void setScanPath(final String scanPath) {
        Latkes.scanPath = scanPath;
    }

    /**
     * Initializes Latke runtime environment.
     */
    public static void initRuntimeEnv() {
        LOGGER.log(Level.TRACE, "Initializes runtime environment from configuration file");

        if (null == runtimeMode) {
            final String runtimeModeValue = getLatkeProperty("runtimeMode");
            if (null != runtimeModeValue) {
                runtimeMode = RuntimeMode.valueOf(runtimeModeValue);
            } else {
                LOGGER.log(Level.TRACE, "Can't parse runtime mode in latke.properties, default to [PRODUCTION]");

                runtimeMode = RuntimeMode.PRODUCTION;
            }
        }
        if (Latkes.RuntimeMode.DEVELOPMENT == getRuntimeMode()) {
            LOGGER.warn("!!!!Runtime mode is [" + Latkes.RuntimeMode.DEVELOPMENT + "], please make sure configured it with ["
                    + Latkes.RuntimeMode.PRODUCTION + "] in latke.properties if deployed on production environment!!!!");
        } else {
            LOGGER.log(Level.INFO, "Runtime mode is [{0}]", getRuntimeMode());
        }

        final RuntimeDatabase runtimeDatabase = getRuntimeDatabase();
        LOGGER.log(Level.INFO, "Runtime database is [{0}]", runtimeDatabase);

        if (RuntimeDatabase.H2 == runtimeDatabase) {
            final String newTCPServer = getLocalProperty("newTCPServer");

            if ("true".equals(newTCPServer)) {
                LOGGER.log(Level.INFO, "Starting H2 TCP server");

                final String jdbcURL = getLocalProperty("jdbc.URL");

                if (StringUtils.isBlank(jdbcURL)) {
                    throw new IllegalStateException("The jdbc.URL in local.properties is required");
                }

                final String[] parts = jdbcURL.split(":");
                if (5 != parts.length) {
                    throw new IllegalStateException("jdbc.URL should like [jdbc:h2:tcp://localhost:8250/~/] (the port part is required)");
                }

                String port = parts[parts.length - 1];
                port = StringUtils.substringBefore(port, "/");

                LOGGER.log(Level.TRACE, "H2 TCP port [{0}]", port);

                try {
                    h2 = org.h2.tools.Server.createTcpServer(new String[]{"-tcpPort", port, "-tcpAllowOthers"}).start();
                } catch (final SQLException e) {
                    final String msg = "H2 TCP server create failed";
                    LOGGER.log(Level.ERROR, msg, e);

                    throw new IllegalStateException(msg);
                }

                LOGGER.info("Started H2 TCP server");
            }
        }

        final RuntimeCache runtimeCache = getRuntimeCache();
        LOGGER.log(Level.INFO, "Runtime cache is [{0}]", runtimeCache);

        locale = new Locale("en_US");
    }

    /**
     * Gets the runtime mode.
     *
     * @return runtime mode
     */
    public static RuntimeMode getRuntimeMode() {
        if (null == runtimeMode) {
            throw new RuntimeException("Runtime mode has not been initialized!");
        }

        return runtimeMode;
    }

    /**
     * Sets the runtime mode with the specified mode.
     *
     * @param runtimeMode the specified mode
     */
    public static void setRuntimeMode(final RuntimeMode runtimeMode) {
        Latkes.runtimeMode = runtimeMode;
    }

    /**
     * Gets the runtime cache.
     *
     * @return runtime cache
     */
    public static RuntimeCache getRuntimeCache() {
        final String runtimeCache = getLocalProperty("runtimeCache");
        if (null == runtimeCache) {
            LOGGER.debug("Not found [runtimeCache] in local.properties, uses [LOCAL_LRU] as default");

            return RuntimeCache.LOCAL_LRU;
        }

        final RuntimeCache ret = RuntimeCache.valueOf(runtimeCache);
        if (null == ret) {
            throw new RuntimeException("Please configures a valid runtime cache in local.properties!");
        }

        return ret;
    }

    /**
     * Gets the runtime database.
     *
     * @return runtime database
     */
    public static RuntimeDatabase getRuntimeDatabase() {
        final String runtimeDatabase = getLocalProperty("runtimeDatabase");
        if (null == runtimeDatabase) {
            throw new RuntimeException("Please configures runtime database in local.properties!");
        }

        final RuntimeDatabase ret = RuntimeDatabase.valueOf(runtimeDatabase);
        if (null == ret) {
            throw new RuntimeException("Please configures a valid runtime database in local.properties!");
        }

        return ret;
    }

    /**
     * Gets the locale. If the {@link #locale} has not been initialized, invoking this method will throw
     * {@link RuntimeException}.
     *
     * @return the locale
     */
    public static Locale getLocale() {
        if (null == locale) {
            throw new RuntimeException("Default locale has not been initialized!");
        }

        return locale;
    }

    /**
     * Sets the locale with the specified locale.
     *
     * @param locale the specified locale
     */
    public static void setLocale(final Locale locale) {
        Latkes.locale = locale;
    }

    /**
     * Gets a property specified by the given key from file "local.properties".
     *
     * @param key the given key
     * @return the value, returns {@code null} if not found
     */
    public static String getLocalProperty(final String key) {
        String ret = LOCAL_PROPS.getProperty(key);
        if (StringUtils.isBlank(ret)) {
            return ret;
        }

        ret = replaceEnvVars(ret);

        return ret;
    }

    /**
     * Gets a property specified by the given key from file "latke.properties".
     *
     * @param key the given key
     * @return the value, returns {@code null} if not found
     */
    public static String getLatkeProperty(final String key) {
        String ret = LATKE_PROPS.getProperty(key);
        if (StringUtils.isBlank(ret)) {
            return ret;
        }

        ret = replaceEnvVars(ret);

        return ret;
    }

    /**
     * Checks whether the remote interfaces are enabled.
     *
     * @return {@code true} if the remote interfaces enabled, returns {@code false} otherwise
     */
    public static boolean isRemoteEnabled() {
        return !REMOTE_PROPS.isEmpty();
    }

    /**
     * Gets a property specified by the given key from file "remote.properties".
     *
     * @param key the given key
     * @return the value, returns {@code null} if not found
     */
    public static String getRemoteProperty(final String key) {
        return REMOTE_PROPS.getProperty(key);
    }

    /**
     * Shutdowns Latke.
     */
    public static void shutdown() {
        try {
            CronService.shutdown();

            EXECUTOR_SERVICE.shutdown();

            if (RuntimeCache.REDIS == getRuntimeCache()) {
                RedisCache.shutdown();
            }

            Connections.shutdownConnectionPool();
            if (RuntimeDatabase.H2 == getRuntimeDatabase()) {
                final String newTCPServer = getLocalProperty("newTCPServer");
                if ("true".equals(newTCPServer)) {
                    h2.stop();
                    h2.shutdown();

                    LOGGER.log(Level.INFO, "Closed H2 TCP server");
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdowns Latke failed", e);
        }

        BeanManager.close();

        // Manually unregister JDBC driver, which prevents Tomcat from complaining about memory leaks
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();

            try {
                DriverManager.deregisterDriver(driver);
                LOGGER.log(Level.TRACE, "Unregistered JDBC driver [" + driver + "]");
            } catch (final SQLException e) {
                LOGGER.log(Level.ERROR, "Unregister JDBC driver [" + driver + "] failed", e);
            }
        }
    }

    /**
     * Gets the skin name for the specified skin directory name. The skin name was configured in skin.properties
     * file({@code name} as the key) under skin directory specified by the given skin directory name.
     *
     * @param skinDirName the given skin directory name
     * @return skin name, returns {@code null} if not found or error occurs
     */
    public static String getSkinName(final String skinDirName) {
        try {
            final Properties ret = new Properties();
            final File file = getWebFile("/skins/" + skinDirName + "/skin.properties");
            ret.load(new FileInputStream(file));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Read skin configuration error[msg={0}]", e.getMessage());

            return null;
        }
    }

    /**
     * Gets a file in web application with the specified path.
     *
     * @param path the specified path
     * @return file,
     * @see javax.servlet.ServletContext#getResource(java.lang.String)
     * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
     */
    public static File getWebFile(final String path) {
        final ServletContext servletContext = AbstractServletListener.getServletContext();

        File ret;

        try {
            final URL resource = servletContext.getResource(path);
            if (null == resource) {
                return null;
            }

            ret = FileUtils.toFile(resource);

            if (null == ret) {
                final File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
                ret = new File(tempdir.getPath() + path);
                FileUtils.copyURLToFile(resource, ret);
                ret.deleteOnExit();
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads file [path=" + path + "] failed", e);

            return null;
        }
    }

    /**
     * Latke runtime JDBC database specified in the configuration file local.properties.
     *
     * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.6, Jul 5, 2016
     * @see Latkes#getRuntimeDatabase()
     */
    public enum RuntimeDatabase {

        /**
         * None.
         */
        NONE,
        /**
         * Oracle.
         */
        ORACLE,
        /**
         * MySQL.
         */
        MYSQL,
        /**
         * H2.
         */
        H2,
        /**
         * MSSQL.
         */
        MSSQL,
    }

    /**
     * Latke runtime cache specified in the configuration file local.properties.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Jul 5, 2017
     * @see Latkes#getRuntimeCache()
     */
    public enum RuntimeCache {

        /**
         * None.
         */
        NONE,
        /**
         * Local LRU memory cache.
         */
        LOCAL_LRU,
        /**
         * Redis.
         */
        REDIS,
    }

    /**
     * Latke runtime mode.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Jun 24, 2011
     * @see Latkes#getRuntimeMode()
     */
    public enum RuntimeMode {

        /**
         * Indicates Latke runs in development.
         */
        DEVELOPMENT,
        /**
         * Indicates Latke runs in production.
         */
        PRODUCTION,
    }

    /**
     * Replaces ${xxx} with corresponding env variable for the specified val.
     *
     * @param val the specified val
     * @return replaced val
     */
    private static String replaceEnvVars(final String val) {
        String ret = val;
        final String[] envVars = StringUtils.substringsBetween(ret, "${", "}");
        if (null != envVars) {
            for (int i = 0; i < envVars.length; i++) {
                final String envKey = envVars[i];
                String envVal = System.getenv(envKey);
                if (StringUtils.isBlank(envVal)) {
                    envVal = "";
                }

                ret = StringUtils.replace(ret, "${" + envKey + "}", envVal);
            }
        }

        return ret;
    }
}
