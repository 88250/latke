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
package org.b3log.latke.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;

/**
 * Plugin loader.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Dec 3, 2011
 */
public final class PluginManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginManager.class.getName());
    /**
     * Type of loaded event.
     */
    public static final String PLUGIN_LOADED_EVENT = "pluginLoadedEvt";
    /**
     * Plugin Cache.
     */
    private static final String PLUGIN_CACHE_NAME = "pluginCache";
    /**
     * Plugins cache.
     * 
     * <p>
     * Caches plugins with the key "plugins" and its value is the real holder, 
     * a map:
     * &lt;"hosting view name", plugins&gt;
     * </p>
     */
    @SuppressWarnings("unchecked")
    private final Cache<String, HashMap<String, HashSet<AbstractPlugin>>> pluginCache =
            (Cache<String, HashMap<String, HashSet<AbstractPlugin>>>) CacheFactory.getCache(PLUGIN_CACHE_NAME);
    /**
     * Plugin root directory.
     */
    public static final String PLUGIN_ROOT = AbstractServletListener.getWebRoot() + Plugin.PLUGINS;
    /**
     * Plugin class loaders.
     */
    private Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();

    /**
     * Updates the specified plugin.
     * 
     * @param plugin the specified plugin
     */
    public void update(final AbstractPlugin plugin) {
        final String viewName = plugin.getViewName();

        HashMap<String, HashSet<AbstractPlugin>> holder = pluginCache.get(PLUGIN_CACHE_NAME);
        if (null == holder) {
            LOGGER.info("Plugin cache miss, reload");
            load();
            holder = pluginCache.get(PLUGIN_CACHE_NAME);

            if (null == holder) {
                throw new IllegalStateException("Plugin cache state error!");
            }
        }

        final HashSet<AbstractPlugin> set = holder.get(viewName);

        // Refresh
        set.remove(plugin);
        set.add(plugin);

        pluginCache.put(PLUGIN_CACHE_NAME, holder);
    }

    /**
     * Gets all plugins.
     * 
     * @return all plugins, returns an empty list if not found
     */
    public List<AbstractPlugin> getPlugins() {
        Map<String, HashSet<AbstractPlugin>> holder = pluginCache.get(PLUGIN_CACHE_NAME);
        if (null == holder) {
            LOGGER.info("Plugin cache miss, reload");
            load();
            holder = pluginCache.get(PLUGIN_CACHE_NAME);

            if (null == holder) {
                throw new IllegalStateException("Plugin cache state error!");
            }
        }

        final List<AbstractPlugin> ret = new ArrayList<AbstractPlugin>();

        for (final Map.Entry<String, HashSet<AbstractPlugin>> entry : holder.entrySet()) {
            ret.addAll(entry.getValue());
        }

        return ret;
    }

    /**
     * Gets a plugin by the specified view name.
     * 
     * @param viewName the specified view name
     * @return a plugin, returns an empty list if not found
     */
    public Set<AbstractPlugin> getPlugins(final String viewName) {
        Map<String, HashSet<AbstractPlugin>> holder = pluginCache.get(PLUGIN_CACHE_NAME);
        if (null == holder) {
            LOGGER.info("Plugin cache miss, reload");
            load();
            holder = pluginCache.get(PLUGIN_CACHE_NAME);

            if (null == holder) {
                throw new IllegalStateException("Plugin cache state error!");
            }
        }

        final Set<AbstractPlugin> ret = holder.get(viewName);
        if (null == ret) {
            return Collections.emptySet();
        }

        return ret;
    }

    /**
     * Loads plugins from directory {@literal webRoot/plugins/}.
     */
    public void load() {
        Stopwatchs.start("Load Plugins");

        classLoaders.clear();

        final File[] pluginsDirs = new File(PLUGIN_ROOT).listFiles();
        final List<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();
        HashMap<String, HashSet<AbstractPlugin>> holder = pluginCache.get(PLUGIN_CACHE_NAME);
        if (null == holder) {
            LOGGER.info("Creates an empty plugin holder");
            holder = new HashMap<String, HashSet<AbstractPlugin>>();
        }

        for (int i = 0; i < pluginsDirs.length; i++) {
            final File pluginDir = pluginsDirs[i];
            if (pluginDir.isDirectory() && !pluginDir.isHidden() && !pluginDir.getName().startsWith(".")) {
                try {
                    LOGGER.log(Level.INFO, "Loading plugin under directory[{0}]", pluginDir.getName());

                    final AbstractPlugin plugin = load(pluginDir, holder);
                    plugins.add(plugin);
                } catch (final Exception e) {
                    LOGGER.log(Level.WARNING, "Load plugin under directory[" + pluginDir.getName() + "] failed", e);
                }
            } else {
                LOGGER.log(Level.WARNING, "It[{0}] is not a directory under " + "directory plugins, ignored", pluginDir.getName());
            }
        }

        try {
            EventManager.getInstance().fireEventSynchronously(new Event<List<AbstractPlugin>>(PLUGIN_LOADED_EVENT, plugins));
        } catch (final EventException e) {
            throw new RuntimeException("Plugin load error", e);
        }

        pluginCache.put(PLUGIN_CACHE_NAME, holder);

        Stopwatchs.end();
    }

    /**
     * Loads a plugin by the specified plugin directory and put it into the 
     * specified holder.
     * 
     * @param pluginDir the specified plugin directory
     * @param holder the specified holder
     * @return loaded plugin
     * @throws Exception exception
     */
    private AbstractPlugin load(final File pluginDir, final HashMap<String, HashSet<AbstractPlugin>> holder) throws Exception {
        final Properties props = new Properties();
        props.load(new FileInputStream(pluginDir.getPath() + File.separator + "plugin.properties"));

        final File defaultClassesFileDir = new File(pluginDir.getPath() + File.separator + "classes");
        final URL defaultClassesFileDirURL = defaultClassesFileDir.toURI().toURL();

        final String webRoot = StringUtils.substringBeforeLast(AbstractServletListener.getWebRoot(), File.separator);
        final String classesFileDirPath = webRoot + props.getProperty("classesDirPath");
        final File classesFileDir = new File(classesFileDirPath);
        final URL classesFileDirURL = classesFileDir.toURI().toURL();

        final URLClassLoader classLoader = new URLClassLoader(new URL[]{
                    defaultClassesFileDirURL, classesFileDirURL}, PluginManager.class.getClassLoader());

        classLoaders.add(classLoader);

        final String pluginClassName = props.getProperty(Plugin.PLUGIN_CLASS);
        LOGGER.log(Level.FINEST, "Loading plugin class[name={0}]", pluginClassName);
        final Class<?> pluginClass = classLoader.loadClass(pluginClassName);
        final AbstractPlugin ret = (AbstractPlugin) pluginClass.newInstance();

        setPluginProps(pluginDir, ret, props);

        registerEventListeners(props, classLoader, ret);

        register(ret, holder);

        return ret;
    }

    /**
     * Registers the specified plugin into the specified holder.
     * 
     * @param plugin the specified plugin
     * @param holder the specified holder 
     */
    private void register(final AbstractPlugin plugin, final HashMap<String, HashSet<AbstractPlugin>> holder) {
        final String viewName = plugin.getViewName();

        HashSet<AbstractPlugin> set = holder.get(viewName);
        if (null == set) {
            set = new HashSet<AbstractPlugin>();
            holder.put(viewName, set);
        }

        set.add(plugin);

        LOGGER.log(Level.FINER, "Registered plugin[name={0}, version={1}] for view[name={2}], [{3}] plugins totally",
                   new Object[]{plugin.getName(), plugin.getVersion(), viewName, holder.size()});
    }

    /**
     * Sets the specified plugin's properties from the specified properties file
     * under the specified plugin directory.
     * 
     * @param pluginDir the specified plugin directory
     * @param plugin the specified plugin
     * @param props the specified properties file
     * @throws Exception exception
     */
    private static void setPluginProps(final File pluginDir, final AbstractPlugin plugin, final Properties props) throws Exception {
        final String author = props.getProperty(Plugin.PLUGIN_AUTHOR);
        final String name = props.getProperty(Plugin.PLUGIN_NAME);
        final String version = props.getProperty(Plugin.PLUGIN_VERSION);
        final String types = props.getProperty(Plugin.PLUGIN_TYPES);
        LOGGER.log(Level.FINEST, "Plugin[name={0}, author={1}, version={2}, types={3}]", new Object[]{name, author, version, types});

        plugin.setAuthor(author);
        plugin.setName(name);
        plugin.setId(name + "_" + version);
        plugin.setVersion(version);
        plugin.setDir(pluginDir);
        plugin.readLangs();
        final String[] typeArray = types.split(",");
        for (int i = 0; i < typeArray.length; i++) {
            final PluginType type = PluginType.valueOf(typeArray[i]);
            plugin.addType(type);
        }
    }

    /**
     * Registers event listeners with the specified plugin properties, class 
     * loader and plugin.
     *
     * <p>
     *   <b>Note</b>: If the specified plugin has some event listeners, each
     *   of these listener MUST implement a static method named 
     *   {@code getInstance} to obtain an instance of this listener. See 
     *   <a href="http://en.wikipedia.org/wiki/Singleton_pattern">
     *   Singleton Pattern</a> for more details.
     * </p>
     * 
     * @param props the specified plugin properties
     * @param classLoader the specified class loader
     * @param plugin the specified plugin
     * @throws Exception exception
     */
    private static void registerEventListeners(final Properties props,
                                               final URLClassLoader classLoader,
                                               final AbstractPlugin plugin)
            throws Exception {
        final String eventListenerClasses = props.getProperty(Plugin.PLUGIN_EVENT_LISTENER_CLASSES);
        final String[] eventListenerClassArray = eventListenerClasses.split(",");
        for (int i = 0; i < eventListenerClassArray.length; i++) {
            final String eventListenerClassName = eventListenerClassArray[i];
            if (Strings.isEmptyOrNull(eventListenerClassName)) {
                LOGGER.log(Level.INFO, "No event listener to load for plugin[name={0}]", plugin.getName());
                return;
            }

            LOGGER.log(Level.FINER, "Loading event listener[className={0}]", eventListenerClassName);
            LOGGER.log(Level.FINEST, "Loading event listener[class={0}]", eventListenerClassName);

            final Class<?> eventListenerClass = classLoader.loadClass(eventListenerClassName);
            final Method getInstance = eventListenerClass.getMethod("getInstance");
            final AbstractEventListener<?> eventListener = (AbstractEventListener) getInstance.invoke(eventListenerClass);

            final EventManager eventManager = EventManager.getInstance();
            eventManager.registerListener(eventListener);
            LOGGER.log(Level.FINER, "Registered event listener[class={0}, eventType={1}] for plugin[name={2}]",
                       new Object[]{eventListener.getClass(), eventListener.getEventType(), plugin.getName()});
        }
    }

    /**
     * Gets the plugin class loaders.
     * 
     * @return plugin class loaders
     */
    public Set<ClassLoader> getClassLoaders() {
        return Collections.unmodifiableSet(classLoaders);
    }

    /**
     * Gets the {@link PluginManager} singleton.
     * 
     * @return a plugin manager singleton
     */
    public static PluginManager getInstance() {
        return PluginManagerSingletonHolder.SINGLETON;
    }

    /**
     * Plugin manager singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jul 23, 2011
     */
    private static final class PluginManagerSingletonHolder {

        /**
         * Singleton.
         */
        private static final PluginManager SINGLETON = new PluginManager();

        /**
         * Private default constructor.
         */
        private PluginManagerSingletonHolder() {
        }
    }

    /**
     * Private default constructor.
     */
    private PluginManager() {
    }
}
