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
package org.b3log.latke.servlet.renderer.freemarker;

import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.time.DateFormatUtils;
import freemarker.template.Template;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.latke.cache.PageCaches;

/**
 * Abstract <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Dec 3, 2011
 */
public abstract class AbstractFreeMarkerRenderer extends AbstractHTTPResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractFreeMarkerRenderer.class.getName());
    /**
     * Template name.
     */
    private String templateName;
    /**
     * Data model.
     */
    private Map<String, Object> dataModel = new HashMap<String, Object>();

    /**
     * Gets a template with the specified template directory name and template 
     * name.
     * 
     * @param templateDirName the specified template directory name
     * @param templateName the specified template name
     * @return template
     * @throws IOException io exception
     */
    protected Template getTemplate(final String templateDirName, final String templateName) throws IOException {
        return Templates.getTemplate(templateDirName, templateName);
    }

    /**
     * Invoked before render.
     * 
     * @param context the specified context
     * @throws Exception exception
     */
    protected abstract void beforeRender(final HTTPRequestContext context) throws Exception;

    /**
     * Invoked after render.
     * 
     * @param context the specified context
     * @throws Exception exception 
     */
    protected abstract void afterRender(final HTTPRequestContext context) throws Exception;

    @Override
    public void render(final HTTPRequestContext context) {
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
                LOGGER.log(Level.SEVERE, "Can not get response writer", ex);
                return;
            }
        }

        if (response.isCommitted()) { // response has been sent redirect
            writer.flush();
            writer.close();

            return;
        }

        try {
            final HttpServletRequest request = context.getRequest();
            final Template template = getTemplate((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), templateName);
            
            if (Templates.hasExpression(template, "${request}")) {
                dataModel.put(Keys.REQUEST, request);
            }

            beforeRender(context);

            final String html = genHTML(context.getRequest(), dataModel, template);
            doRender(html, context.getRequest(), response);

            afterRender(context);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "FreeMarker renders error", e);

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (final IOException ex) {
                LOGGER.log(Level.SEVERE, "Can not send error 500!", ex);
            }
        }
    }

    /**
     * Processes the specified FreeMarker template with the specified request, 
     * data model. 
     * 
     * @param request the specified request
     * @param dataModel the specified data model
     * @param template the specified FreeMarker template
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
        final String msg = String.format("<!-- Generated by B3log Latke(%1$d ms), %2$s -->", endimeMillis - startTimeMillis, dateString);
        pageContentBuilder.append(msg);

        final String ret = pageContentBuilder.toString();

        request.setAttribute(PageCaches.CACHED_CONTENT, ret);

        return ret;
    }

    /**
     * Processes the specified FreeMarker template with the specified request, 
     * data model and response. 
     * 
     * <p>
     * Puts the page response contents into cache with the key getting from 
     * request attribute specified by <i>page cache key</i>.
     * </p>
     * 
     * <p>
     *   <b>Note</b>: This method will write page content to the writer of the
     *   specified response without flush/close it.
     * </p>
     *
     * @param html the specified HTML content
     * @param request the specified request
     * @param response the specified response
     * @throws Exception exception
     */
    @SuppressWarnings("unchecked")
    protected void doRender(final String html, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (final Exception e) {
            writer = new PrintWriter(response.getOutputStream());
        }

        if (response.isCommitted()) { // response has been sent redirect
            writer.flush();
            writer.close();

            return;
        }

        writer.write(html);
        writer.flush();
        writer.close();
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
}
