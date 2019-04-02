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
package org.b3log.latke.servlet.renderer;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.servlet.RequestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public abstract class AbstractResponseRenderer implements ResponseRenderer {

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
    public abstract void render(final RequestContext context);

    @Override
    public void preRender(final RequestContext context) {
        if (StringUtils.isBlank(rendererId)) {
            return;
        }

        final Set<AbstractPlugin> pSet = BeanManager.getInstance().getReference(PluginManager.class).getPlugins(rendererId);
        for (final AbstractPlugin plugin : pSet) {
            plugin.prePlug(context);
        }
    }

    @Override
    public void postRender(final RequestContext context) {
        if (StringUtils.isBlank(rendererId)) {
            return;
        }

        final Set<AbstractPlugin> pSet = BeanManager.getInstance().getReference(PluginManager.class).getPlugins(rendererId);
        for (final AbstractPlugin plugin : pSet) {
            plugin.plug(getRenderDataModel(), context);
        }
    }

    /**
     * getRenderDataModel.
     *
     * @return map
     */
    public Map<String, Object> getRenderDataModel() {
        return new HashMap<>();
    }
}
