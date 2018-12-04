/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.servlet.renderer;


import org.apache.commons.lang.StringUtils;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.servlet.HTTPRequestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Abstract HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public abstract class AbstractHTTPResponseRenderer implements HTTPResponseRenderer {

    /**
     * the rendererId of this renderer.
     */
    private String rendererId;

    /**
     * setRendererId.
     *
     * @param rendererId rendererId
     */
    public void setRendererId(final String rendererId) {
        this.rendererId = rendererId;
    }

    @Override
    public abstract void render(final HTTPRequestContext context);

    @Override
    public void preRender(final HTTPRequestContext context, final Map<String, Object> args) {
        if (StringUtils.isBlank(rendererId)) {
            return;
        }

        final Set<AbstractPlugin> pSet = BeanManager.getInstance().getReference(PluginManager.class).getPlugins(rendererId);
        for (AbstractPlugin plugin : pSet) {
            plugin.prePlug(context, args);
        }
    }

    @Override
    public void postRender(final HTTPRequestContext context, final Object ret) {

        if (StringUtils.isBlank(rendererId)) {
            return;
        }

        final Set<AbstractPlugin> pSet = BeanManager.getInstance().getReference(PluginManager.class).getPlugins(rendererId);
        for (AbstractPlugin plugin : pSet) {
            plugin.plug(getRenderDataModel(), context, ret);
        }
    }

    /**
     * getRenderDataModel.
     *
     * @return map
     */
    public Map<String, Object> getRenderDataModel() {
        return new HashMap<String, Object>();
    }
}
