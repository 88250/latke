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

import freemarker.template.Template;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.util.Requests;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, May 1, 2020
 * @since 2.4.34
 */
public abstract class AbstractFreeMarkerRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(AbstractFreeMarkerRenderer.class);

    /**
     * Template name.
     */
    private String templateName;

    /**
     * Data model.
     */
    private final Map<String, Object> dataModel = new HashMap<>();

    /**
     * Invoked before render.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    protected abstract void beforeRender(final RequestContext context) throws Exception;

    /**
     * Invoked after render.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    protected abstract void afterRender(final RequestContext context) throws Exception;

    /**
     * Gets a template.
     *
     * @return template, returns {@code null} if not found
     */
    protected abstract Template getTemplate();

    @Override
    public void render(final RequestContext context) {
        final Response response = context.getResponse();
        response.setContentType("text/html; charset=utf-8");
        if (response.isCommitted()) { // response has been sent redirect
            return;
        }

        final Request request = context.getRequest();
        final Template template = getTemplate();
        if (null == template) {
            LOGGER.log(Level.ERROR, "Not found template [{}]", templateName);
            response.sendError0(404);
            return;
        }

        try {
            dataModel.put(Keys.REQUEST, request);
            Keys.fillServer(dataModel);

            beforeRender(context);

            final String html = genHTML(context.getRequest(), dataModel, template);
            doRender(html, context.getRequest(), response);

            afterRender(context);
        } catch (final Exception e) {
            final String requestLog = Requests.getLog(request);
            LOGGER.log(Level.ERROR, "Renders template [" + templateName + "] failed [" + requestLog + "]", e);
            if (null != context.attr(RequestContext.ERROR_CODE)) {
                // 错误处理器如果也报错的话走内部 500 处理
                response.sendError0(500);
            } else {
                response.sendError(500);
            }
        }
    }

    /**
     * Processes the specified FreeMarker template with the specified request, data model.
     *
     * @param request   the specified request
     * @param dataModel the specified data model
     * @param template  the specified FreeMarker template
     * @return generated HTML
     * @throws Exception exception
     */
    protected String genHTML(final Request request, final Map<String, Object> dataModel, final Template template)
            throws Exception {
        final StringWriter stringWriter = new StringWriter();
        template.setOutputEncoding("UTF-8");
        template.process(dataModel, stringWriter);

        final StringBuilder pageContentBuilder = new StringBuilder(stringWriter.toString());
        final long endTimeMillis = System.currentTimeMillis();
        final String dateString = DateFormatUtils.format(endTimeMillis, "yyyy/MM/dd HH:mm:ss");
        final long startTimeMillis = (Long) request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS);
        final String msg = String.format("\n<!-- Generated by Latke (https://github.com/88250/latke) in %1$dms, %2$s -->",
                endTimeMillis - startTimeMillis, dateString);
        pageContentBuilder.append(msg);

        return pageContentBuilder.toString();
    }

    /**
     * Processes the specified FreeMarker template with the specified request, data model and response.
     * <p>
     * Puts the page response contents into cache with the key getting from request attribute specified by <i>page cache
     * key</i>.
     * </p>
     *
     * @param html     the specified HTML content
     * @param request  the specified request
     * @param response the specified response
     */
    protected void doRender(final String html, final Request request, final Response response) {
        if (response.isCommitted()) { // response has been sent redirect
            return;
        }
        response.sendString(html);
    }

    /**
     * Gets the data model.
     *
     * @return data model
     */
    public Map<String, Object> getDataModel() {
        return dataModel;
    }

    /**
     * Gets the template name.
     *
     * @return template name
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the template name with the specified template name.
     *
     * @param templateName the specified template name
     */
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    @Override
    public Map<String, Object> getRenderDataModel() {
        return dataModel;
    }
}
