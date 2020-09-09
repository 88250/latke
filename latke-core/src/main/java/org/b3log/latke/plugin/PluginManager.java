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
package org.b3log.latke.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.util.Stopwatchs;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin loader.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/yanxingangsun">yanxingangsun</a>
 * @version 1.0.3.1, Jul 29, 2019
 */
@Singleton
public class PluginManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PluginManager.class);

    /**
     * Type of loaded event.
     */
    public static final String PLUGIN_LOADED_EVENT = "pluginLoadedEvt";

    /**
     * Plugins cache.
     * <p>
     * Caches plugins with the key "plugins" and its value is the real holder, a map: &lt;"hosting view name", plugins&gt;
     * </p>
     */
    private final Map<String, Set<AbstractPlugin>> pluginCache = new HashMap<>();

    /**
     * Plugin class loaders.
     */
    private final Set<ClassLoader> classLoaders = new HashSet<>();

    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;

    /**
     * Gets all plugins.
     *
     * @return all plugins, returns an empty list if not found
     */
    public List<AbstractPlugin> getPlugins() {
        if (pluginCache.isEmpty()) {
            LOGGER.info("Plugin cache miss, reload");

            load();
        }

        return pluginCache.values().stream().flatMap(Set::stream).collect(Collectors.toList());
    }

    /**
     * Gets a plugin by the specified view name.
     *
     * @param viewName the specified view name
     * @return a plugin, returns an empty list if not found
     */
    public Set<AbstractPlugin> getPlugins(final String viewName) {
        if (pluginCache.isEmpty()) {
            LOGGER.info("Plugin cache miss, reload");

            load();
        }

        return pluginCache.getOrDefault(viewName, Collections.emptySet());
    }

    /**
     * Loads plugins from directory {@literal webRoot/plugins/}.
     */
    public void load() {
        Stopwatchs.start("Load Plugins");

        classLoaders.clear();

        final List<String> pluginDirPaths = Latkes.listFiles("/plugins");
        final List<AbstractPlugin> plugins = new ArrayList<>();
        for (final String pluginDirPath : pluginDirPaths) {
            try {
                LOGGER.log(Level.INFO, "Loading plugin under directory [{}]", pluginDirPath);

                final AbstractPlugin plugin = load(pluginDirPath, pluginCache);
                if (plugin != null) {
                    plugins.add(plugin);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Load plugin under directory [" + pluginDirPath + "] failed", e);
            }
        }

        eventManager.fireEventSynchronously(new Event<>(PLUGIN_LOADED_EVENT, plugins));

        Stopwatchs.end();
    }

    /**
     * Loads a plugin by the specified plugin directory and put it into the specified holder.
     *
     * @param pluginDirPath the specified plugin directory
     * @param holder        the specified holder
     * @return loaded plugin
     * @throws Exception exception
     */
    private AbstractPlugin load(final String pluginDirPath, final Map<String, Set<AbstractPlugin>> holder) throws Exception {
        final Properties props = new Properties();

        String plugin = StringUtils.substringAfter(pluginDirPath, "/plugins");
        plugin = plugin.replace("/", "");

        final File file = Latkes.getFile("/plugins/" + plugin + "/plugin.properties");
        props.load(new FileInputStream(file));

        final URL defaultClassesFileDirURL = PluginManager.class.getResource("/plugins/" + plugin + "classes");
        URLClassLoader classLoader;
        if (null != defaultClassesFileDirURL) {
            classLoader = new URLClassLoader(new URL[]{defaultClassesFileDirURL}, PluginManager.class.getClassLoader());
        } else {
            classLoader = new URLClassLoader(new URL[0], PluginManager.class.getClassLoader());
        }

        classLoaders.add(classLoader);

        String pluginClassName = props.getProperty(Plugin.PLUGIN_CLASS);
        if (StringUtils.isBlank(pluginClassName)) {
            pluginClassName = NotInteractivePlugin.class.getName();
        }

        final String rendererId = props.getProperty(Plugin.PLUGIN_RENDERER_ID);
        if (StringUtils.isBlank(rendererId)) {
            LOGGER.log(Level.WARN, "no renderer defined by this plugin [" + plugin + "]，this plugin will be ignore!");
            return null;
        }

        final Class<?> pluginClass = classLoader.loadClass(pluginClassName);

        LOGGER.log(Level.TRACE, "Loading plugin class [name={}]", pluginClassName);
        final AbstractPlugin ret = (AbstractPlugin) pluginClass.getDeclaredConstructor().newInstance();
        ret.setRendererId(rendererId);

        setPluginProps(plugin, ret, props);
        register(ret, holder);

        ret.changeStatus();

        return ret;
    }

    /**
     * Registers the specified plugin into the specified holder.
     *
     * @param plugin the specified plugin
     * @param holder the specified holder
     */
    private void register(final AbstractPlugin plugin, final Map<String, Set<AbstractPlugin>> holder) {
        final String rendererId = plugin.getRendererId();

        /*
         * the rendererId support multiple,using ';' to split.
         * and using Map to match the plugin is not flexible, a regular expression match pattern may be needed in future.
         */
        Arrays.asList(rendererId.split(";")).forEach(rid ->
                holder.computeIfAbsent(rid, k -> new HashSet<>()).add(plugin));

        LOGGER.log(Level.DEBUG, "Registered plugin [name={}, version={}] for rendererId [name={}], [{}] plugins totally",
                plugin.getName(), plugin.getVersion(), rendererId, holder.size());
    }

    /**
     * Sets the specified plugin's properties from the specified properties file under the specified plugin directory.
     *
     * @param pluginDirName the specified plugin directory
     * @param plugin        the specified plugin
     * @param props         the specified properties file
     */
    static void setPluginProps(final String pluginDirName, final AbstractPlugin plugin, final Properties props) {
        final String author = props.getProperty(Plugin.PLUGIN_AUTHOR);
        final String name = props.getProperty(Plugin.PLUGIN_NAME);
        final String version = props.getProperty(Plugin.PLUGIN_VERSION);
        final String types = props.getProperty(Plugin.PLUGIN_TYPES);

        LOGGER.log(Level.TRACE, "Plugin [name={}, author={}, version={}, types={}]", name, author, version, types);

        plugin.setAuthor(author);
        plugin.setName(name);
        plugin.setId(name + "_" + version);
        plugin.setVersion(version);
        plugin.setDir(pluginDirName);
        plugin.readLangs();

        // try to find the setting config.json
        final File settingFile = Latkes.getFile("/plugins/" + pluginDirName + "/config.json");

        if (null != settingFile && settingFile.exists()) {
            try {
                final String config = FileUtils.readFileToString(settingFile, Charset.defaultCharset());
                final JSONObject jsonObject = new JSONObject(config);

                plugin.setSetting(jsonObject);
            } catch (final IOException ie) {
                LOGGER.log(Level.ERROR, "reading the config of the plugin [" + name + "]  failed", ie);
            } catch (final JSONException e) {
                LOGGER.log(Level.ERROR, "convert the  config of the plugin [" + name + "] to json failed", e);
            }
        }

        Arrays.stream(types.split(",")).map(PluginType::valueOf).forEach(plugin::addType);
    }

    /**
     * Gets the plugin class loaders.
     *
     * @return plugin class loaders
     */
    public Set<ClassLoader> getClassLoaders() {
        return Collections.unmodifiableSet(classLoaders);
    }
}
