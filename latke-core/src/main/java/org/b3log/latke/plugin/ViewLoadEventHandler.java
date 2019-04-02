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


import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(ViewLoadEventHandler.class);

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

        LOGGER.log(Level.DEBUG, "Plugin count[{0}] of view[name={1}]", plugins.size(), viewName);
        for (final AbstractPlugin plugin : plugins) {
            switch (plugin.getStatus()) {
                case ENABLED:
                    plugin.plug(dataModel);
                    LOGGER.log(Level.DEBUG, "Plugged[name={0}]", plugin.getName());

                    break;
                case DISABLED:
                    plugin.unplug();
                    LOGGER.log(Level.DEBUG, "Unplugged[name={0}]", plugin.getName());

                    break;
                default:
                    throw new AssertionError("Plugin state error, this is a bug! Please report this bug (https://github.com/b3log/latke/issues/new)!");
            }
        }
    }
}
