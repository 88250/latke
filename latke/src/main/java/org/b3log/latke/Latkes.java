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
package org.b3log.latke;


import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.cron.CronService;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.Strings;
import org.b3log.latke.util.freemarker.Templates;
import org.h2.tools.Server;


/**
 * Latke framework configuration utility facade.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.3.7, Feb 11, 2014
 * @see #initRuntimeEnv()
 * @see #shutdown()
 * @see #getServePath()
 * @see #getStaticServePath()
 */
public final class Latkes {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Latkes.class.getName());

    /**
     * Locale. Initializes this by {@link #setLocale(java.util.Locale)}.
     */
    private static Locale locale;

    /**
     * Where Latke runs on?.
     */
    private static RuntimeEnv runtimeEnv;

    /**
     * Which mode Latke runs in?
     */
    private static RuntimeMode runtimeMode;

    /**
     * Local properties (local.properties).
     */
    private static final Properties LOCAL_PROPS = new Properties();

    /**
     * Static resource version.
     */
    private static String staticResourceVersion;

    /**
     * Server scheme.
     */
    private static String serverScheme = "http";

    /**
     * Static server scheme.
     */
    private static String staticServerScheme = "http";

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
     * Latke configurations (latke.properties).
     */
    private static final Properties LATKE_PROPS = new Properties();

    /**
     * Latke remote interfaces configurations (remote.properties).
     */
    private static final Properties REMOTE_PROPS = new Properties();

    /**
     * H2 database TCP server.
     *
     * <p>
     * If Latke is running on {@link RuntimeEnv#LOCAL LOCAL} environment and using {@link RuntimeDatabase#H2 H2} database and specified
     * newTCPServer=true in local.properties, creates a H2 TCP server and starts it.
     * </p>
     */
    private static Server h2;

    static {
        LOGGER.debug("Loading latke.properties");
        try {
            final InputStream resourceAsStream = Latkes.class.getResourceAsStream("/latke.properties");

            if (null != resourceAsStream) {
                LATKE_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded latke.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Not found latke.properties");
            throw new RuntimeException("Not found latke.properties");
        }

        LOGGER.debug("Loading local.properties");
        try {
            final InputStream resourceAsStream = Latkes.class.getResourceAsStream("/local.properties");

            if (null != resourceAsStream) {
                LOCAL_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded local.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.DEBUG, "Not found local.properties");
            // Ignores....
        }

        LOGGER.debug("Loading remote.properties");
        try {
            final InputStream resourceAsStream = Latkes.class.getResourceAsStream("/remote.properties");

            if (null != resourceAsStream) {
                REMOTE_PROPS.load(resourceAsStream);
                LOGGER.debug("Loaded remote.properties");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.DEBUG, "Not found Latke remote.properties");
            // Ignores....
        }
    }

    /**
     * Gets static resource (JS, CSS files) version.
     *
     * <p>
     * Returns the value of "staticResourceVersion" property in local.properties.
     * </p>
     *
     * @return static resource version
     */
    public static String getStaticResourceVersion() {
        if (null == staticResourceVersion) {
            staticResourceVersion = LATKE_PROPS.getProperty("staticResourceVersion");
        }

        return staticResourceVersion;
    }

    /**
     * Gets server scheme.
     *
     * <p>
     * Returns the value of "serverScheme" property in latke.properties.
     * </p>
     *
     * @return server scheme
     */
    public static String getServerScheme() {
        if (null == serverScheme) {
            serverScheme = LATKE_PROPS.getProperty("serverScheme");
        }

        return serverScheme;
    }

    /**
     * Gets server host.
     *
     * <p>
     * Returns the value of "serverHost" property in latke.properties.
     * </p>
     *
     * @return server host
     */
    public static String getServerHost() {
        if (null == serverHost) {
            serverHost = LATKE_PROPS.getProperty("serverHost");
        }

        return serverHost;
    }

    /**
     * Gets server port.
     *
     * <p>
     * Returns the value of "serverPort" property in latke.properties.
     * </p>
     *
     * @return server port
     */
    public static String getServerPort() {
        if (null == serverPort) {
            serverPort = LATKE_PROPS.getProperty("serverPort");
        }

        return serverPort;
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

            if (!Strings.isEmptyOrNull(port)) {
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
     *
     * <p>
     * Returns the value of "staticServerScheme" property in latke.properties.
     * </p>
     *
     * @return static server scheme
     */
    public static String getStaticServerScheme() {
        if (null == staticServerScheme) {
            staticServerScheme = LATKE_PROPS.getProperty("staticServerScheme");
        }

        return staticServerScheme;
    }

    /**
     * Gets static server host.
     *
     * <p>
     * Returns the value of "staticServerHost" property in latke.properties.
     * </p>
     *
     * @return static server host
     */
    public static String getStaticServerHost() {
        if (null == staticServerHost) {
            staticServerHost = LATKE_PROPS.getProperty("staticServerHost");
        }

        return staticServerHost;
    }

    /**
     * Gets static server port.
     *
     * <p>
     * Returns the value of "staticServerPort" property in latke.properties.
     * </p>
     *
     * @return static server port
     */
    public static String getStaticServerPort() {
        if (null == staticServerPort) {
            staticServerPort = LATKE_PROPS.getProperty("staticServerPort");
        }

        return staticServerPort;
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

            if (!Strings.isEmptyOrNull(port)) {
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
     * <p>
     * If Latke runs on xAE, returns "" always, returns the value of "contextPath" property in latke.properties otherwise.
     * </p>
     *
     * @return context path
     */
    public static String getContextPath() {
        if (RuntimeEnv.GAE == getRuntimeEnv()) {
            return "";
        }

        if (null == contextPath) {
            contextPath = LATKE_PROPS.getProperty("contextPath");
        }

        return contextPath;
    }

    /**
     * Gets static path.
     *
     * <p>
     * If Latke runs on xAE, returns "" always, returns the value of "staticPath" property in latke.properties otherwise.
     * </p>
     *
     * @return static path
     */
    public static String getStaticPath() {
        if (RuntimeEnv.GAE == getRuntimeEnv()) {
            return "";
        }

        if (null == staticPath) {
            staticPath = LATKE_PROPS.getProperty("staticPath");
        }

        return staticPath;
    }

    /**
     * Gets IoC scan path.
     *
     * @return scan path
     */
    public static String getScanPath() {
        if (null == scanPath) {
            scanPath = LATKE_PROPS.getProperty("scanPath");
        }

        return scanPath;
    }

    /**
     * Gets runtime configuration of a service specified by the given service name.
     *
     * <p>
     * If current runtime environment is local, returns local in any case.
     * </p>
     *
     * @param serviceName the given service name
     * @return runtime configuration, returns {@code null} if not found
     */
    public static RuntimeEnv getRuntime(final String serviceName) {
        if (RuntimeEnv.LOCAL == getRuntimeEnv()) {
            return RuntimeEnv.LOCAL;
        }

        final String value = LATKE_PROPS.getProperty(serviceName);

        if (null == value) {
            LOGGER.log(Level.WARN, "Rutnime service[name={0}] is undefined, please configure it in latkes.properties", serviceName);
            return null;
        }

        return RuntimeEnv.valueOf(value);
    }

    /**
     * Initializes {@linkplain RuntimeEnv runtime environment}.
     *
     * <ol>
     * <li>
     * If the "latke.properties" has a valid runtime environment configuration (for example,
     * runtimeEnv=GAE or runtimeEnv=LOCAL), initializes the runtime environment as its specified
     * </li>
     * <li>
     * If the GAERepository class (org.b3log.latke.repository.gae.GAERepository)
     * is on the classpath, considered Latke is running on <a href="http://code.google.com/appengine">Google App Engine</a>,
     * otherwise, considered Latke is running on standard Servlet container.
     * </li>
     * </ol>
     *
     * <p>
     * If the Latke runs on the standard Servlet container (local environment),
     * Latke will read database configurations from file "local.properties".
     * </p>
     *
     * <p>
     * Sets the current {@link RuntimeMode runtime mode} to
     * {@link RuntimeMode#DEVELOPMENT development mode}.
     * </p>
     *
     * @see RuntimeEnv
     */
    public static void initRuntimeEnv() {
        LOGGER.log(Level.TRACE, "Initializes runtime environment from configuration file");
        final String runtimeEnvValue = LATKE_PROPS.getProperty("runtimeEnv");

        if (null != runtimeEnvValue) {
            runtimeEnv = RuntimeEnv.valueOf(runtimeEnvValue);
        }

        if (null == runtimeEnv) {
            LOGGER.log(Level.TRACE, "Initializes runtime environment by class loading");

            try {
                runtimeEnv = RuntimeEnv.GAE;
                Class.forName("org.b3log.latke.repository.gae.GAERepository");
            } catch (final ClassNotFoundException e) {
                runtimeEnv = RuntimeEnv.LOCAL;
            }
        }

        final String runtimeModeValue = LATKE_PROPS.getProperty("runtimeMode");

        if (null != runtimeModeValue) {
            runtimeMode = RuntimeMode.valueOf(runtimeModeValue);
        } else {
            LOGGER.log(Level.TRACE, "Can't parse runtime mode in latke.properties, default to [DEVELOPMENT]");
            runtimeMode = RuntimeMode.DEVELOPMENT;
        }

        LOGGER.log(Level.INFO, "Latke is running on [{0}] with mode [{1}]", new Object[] {Latkes.getRuntimeEnv(), Latkes.getRuntimeMode()});

        if (RuntimeEnv.LOCAL == runtimeEnv) {
            // Read local database configurations
            final RuntimeDatabase runtimeDatabase = getRuntimeDatabase();

            LOGGER.log(Level.INFO, "Runtime database is [{0}]", runtimeDatabase);

            if (RuntimeDatabase.H2 == runtimeDatabase) {
                final String newTCPServer = Latkes.getLocalProperty("newTCPServer");

                if ("true".equals(newTCPServer)) {
                    LOGGER.log(Level.INFO, "Starting H2 TCP server");

                    final String jdbcURL = Latkes.getLocalProperty("jdbc.URL");

                    if (Strings.isEmptyOrNull(jdbcURL)) {
                        throw new IllegalStateException("The jdbc.URL in local.properties is required");
                    }

                    final String[] parts = jdbcURL.split(":");

                    if (parts.length != Integer.valueOf("5")/* CheckStyle.... */) {
                        throw new IllegalStateException("jdbc.URL should like [jdbc:h2:tcp://localhost:8250/~/] (the port part is required)");
                    }

                    String port = parts[parts.length - 1];

                    port = StringUtils.substringBefore(port, "/");

                    LOGGER.log(Level.TRACE, "H2 TCP port [{0}]", port);

                    try {
                        h2 = Server.createTcpServer(new String[] {"-tcpPort", port, "-tcpAllowOthers"}).start();
                    } catch (final SQLException e) {
                        final String msg = "H2 TCP server create failed";

                        LOGGER.log(Level.ERROR, msg, e);
                        throw new IllegalStateException(msg);
                    }

                    LOGGER.info("Started H2 TCP server");
                }
            }
        }

        locale = new Locale("en_US");
    }

    /**
     * Gets the runtime environment.
     *
     * @return runtime environment
     */
    public static RuntimeEnv getRuntimeEnv() {
        if (null == Latkes.runtimeEnv) {
            throw new RuntimeException("Runtime enviornment has not been initialized!");
        }

        return Latkes.runtimeEnv;
    }

    /**
     * Sets the runtime mode with the specified mode.
     *
     * @param runtimeMode
     * the specified mode
     */
    public static void setRuntimeMode(final RuntimeMode runtimeMode) {
        Latkes.runtimeMode = runtimeMode;
    }

    /**
     * Gets the runtime mode.
     *
     * @return runtime mode
     */
    public static RuntimeMode getRuntimeMode() {
        if (null == Latkes.runtimeMode) {
            throw new RuntimeException("Runtime mode has not been initialized!");
        }

        return Latkes.runtimeMode;
    }

    /**
     * Gets the runtime database.
     *
     * @return runtime database
     */
    public static RuntimeDatabase getRuntimeDatabase() {
        if (RuntimeEnv.LOCAL != runtimeEnv) {
            throw new RuntimeException(
                "Underlying database can be specified when Latke runs on [LOCAL] environment only, " + "current runtime enviornment ["
                + runtimeEnv + ']');
        }

        final String runtimeDatabase = LOCAL_PROPS.getProperty("runtimeDatabase");

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
     * Sets the locale with the specified locale.
     *
     * @param locale the specified locale
     */
    public static void setLocale(final Locale locale) {
        Latkes.locale = locale;
    }

    /**
     * Gets the locale. If the {@link #locale} has not been initialized,
     * invoking this method will throw {@link RuntimeException}.
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
     * Determines whether Latkes runs with a JDBC database.
     *
     * @return {@code true} if Latkes runs with a JDBC database, returns {@code false} otherwise
     */
    public static boolean runsWithJDBCDatabase() {
        return RuntimeEnv.LOCAL == Latkes.getRuntimeEnv();
    }

    /**
     * Gets a property specified by the given key from file "local.properties".
     *
     * @param key the given key
     * @return the value, returns {@code null} if not found
     */
    public static String getLocalProperty(final String key) {
        return LOCAL_PROPS.getProperty(key);
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
            if (RuntimeEnv.LOCAL != getRuntimeEnv()) {
                return;
            }

            Connections.shutdownConnectionPool();

            final RuntimeDatabase runtimeDatabase = getRuntimeDatabase();

            switch (runtimeDatabase) {
            case H2:
                final String newTCPServer = Latkes.getLocalProperty("newTCPServer");

                if ("true".equals(newTCPServer)) {
                    h2.stop();
                    h2.shutdown();

                    LOGGER.log(Level.INFO, "Closed H2 TCP server");
                }
                break;

            default:

            }

            CronService.shutdown();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdowns Latke failed", e);
        }

        Lifecycle.endApplication();
    }

    /**
     * Sets time zone by the specified time zone id.
     *
     * @param timeZoneId the specified time zone id
     */
    public static void setTimeZone(final String timeZoneId) {
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        Templates.MAIN_CFG.setTimeZone(timeZone);
        Templates.MOBILE_CFG.setTimeZone(timeZone);
    }
    
    /**
     * Loads skin with the specified directory name.
     * 
     * @param skinDirName the specified directory name
     */
    public static void loadSkin(final String skinDirName) {
        LOGGER.debug("Loading skin [dirName=" + skinDirName + ']');

        final String skinName = getSkinName(skinDirName);

        LOGGER.log(Level.INFO, "Current skin[name={0}]", skinName);

        try {
            final String webRootPath = AbstractServletListener.getWebRoot();
            final String skinPath = webRootPath + "skins/" + skinDirName;

            Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        Latkes.setTimeZone("Asia/Shanghai");
        
        LOGGER.info("Loaded skins....");
    }

    /**
     * Gets the skin name for the specified skin directory name. The skin name
     * was configured in skin.properties file({@code name} as the key) under
     * skin directory specified by the given skin directory name.
     *
     * @param skinDirName the given skin directory name
     * @return skin name, returns {@code null} if not found or error occurs
     * @see #getSkinDirNames()
     */
    private static String getSkinName(final String skinDirName) {
        final String webRootPath = AbstractServletListener.getWebRoot();
        final File skins = new File(webRootPath + "skins/");
        final File[] skinDirs = skins.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return pathname.isDirectory() && pathname.getName().equals(skinDirName);
            }
        });

        if (null == skinDirs) {
            LOGGER.error("Skin directory is null");

            return null;
        }

        if (1 != skinDirs.length) {
            LOGGER.log(Level.ERROR, "Skin directory count[{0}]", skinDirs.length);

            return null;
        }

        try {
            final Properties ret = new Properties();
            final String skinPropsPath = skinDirs[0].getPath() + "/" + "skin.properties";

            ret.load(new FileReader(skinPropsPath));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Read skin configuration error[msg={0}]", e.getMessage());

            return null;
        }
    }

    /**
     * Private constructor.
     */
    private Latkes() {}
}
