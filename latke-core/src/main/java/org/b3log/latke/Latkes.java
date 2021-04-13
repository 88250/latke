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
package org.b3log.latke;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.cache.redis.RedisCache;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Discoverer;
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
 * @version 2.11.1.20, Apr 2, 2021
 * @see #init()
 * @see #shutdown()
 * @see #getServePath()
 * @see #getStaticServePath()
 */
public final class Latkes {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Latkes.class);

    /**
     * Executor service.
     */
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Version.
     */
    public static final String VERSION = "3.4.14";

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
     * Init flag.
     */
    private static boolean inited;

    /**
     * Indicates whether HTTP session is enabled.
     */
    private static boolean enabledSession = true;

    /**
     * Scheme of the current request.
     */
    private static final ThreadLocal<String> SCHEME = new ThreadLocal<>();

    /**
     * Host of the current request.
     */
    private static final ThreadLocal<String> HOST = new ThreadLocal<>();

    /**
     * Port of the current request.
     */
    private static final ThreadLocal<String> PORT = new ThreadLocal<>();

    /**
     * Sets the current scheme with the specified scheme.
     *
     * @param scheme the specified scheme
     */
    public static void setScheme(final String scheme) {
        SCHEME.set(scheme);
    }

    /**
     * Sets the current host with the specified host.
     *
     * @param host the specified host
     */
    public static void setHost(final String host) {
        HOST.set(host);
    }

    /**
     * Sets the current port with the specified port.
     *
     * @param port the specified port
     */
    public static void setPort(final String port) {
        PORT.set(port);
    }

    /**
     * Clears scheme, host and port.
     */
    public static void clearSchemeHostPort() {
        SCHEME.remove();
        HOST.remove();
        PORT.remove();
    }

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
            final URL resource = Latkes.class.getResource("/latke.properties");
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
    public static void setLatkeProps(final Properties props) {
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
        final String scheme = SCHEME.get();
        if (null != scheme) {
            return scheme;
        }

        String ret = getLatkeProperty("serverScheme");
        if (null == ret) {
            ret = "http";
        }

        return ret;
    }

    /**
     * Sets server scheme.
     *
     * @param serverScheme the specified server scheme
     */
    public static void setServerScheme(final String serverScheme) {
        setLatkeProperty("serverScheme", serverScheme);
    }

    /**
     * Gets server host.
     *
     * @return server host
     */
    public static String getServerHost() {
        final String host = HOST.get();
        if (null != host) {
            return host;
        }

        String ret = getLatkeProperty("serverHost");
        if (null == ret) {
            ret = "localhost";
        }

        return ret;
    }

    /**
     * Sets server host.
     *
     * @param serverHost the specified server host
     */
    public static void setServerHost(final String serverHost) {
        setLatkeProperty("serverHost", serverHost);
    }

    /**
     * Gets server port.
     *
     * @return server port
     */
    public static String getServerPort() {
        final String port = PORT.get();
        if (null != port) {
            return port;
        }

        String ret = getLatkeProperty("serverPort");
        if (null == ret) {
            ret = "8080";
        }

        return ret;
    }

    /**
     * Sets server port.
     *
     * @param serverPort the specified server port
     */
    public static void setServerPort(final String serverPort) {
        setLatkeProperty("serverPort", serverPort);
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
     * Sets static server scheme.
     *
     * @param staticServerScheme the specified static server scheme
     */
    public static void setStaticServerScheme(final String staticServerScheme) {
        setLatkeProperty("staticServerScheme", staticServerScheme);
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
     * Sets static server host.
     *
     * @param staticServerHost the specified static server host
     */
    public static void setStaticServerHost(final String staticServerHost) {
        setLatkeProperty("staticServerHost", staticServerHost);
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
     * Sets static server port.
     *
     * @param staticServerPort the specified static server port
     */
    public static void setStaticServerPort(final String staticServerPort) {
        setLatkeProperty("staticServerPort", staticServerPort);
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
            LOGGER.log(Level.DEBUG, "Runtime mode is [{}]", getRuntimeMode());
        }

        final RuntimeDatabase runtimeDatabase = getRuntimeDatabase();
        LOGGER.log(Level.DEBUG, "Runtime database is [{}]", runtimeDatabase);

        final RuntimeCache runtimeCache = getRuntimeCache();
        LOGGER.log(Level.INFO, "Runtime cache is [{}]", runtimeCache);

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
     * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.1.0.0, Jun 20, 2020
     * @see Latkes#getRuntimeDatabase()
     */
    public enum RuntimeDatabase {

        /**
         * None.
         */
        NONE,
        /**
         * MySQL.
         */
        MYSQL,
        /**
         * H2.
         */
        H2,
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
         * Local LRU memory cache (Guava).
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
            for (final String envKey : envVars) {
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
