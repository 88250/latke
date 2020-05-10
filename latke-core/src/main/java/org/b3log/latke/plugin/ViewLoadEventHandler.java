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


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.BeanManager;

import java.util.Map;
import java.util.Set;


/**
 * FreeMarker view load event handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Aug 9, 2011
 */
public final class ViewLoadEventHandler extends AbstractEventListener<ViewLoadEventData> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ViewLoadEventHandler.class);

    @Override
    public String getEventType() {
        return Keys.FREEMARKER_ACTION;
    }

    @Override
    public void action(final Event<ViewLoadEventData> event) {
        final ViewLoadEventData data = event.getData();
        final String viewName = data.getViewName();
        final Map<String, Object> dataModel = data.getDataModel();

        final PluginManager pluginManager = BeanManager.getInstance().getReference(PluginManager.class);
        final Set<AbstractPlugin> plugins = pluginManager.getPlugins(viewName);

        LOGGER.log(Level.DEBUG, "Plugin count[{}] of view[name={}]", plugins.size(), viewName);
        for (final AbstractPlugin plugin : plugins) {
            switch (plugin.getStatus()) {
                case ENABLED:
                    plugin.plug(dataModel);
                    LOGGER.log(Level.DEBUG, "Plugged[name={}]", plugin.getName());
                    break;
                case DISABLED:
                    plugin.unplug();
                    LOGGER.log(Level.DEBUG, "Unplugged[name={}]", plugin.getName());
                    break;
                default:
                    throw new AssertionError("Plugin state error, this is a bug! Please report this bug (https://github.com/88250/latke/issues/new)!");
            }
        }
    }
}
