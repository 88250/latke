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

import org.b3log.latke.repository.jdbc.util.JdbcRepositories;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * This class define framework keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.2.10, Feb 10, 2019
 */
public final class Keys {

    /**
     * Key of data.
     */
    public static final String DATA = "data";

    /**
     * Key of token.
     */
    public static final String TOKEN = "token";

    /**
     * Key of message.
     */
    public static final String MSG = "msg";

    /**
     * Key of event.
     */
    public static final String EVENTS = "events";

    /**
     * Key of code.
     */
    public static final String CODE = "code";

    /**
     * Key of action status code.
     */
    public static final String STATUS_CODE = "sc";

    /**
     * Key of session id.
     */
    public static final String SESSION_ID = "sId";

    /**
     * Key of results.
     */
    public static final String RESULTS = "rslts";

    /**
     * Key of id of an entity json object.
     */
    public static final String OBJECT_ID = JdbcRepositories.getDefaultKeyName();

    /**
     * Key of ids.
     */
    public static final String OBJECT_IDS = "oIds";

    /**
     * Key of locale.
     */
    public static final String LOCALE = "locale";

    /**
     * Key of language.
     */
    public static final String LANGUAGE = "lang";

    /**
     * Simple date format. (yyyy-MM-dd HH:mm:ss)
     */
    public static final DateFormat SIMPLE_DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Key of template directory name.
     */
    public static final String TEMAPLTE_DIR_NAME = "templateDirName";

    /**
     * Key of exclusion.
     */
    public static final String EXCLUDES = "excludes";

    /**
     * Key of request.
     */
    public static final String REQUEST = "request";

    /**
     * Key of FreeMarker render.
     */
    public static final String FREEMARKER_ACTION = "FreeMarkerAction";

    /**
     * Private constructor.
     */
    private Keys() {
    }

    /**
     * Fills the server info into the specified data model.
     * <ul>
     * <li>{@value org.b3log.latke.Keys.Server#SERVER_SCHEME}</li>
     * <li>{@value org.b3log.latke.Keys.Server#SERVER_HOST}</li>
     * <li>{@value org.b3log.latke.Keys.Server#SERVER_PORT}</li>
     * <li>{@value org.b3log.latke.Keys.Server#SERVER}</li>
     * <li>{@value org.b3log.latke.Keys.Server#CONTEXT_PATH}</li>
     * <li>{@value org.b3log.latke.Keys.Server#SERVE_PATH}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_SERVER_SCHEME}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_SERVER_HOST}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_SERVER_PORT}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_SERVER}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_PATH}</li>
     * <li>{@value org.b3log.latke.Keys.Server#STATIC_SERVE_PATH}</li>
     * </ul>
     *
     * @param dataModel the specified data model
     */
    public static void fillServer(final Map<String, Object> dataModel) {
        dataModel.put(Server.SERVER_SCHEME, Latkes.getServerScheme());
        dataModel.put(Server.SERVER_HOST, Latkes.getServerHost());
        dataModel.put(Server.SERVER_PORT, Latkes.getServerPort());
        dataModel.put(Server.SERVER, Latkes.getServer());
        dataModel.put(Server.CONTEXT_PATH, Latkes.getContextPath());
        dataModel.put(Server.SERVE_PATH, Latkes.getServePath());

        dataModel.put(Server.STATIC_SERVER_SCHEME, Latkes.getStaticServerScheme());
        dataModel.put(Server.STATIC_SERVER_HOST, Latkes.getStaticServerHost());
        dataModel.put(Server.STATIC_SERVER_PORT, Latkes.getStaticServerPort());
        dataModel.put(Server.STATIC_SERVER, Latkes.getStaticServer());
        dataModel.put(Server.STATIC_PATH, Latkes.getStaticPath());
        dataModel.put(Server.STATIC_SERVE_PATH, Latkes.getStaticServePath());
    }

    /**
     * Fills the runtime info into the specified data model.
     * <ul>
     * <li>{@value org.b3log.latke.Keys.Runtime#RUNTIME_CACHE}</li>
     * <li>{@value org.b3log.latke.Keys.Runtime#RUNTIME_DATABASE}</li>
     * <li>{@value org.b3log.latke.Keys.Runtime#RUNTIME_MODE}</li>
     * </ul>
     *
     * @param dataModel the specified data model
     */
    public static void fillRuntime(final Map<String, Object> dataModel) {
        dataModel.put(Runtime.RUNTIME_CACHE, Latkes.getRuntimeCache().name());
        dataModel.put(Runtime.RUNTIME_DATABASE, Latkes.getRuntimeDatabase().name());
        dataModel.put(Runtime.RUNTIME_MODE, Latkes.getRuntimeMode().name());
    }

    /**
     * This class define HTTP request keys.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.1.0.0, Oct 15, 2018
     */
    public static final class HttpRequest {

        /**
         * Key of is mobile device.
         */
        public static final String IS_MOBILE_BOT = "isMobileBot";

        /**
         * Key of is search engine bot.
         */
        public static final String IS_SEARCH_ENGINE_BOT = "isSearchEngineBot";

        /**
         * Key of static resource checked.
         */
        public static final String REQUEST_STATIC_RESOURCE_CHECKED = "requestStaticResourceChecked";

        /**
         * Key of static resource requesting.
         */
        public static final String IS_REQUEST_STATIC_RESOURCE = "isRequestStaticResource";

        /**
         * Key of start time millis.
         */
        public static final String START_TIME_MILLIS = "startTimeMillis";

        /**
         * Key of request URI.
         */
        public static final String REQUEST_URI = "requestURI";

        /**
         * Key of request method.
         */
        public static final String REQUEST_METHOD = "requestMethod";

        /**
         * Privates constructor.
         */
        private HttpRequest() {
        }
    }

    /**
     * This class define server keys.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, May 4, 2012
     */
    public static final class Server {

        /**
         * Key of server scheme.
         */
        public static final String SERVER_SCHEME = "serverScheme";

        /**
         * Key of server host.
         */
        public static final String SERVER_HOST = "serverHost";

        /**
         * Key of server port.
         */
        public static final String SERVER_PORT = "serverPort";

        /**
         * Key of server.
         */
        public static final String SERVER = "server";

        /**
         * Key of static server scheme.
         */
        public static final String STATIC_SERVER_SCHEME = "staticServerScheme";

        /**
         * Key of static server host.
         */
        public static final String STATIC_SERVER_HOST = "staticServerHost";

        /**
         * Key of static server port.
         */
        public static final String STATIC_SERVER_PORT = "staticServerPort";

        /**
         * Key of static server.
         */
        public static final String STATIC_SERVER = "staticServer";

        /**
         * Key of context path.
         */
        public static final String CONTEXT_PATH = "contextPath";

        /**
         * Key of static path.
         */
        public static final String STATIC_PATH = "staticPath";

        /**
         * Key of serve path.
         */
        public static final String SERVE_PATH = "servePath";

        /**
         * Key of static serve path.
         */
        public static final String STATIC_SERVE_PATH = "staticServePath";

        /**
         * Private constructor.
         */
        private Server() {
        }
    }

    /**
     * This class define runtime keys.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.1.0.0, Jul 5, 2015
     */
    public static final class Runtime {

        /**
         * Key of runtime cache.
         */
        public static final String RUNTIME_CACHE = "runtimeCache";

        /**
         * Key of runtime database.
         */
        public static final String RUNTIME_DATABASE = "runtimeDatabase";

        /**
         * Key of runtime mode.
         */
        public static final String RUNTIME_MODE = "runtimeMode";

        /**
         * Private constructor.
         */
        private Runtime() {
        }
    }
}
