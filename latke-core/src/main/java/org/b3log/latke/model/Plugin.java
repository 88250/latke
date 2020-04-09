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
package org.b3log.latke.model;

/**
 * This class defines all plugin model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jul 29, 2019
 */
public final class Plugin {

    /**
     * Key of plugin.
     */
    public static final String PLUGIN = "plugin";

    /**
     * Key of plugins.
     */
    public static final String PLUGINS = "plugins";

    /**
     * rendererId of the plugin.
     */
    public static final String PLUGIN_RENDERER_ID = "rendererId";

    /**
     * Key of plugin author.
     */
    public static final String PLUGIN_AUTHOR = "author";

    /**
     * Key of plugin name.
     */
    public static final String PLUGIN_NAME = "name";

    /**
     * Key of plugin version.
     */
    public static final String PLUGIN_VERSION = "version";

    /**
     * Key of plugin types.
     */
    public static final String PLUGIN_TYPES = "types";

    /**
     * Key of plugin class.
     */
    public static final String PLUGIN_CLASS = "pluginClass";

    /**
     * Key of plugin setting(json formatter).
     */
    public static final String PLUGIN_SETTING = "setting";

    /**
     * Key of plugin status.
     */
    public static final String PLUGIN_STATUS = "status";

    /**
     * Private constructor.
     */
    private Plugin() {
    }
}
