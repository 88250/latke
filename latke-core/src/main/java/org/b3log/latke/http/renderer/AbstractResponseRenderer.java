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
package org.b3log.latke.http.renderer;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public abstract class AbstractResponseRenderer implements ResponseRenderer {

    /**
     * The id of this renderer.
     */
    private String rendererId;

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
     * Gets the data model.
     *
     * @return a new hash map
     */
    public Map<String, Object> getRenderDataModel() {
        return new HashMap<>();
    }

    /**
     * Sets the id of this renderer.
     *
     * @param rendererId the specified renderer id
     */
    public void setRendererId(final String rendererId) {
        this.rendererId = rendererId;
    }
}
