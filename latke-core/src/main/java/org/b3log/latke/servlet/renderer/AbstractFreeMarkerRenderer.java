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

import freemarker.template.Template;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.util.Requests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.15, Nov 28, 2018
 */
public abstract class AbstractFreeMarkerRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractFreeMarkerRenderer.class);

    /**
     * Template name.
     */
    private String templateName;

    /**
     * Data model.
     */
    private Map<String, Object> dataModel = new HashMap<>();

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
        final HttpServletResponse response = context.getResponse();
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (final Exception e) {
            try {
                writer = new PrintWriter(response.getOutputStream());
            } catch (final IOException ex) {
                LOGGER.log(Level.ERROR, "Can not get response writer", ex);
                return;
            }
        }

        if (response.isCommitted()) { // response has been sent redirect
            writer.flush();
            writer.close();

            return;
        }

        final HttpServletRequest request = context.getRequest();
        final Template template = getTemplate();
        if (null == template) {
            LOGGER.log(Level.ERROR, "Not found template [{0}]", templateName);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.log(Level.ERROR, "Can not send error 404!", ex);
            }

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

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (final IOException ex) {
                LOGGER.log(Level.ERROR, "Sends error 500 failed", ex);
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
    protected String genHTML(final HttpServletRequest request, final Map<String, Object> dataModel, final Template template)
            throws Exception {
        final StringWriter stringWriter = new StringWriter();
        template.setOutputEncoding("UTF-8");
        template.process(dataModel, stringWriter);

        final StringBuilder pageContentBuilder = new StringBuilder(stringWriter.toString());
        final long endimeMillis = System.currentTimeMillis();
        final String dateString = DateFormatUtils.format(endimeMillis, "yyyy/MM/dd HH:mm:ss");
        final long startTimeMillis = (Long) request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS);
        final String msg = String.format("\n<!-- Generated by Latke (https://github.com/b3log/latke) in %1$dms, %2$s -->",
                endimeMillis - startTimeMillis, dateString);
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
     * @throws Exception exception
     */
    protected void doRender(final String html, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (final Exception e) {
            writer = new PrintWriter(response.getOutputStream());
        }

        try {
            if (response.isCommitted()) { // response has been sent redirect
                writer.flush();
                writer.close();

                return;
            }

            writer.write(html);
            writer.flush();
            writer.close();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Writes pipe failed: " + e.getMessage());
        }
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
