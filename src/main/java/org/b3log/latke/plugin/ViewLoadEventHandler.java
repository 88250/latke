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

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;

/**
 * FreeMarker view load event handler.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Aug 9, 2011
 */
public final class ViewLoadEventHandler extends AbstractEventListener<ViewLoadEventData> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ViewLoadEventHandler.class.getName());

    @Override
    public String getEventType() {
        return Keys.FREEMARKER_ACTION;
    }

    @Override
    public void action(final Event<ViewLoadEventData> event) throws EventException {
        final ViewLoadEventData data = event.getData();
        final String viewName = data.getViewName();
        final Map<String, Object> dataModel = data.getDataModel();

        final Set<AbstractPlugin> plugins = PluginManager.getInstance().getPlugins(viewName);
        LOGGER.log(Level.FINER, "Plugin count[{0}] of view[name={1}]", new Object[]{plugins.size(), viewName});
        for (final AbstractPlugin plugin : plugins) {
            switch (plugin.getStatus()) {
                case ENABLED:
                    plugin.plug(dataModel);
                    LOGGER.log(Level.FINER, "Plugged[name={0}]", plugin.getName());
                    break;
                case DISABLED:
                    plugin.unplug();
                    LOGGER.log(Level.FINER, "Unplugged[name={0}]", plugin.getName());
                    break;
                default:
                    throw new AssertionError("Plugin state error, this is a bug! Please report "
                                             + "this bug on http://code.google.com/p/b3log-solo/issues/list!");
            }
        }
    }
}
