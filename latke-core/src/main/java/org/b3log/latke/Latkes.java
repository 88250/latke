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
package org.b3log.latke;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.cache.redis.RedisCache;
import org.b3log.latke.http.renderer.StaticFileRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Discoverer;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.util.Connections;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Latke framework configuration utility facade.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.11.0.11, Nov 22, 2019
 * @see #init()
 * @see #shutdown()
 * @see #getServePath()
 * @see #getStaticServePath()
 */
public final class Latkes {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Latkes.class);

    /**
     * Executor service.
     */
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Version.
     */
    public static final String VERSION = "3.0.17";

    /**
     * Application startup time millisecond.
     */
    public static long startupTimeMillis = System.currentTimeMillis();

    /**
     * Local properties (local.properties).
     */
    private static Properties localProps;

    /**
     * Latke configurations (latke.properties).
     */
    private static Properties latkeProps;

    /**
     * Which mode Latke runs in?
     */
    private static RuntimeMode runtimeMode;

    /**
     * Static resource version.
     */
    private static String staticResourceVersion;

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

    /**
     * Init flag.
     */
    private static boolean inited;

    /**
     * Indicates whether HTTP session is enabled.
     */
    private static boolean enabledSession = true;

    /**
     * Whether enables HTTP session. Default is enabled.
     *
     * @param enabled {@code true} if enable it
     */
    public static void setEnabledSession(final boolean enabled) {
        enabledSession = enabled;
    }

    /**
     * Is enabled HTTP session.
     *
     * @return {@true} if enabled it, returns {@code false} otherwise
     */
    public static boolean isEnabledSession() {
        return enabledSession;
    }

    /**
     * Checks if process is running via docker.
     *
     * @return {@code true} it is, returns {@code false} otherwise
     */
    public static boolean isDocker() {
        return 1 == currentPID();
    }

    /**
     * Gets the current process's id.
     *
     * @return the current process's id
     */
    public static long currentPID() {
        final String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();

        return Long.parseLong(processName.split("@")[0]);
    }

    /**
     * Gets operating system name.
     *
     * @return os name
     */
    public static String getOperatingSystemName() {
        return System.getProperty("os.name");
    }

    private static boolean inJar;

    static {
        try {
            final URL resource = StaticFileRenderer.class.getResource("/");
            inJar = null == resource || "jar".equals(resource.toURI().getScheme());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Checks filesystem failed, exit", e);
            System.exit(-1);
        }
    }

    /**
     * Checks whether process is running via jar.
     *
     * @return {@code true} it is, returns {@code false} otherwise
     */
    public static boolean isInJar() {
        return inJar;
    }

    /**
     * Sets local.props with the specified props. This method is useful when you want to override behaviours of the default properties.
     *
     * @param props the specified props
     */
    public static void setLocalProps(final Properties props) {
        Latkes.localProps = props;
    }

    /**
     * Sets local.props with the specified key and value.
     *
     * @param key   the specified key
     * @param value the specified value
     */
    public static void setLocalProperty(final String key, final String value) {
        if (null == key) {
            LOGGER.log(Level.WARN, "local.props can not set null key");

            return;
        }
        if (null == value) {
            LOGGER.log(Level.WARN, "local.props can not set null value");

            return;
        }

        localProps.setProperty(key, value);
    }

    /**
     * Sets latke.props with the specified props. This method is useful when you want to override behaviours of the default properties.
     *
     * @param props the specified props
     */
    public static void setLakteProps(final Properties props) {
        Latkes.latkeProps = props;
    }

    /**
     * Sets latke.props with the specified key and value.
     *
     * @param key   the specified key
     * @param value the specified value
     */
    public static void setLatkeProperty(final String key, final String value) {
        if (null == key) {
            LOGGER.log(Level.WARN, "latke.props can not set null key");

            return;
        }
        if (null == value) {
            LOGGER.log(Level.WARN, "latke.props can not set null value");

            return;
        }

        latkeProps.setProperty(key, value);
    }

    /**
     * Loads the local.props.
     */
    private static void loadLocalProps() {
        if (null == localProps) {
            localProps = new Properties();
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
                localProps.load(resourceAsStream);
                LOGGER.debug("Loaded local.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.DEBUG, "Loads local.properties failed, ignored");
        }
    }

    /**
     * Loads the latke.props.
     */
    private static void loadLatkeProps() {
        if (null == latkeProps) {
            latkeProps = new Properties();
        }

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
                latkeProps.load(resourceAsStream);
                LOGGER.debug("Loaded latke.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Loads latke.properties failed", e);

            throw new RuntimeException("Loads latke.properties failed");
        }
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
                staticResourceVersion = String.valueOf(startupTimeMillis);
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
     *
     * @return server scheme
     */
    public static String getServerScheme() {
        String ret = getLatkeProperty("serverScheme");
        if (null == ret) {
            ret = "http";
        }

        return ret;
    }

    /**
     * Gets server host.
     *
     * @return server host
     */
    public static String getServerHost() {
        String ret = getLatkeProperty("serverHost");
        if (null == ret) {
            ret = "localhost";
        }

        return ret;
    }

    /**
     * Gets public IP.
     *
     * @return public IP
     */
    public static String getPublicIP() {
        if (StringUtils.isBlank(PUBLIC_IP)) {
            initPublicIP();
        }
        return PUBLIC_IP;
    }

    private static String PUBLIC_IP;

    /**
     * Init public IP.
     */
    public synchronized static void initPublicIP() {
        if (StringUtils.isNotBlank(PUBLIC_IP)) {
            return;
        }

        try {
            final URL url = new URL("http://checkip.amazonaws.com");
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                PUBLIC_IP = in.readLine();
            }
            urlConnection.disconnect();

            return;
        } catch (final Exception e) {
            try {
                PUBLIC_IP = InetAddress.getLocalHost().getHostAddress();

                return;
            } catch (final Exception e2) {
            }
        }

        PUBLIC_IP = "127.0.0.1";
    }

    /**
     * Gets server port.
     *
     * @return server port
     */
    public static String getServerPort() {
        String ret = getLatkeProperty("serverPort");
        if (null == ret) {
            ret = "8080";
        }

        return ret;
    }

    /**
     * Gets server.
     *
     * @return server, ${serverScheme}://${serverHost}:${serverPort}
     */
    public static String getServer() {
        final StringBuilder serverBuilder = new StringBuilder(getServerScheme()).append("://").append(getServerHost());
        final String port = getServerPort();
        if (StringUtils.isNotBlank(port) && !"80".equals(port) && !"443".equals(port)) {
            serverBuilder.append(':').append(port);
        }

        return serverBuilder.toString();
    }

    /**
     * Gets serve path.
     *
     * @return serve path, ${server}${contextPath}
     */
    public static String getServePath() {
        return getServer() + getContextPath();
    }

    /**
     * Gets static server scheme.
     *
     * @return static server scheme
     */
    public static String getStaticServerScheme() {
        String ret = getLatkeProperty("staticServerScheme");
        if (null == ret) {
            return getServerScheme();
        }

        return ret;
    }

    /**
     * Gets static server host.
     *
     * @return static server host
     */
    public static String getStaticServerHost() {
        String ret = getLatkeProperty("staticServerHost");
        if (null == ret) {
            return getServerHost();
        }

        return ret;
    }

    /**
     * Gets static server port.
     *
     * @return static server port
     */
    public static String getStaticServerPort() {
        String ret = getLatkeProperty("staticServerPort");
        if (null == ret) {
            return getServerPort();
        }

        return ret;
    }

    /**
     * Gets static server.
     *
     * @return static server, ${staticServerScheme}://${staticServerHost}:${staticServerPort}
     */
    public static String getStaticServer() {
        final StringBuilder staticServerBuilder = new StringBuilder(getStaticServerScheme()).append("://").append(getStaticServerHost());
        final String port = getStaticServerPort();
        if (StringUtils.isNotBlank(port) && !"80".equals(port) && !"443".equals(port)) {
            staticServerBuilder.append(':').append(port);
        }

        return staticServerBuilder.toString();
    }

    /**
     * Gets static serve path.
     *
     * @return static serve path, ${staticServer}${staticPath}
     */
    public static String getStaticServePath() {
        return getStaticServer() + getStaticPath();
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

        contextPath = "";
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
            if (StringUtils.isBlank(scanPath)) {
                scanPath = "org.b3log";
                LOGGER.log(Level.INFO, "IoC scan path is empty, uses \"org.b3log\" as default scan path");
            }
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
     * Initializes Latke framework.
     */
    public static synchronized void init() {
        if (inited) {
            return;
        }
        inited = true;

        LOGGER.log(Level.TRACE, "Initializing Latke");

        loadLatkeProps();
        loadLocalProps();

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
            LOGGER.log(Level.DEBUG, "Runtime mode is [{0}]", getRuntimeMode());
        }

        final RuntimeDatabase runtimeDatabase = getRuntimeDatabase();
        LOGGER.log(Level.DEBUG, "Runtime database is [{0}]", runtimeDatabase);

        if (RuntimeDatabase.H2 == runtimeDatabase) {
            final String newTCPServer = getLocalProperty("newTCPServer");

            if ("true".equals(newTCPServer)) {
                LOGGER.log(Level.DEBUG, "Starting H2 TCP server");

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

                LOGGER.log(Level.DEBUG, "Started H2 TCP server");
            }
        }

        final RuntimeCache runtimeCache = getRuntimeCache();
        LOGGER.log(Level.INFO, "Runtime cache is [{0}]", runtimeCache);

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        final Collection<Class<?>> beanClasses = Discoverer.discover(Latkes.getScanPath());
        BeanManager.start(beanClasses);

        LOGGER.log(Level.INFO, "Initialized Latke");
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

        return RuntimeCache.valueOf(runtimeCache);
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
     * Gets the locale.
     *
     * @return the locale
     */
    public static Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Sets the locale with the specified locale.
     *
     * @param locale the specified locale
     */
    public static void setLocale(final Locale locale) {
        Locale.setDefault(locale);
    }

    /**
     * Gets a property specified by the given key from file "local.properties".
     *
     * @param key the given key
     * @return the value, returns {@code null} if not found
     */
    public static String getLocalProperty(final String key) {
        String ret = localProps.getProperty(key);
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
        String ret = latkeProps.getProperty(key);
        if (StringUtils.isBlank(ret)) {
            return ret;
        }

        ret = replaceEnvVars(ret);

        return ret;
    }

    /**
     * Shutdowns Latke.
     */
    public static void shutdown() {
        try {
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
                    LOGGER.log(Level.DEBUG, "Closed H2 TCP server");
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
            final File file = getFile("/skins/" + skinDirName + "/skin.properties");
            ret.load(new FileInputStream(file));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Read skin [" + skinDirName + "]'s configuration failed: " + e.getMessage());

            return null;
        }
    }

    /**
     * Gets a file with the specified path.
     *
     * @param path the specified path
     * @return file
     */
    public static File getFile(final String path) {
        try {
            final URL resource = Latkes.class.getResource(path);
            if (null == resource) {
                return null;
            }

            File ret = FileUtils.toFile(resource);
            if (null == ret) {
                final File tempdir = FileUtils.getTempDirectory();
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
     * Lists file names under the specified path
     *
     * @param path the specified path
     * @return file names
     */
    public static List<String> listFiles(final String path) {
        final List<String> ret = new ArrayList<>();

        try (final InputStream in = Latkes.class.getResourceAsStream(path);
             final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                ret.add(path + "/" + resource);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Read file names [path=" + path + "] failed", e);
        }

        return ret;
    }


    /**
     * Latke runtime JDBC database specified in the configuration file local.properties.
     *
     * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
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
         * Local LRU memory cache (Caffeine).
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

    /**
     * Private constructor.
     */
    private Latkes() {
    }
}
