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
package org.b3log.latke.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.servlet.RequestContext;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * Abstract plugin.
 *
 * <p>
 * Id of a plugin is {@linkplain #name name}_{@linkplain #version version}. See {@link PluginManager#setPluginProps} for more details.
 * If the id of one plugin {@linkplain #equals(java.lang.Object) equals} to another's, considering they are the same.
 * </p>
 *
 * <p>
 * <b>Note</b>: The subclass extends from this abstract class MUST has a static method named {@code getInstance} to obtain an instance
 * of this plugin. See <a href="http://en.wikipedia.org/wiki/Singleton_pattern"> Singleton Pattern</a> for more implementation
 * details.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.3.2.2, Dec 5, 2018
 * @see PluginManager
 * @see PluginStatus
 * @see PluginType
 */
public abstract class AbstractPlugin implements Serializable {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractPlugin.class);

    /**
     * Default serial version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Id of this plugin.
     */
    private String id;

    /**
     * the rendererId of the plugin.
     */
    private String rendererId;

    /**
     * Name of this plugin.
     */
    private String name;

    /**
     * Author of this author.
     */
    private String author;

    /**
     * Version of this plugin.
     */
    private String version;

    /**
     * Directory of this plugin.
     */
    private String dirName;

    /**
     * Status of this plugin.
     */
    private PluginStatus status = PluginStatus.ENABLED;

    /**
     * the setting of this plugin.
     */
    private JSONObject setting = new JSONObject();

    /**
     * Types of this plugin.
     */
    private Set<PluginType> types = new HashSet<PluginType>();

    /**
     * Languages.
     */
    private Map<String, Properties> langs = new HashMap<String, Properties>();

    /**
     * FreeMarker configuration.
     */
    private transient Configuration configuration;

    /**
     * Event listeners.
     */
    private List<AbstractEventListener<?>> eventListeners = new ArrayList<AbstractEventListener<?>>();

    /**
     * Adds the specified event listener.
     *
     * @param eventListener the specified event listener
     */
    public void addEventListener(final AbstractEventListener<?> eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * Unplugs.
     */
    public void unplug() {
    }

    /**
     * Gets the directory name of this plugin.
     *
     * @return directory of this plugin
     */
    public String getDirName() {
        return dirName;
    }

    /**
     * Sets the directory name of this plugin with the specified directory. Initializes template engine configuration.
     *
     * @param dirName the specified directory name
     */
    public void setDir(final String dirName) {
        this.dirName = dirName;

        initTemplateEngineCfg();
    }

    /**
     * Initializes template engine configuration.
     */
    private void initTemplateEngineCfg() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        final ServletContext servletContext = AbstractServletListener.getServletContext();

        configuration.setServletContextForTemplateLoading(servletContext, "/plugins/" + dirName);
        LOGGER.log(Level.DEBUG, "Initialized template configuration");
    }

    /**
     * Reads lang_xx.properties into field {@link #langs langs}.
     */
    public void readLangs() {
        final ServletContext servletContext = AbstractServletListener.getServletContext();

        @SuppressWarnings("unchecked") final Set<String> resourcePaths = servletContext.getResourcePaths("/plugins/" + dirName);

        for (final String resourcePath : resourcePaths) {
            if (resourcePath.contains("lang_") && resourcePath.endsWith(".properties")) {
                final String langFileName = StringUtils.substringAfter(resourcePath, "/plugins/" + dirName + "/");

                final String key = langFileName.substring("lang_".length(), langFileName.lastIndexOf("."));
                final Properties props = new Properties();

                try {
                    final File file = Latkes.getWebFile(resourcePath);

                    props.load(new FileInputStream(file));

                    langs.put(key, props);
                } catch (final Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.ERROR, "Get plugin[name=" + name + "]'s language configuration failed",
                            e);
                }
            }
        }
    }

    /**
     * Gets language label with the specified locale and key.
     *
     * @param locale the specified locale
     * @param key    the specified key
     * @return language label
     */
    public String getLang(final Locale locale, final String key) {
        return langs.get(locale.toString()).getProperty(key);
    }

    /**
     * prePlug after the real method be invoked.
     *
     * @param context context
     */
    public abstract void prePlug(final RequestContext context);

    /**
     * postPlug after the dataModel of the simplest-view be generated.
     *
     * @param dataModel dataModel
     * @param context   context
     */
    public abstract void postPlug(Map<String, Object> dataModel, RequestContext context);

    /**
     * The lifecycle pointcut for the plugin to start(enable status).
     */
    protected void start() {
        final EventManager eventManager = BeanManager.getInstance().getReference(EventManager.class);
        for (final AbstractEventListener<?> eventListener : eventListeners) {
            eventManager.registerListener(eventListener);
        }
    }

    /**
     * The lifecycle pointcut for the plugin to close(disable status).
     */
    protected void stop() {
        final EventManager eventManager = BeanManager.getInstance().getReference(EventManager.class);
        for (final AbstractEventListener<?> eventListener : eventListeners) {
            eventManager.unregisterListener(eventListener);
        }
    }

    /**
     * Plugs with the specified data model.
     *
     * @param dataModel the specified data model
     */
    public void plug(final Map<String, Object> dataModel) {
        plug(dataModel, null);
    }

    /**
     * Plugs with the specified data model and the args from request.
     *
     * @param dataModel dataModel
     * @param context   context
     */
    public void plug(final Map<String, Object> dataModel, final RequestContext context) {
        String content = (String) dataModel.get(Plugin.PLUGINS);

        if (null == content) {
            dataModel.put(Plugin.PLUGINS, "");
        }

        handleLangs(dataModel);
        fillDefault(dataModel);

        postPlug(dataModel, context);

        content = (String) dataModel.get(Plugin.PLUGINS);
        final StringBuilder contentBuilder = new StringBuilder(content);

        contentBuilder.append(getViewContent(dataModel));

        final String pluginsContent = contentBuilder.toString();

        dataModel.put(Plugin.PLUGINS, pluginsContent);

        LOGGER.log(Level.DEBUG, "Plugin[name={0}] has been plugged", getName());

    }

    /**
     * Processes languages. Retrieves language labels with default locale, then sets them into the specified data model.
     *
     * @param dataModel the specified data model
     */
    private void handleLangs(final Map<String, Object> dataModel) {
        final Locale locale = Latkes.getLocale();
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();

        final StringBuilder keyBuilder = new StringBuilder(language);

        if (StringUtils.isNotBlank(country)) {
            keyBuilder.append("_").append(country);
        }
        if (StringUtils.isNotBlank(variant)) {
            keyBuilder.append("_").append(variant);
        }

        final String localKey = keyBuilder.toString();
        final Properties props = langs.get(localKey);

        if (null == props) {
            return;
        }

        final Set<Object> keySet = props.keySet();

        for (final Object key : keySet) {
            dataModel.put((String) key, props.getProperty((String) key));
        }
    }

    /**
     * Fills default values into the specified data model.
     *
     * <p>
     * The default data model variable values includes:
     * <ul>
     * <li>{@code Keys.SERVER.*}</li>
     * <li>{@code Keys.RUNTIME.*}</li>
     * </ul>
     * </p>
     *
     * @param dataModel the specified data model
     * @see Keys#fillServer(java.util.Map)
     */
    private void fillDefault(final Map<String, Object> dataModel) {
        Keys.fillServer(dataModel);
        Keys.fillRuntime(dataModel);
    }

    /**
     * Gets view content of a plugin. The content is processed with the
     * specified data model by template engine.
     *
     * @param dataModel the specified data model
     * @return plugin view content
     */
    private String getViewContent(final Map<String, Object> dataModel) {
        if (null == configuration) {
            initTemplateEngineCfg();
        }

        try {
            final Template template = configuration.getTemplate("plugin.ftl");
            final StringWriter sw = new StringWriter();

            template.process(dataModel, sw);

            return sw.toString();
        } catch (final Exception e) {
            // This plugin has no view

            return "";
        }
    }

    /**
     * Converts this plugin to a json object (plugin description).
     *
     * @return a json object, for example,
     * <pre>
     * {
     *     "oId": "",
     *     "name": "",
     *     "version": "",
     *     "author": "",
     *     "status": "" // Enumeration name of {@link PluginStatus}
     * }
     * </pre>
     * @throws JSONException if can not convert
     */
    public JSONObject toJSONObject() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, getId());
        ret.put(Plugin.PLUGIN_NAME, getName());
        ret.put(Plugin.PLUGIN_VERSION, getVersion());
        ret.put(Plugin.PLUGIN_AUTHOR, getAuthor());
        ret.put(Plugin.PLUGIN_STATUS, getStatus().name());
        ret.put(Plugin.PLUGIN_SETTING, getSetting().toString());

        return ret;
    }

    /**
     * Gets the id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id with the specified id.
     *
     * @param id the specified id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Sets the status with the specified status.
     *
     * @param status the specified status
     */
    public void setStatus(final PluginStatus status) {
        this.status = status;
    }

    /**
     * Gets the status of this plugin.
     *
     * @return status
     */
    public PluginStatus getStatus() {
        return status;
    }

    /**
     * Gets the author of this plugin.
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of this plugin with the specified author.
     *
     * @param author the specified author
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Gets the name of this plugin.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this plugin with the specified name.
     *
     * @param name the specified name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the version of this plugin.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of this plugin with the specified version.
     *
     * @param version the specified version
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * getSetting.
     *
     * @return the setting
     */
    public JSONObject getSetting() {
        return setting;
    }

    /**
     * setSetting.
     *
     * @param setting the setting to set
     */
    public void setSetting(final JSONObject setting) {
        this.setting = setting;
    }

    /**
     * Gets the types of this plugin.
     *
     * @return types
     */
    public Set<PluginType> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    /**
     * getRendererId.
     *
     * @return the rendererId
     */
    public String getRendererId() {
        return rendererId;
    }

    /**
     * setRendererId.
     *
     * @param rendererId the rendererId to set
     */
    public void setRendererId(final String rendererId) {
        this.rendererId = rendererId;
    }

    /**
     * Adds the specified type.
     *
     * @param type the specified type
     */
    public void addType(final PluginType type) {
        types.add(type);
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPlugin other = (AbstractPlugin) obj;

        if ((null == this.id) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 2;

        hash = 2 + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * when the plugin change the status,it should note the pointcut lifecycle to know.
     * <p>
     * to enable :start()
     * to disable :stop()
     * </p>
     */
    public void changeStatus() {

        switch (status) {
            case ENABLED:
                start();
                break;

            case DISABLED:
                stop();

            default:
        }
    }

}
