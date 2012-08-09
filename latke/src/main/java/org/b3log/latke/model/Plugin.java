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
package org.b3log.latke.model;

/**
 * This class defines all plugin model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Oct 30, 2011
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
     * Key of plugin event listener classes.
     */
    public static final String PLUGIN_EVENT_LISTENER_CLASSES = "eventListenerClasses";
    /**
     * Key of plugin status.
     */
    public static final String PLUGIN_STATUS = "status";

    /**
     * Private default constructor.
     */
    private Plugin() {
    }
}
