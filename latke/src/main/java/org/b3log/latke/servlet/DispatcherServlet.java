/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.CacheHandler;
import org.b3log.latke.servlet.handler.Ihandler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.PrepareHandler;
import org.b3log.latke.servlet.handler.RequestMatchHandler;
import org.b3log.latke.servlet.handler.StaticResourceHandler;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.HTTP404Renderer;
import org.b3log.latke.servlet.renderer.HTTP500Renderer;


/**
 * NEW core dispatch-controller for HTTP request dispatching.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.1.0, Sep 12, 2013
 */
public final class DispatcherServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DispatcherServlet.class);

    /**
     * the holder of all the sys-handler.
     */
    private static final List<Ihandler> SYS_HANDLER = new ArrayList<Ihandler>();

    @Override
    public void init() throws ServletException {

        // before StaticResourceHandler ?
        SYS_HANDLER.add(new CacheHandler());
        SYS_HANDLER.add(new StaticResourceHandler(getServletContext()));
        SYS_HANDLER.add(new RequestMatchHandler());
        SYS_HANDLER.add(new PrepareHandler());
        SYS_HANDLER.add(new AdviceHandler());
        SYS_HANDLER.add(new MethodInvokeHandler());
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final HTTPRequestContext httpRequestContext = new HTTPRequestContext();

        httpRequestContext.setRequest(req);
        httpRequestContext.setResponse(resp);
        final HttpControl httpControl = new HttpControl(SYS_HANDLER.iterator(), httpRequestContext);

        try {
            httpControl.nextHandler();
        } catch (final Exception e) {
            httpRequestContext.setRenderer(new HTTP500Renderer(e));
        }

        result(httpRequestContext);
    }

    /**
     * To http repsonse.
     * 
     * @param context {@link HTTPRequestContext}
     * @throws IOException IOException
     */
    private void result(final HTTPRequestContext context) throws IOException {
        final HttpServletResponse response = context.getResponse();

        if (response.isCommitted()) { // Sends rdirect or send error
            final PrintWriter writer = response.getWriter();

            writer.flush();
            writer.close();
            return;
        }

        AbstractHTTPResponseRenderer renderer = context.getRenderer();

        if (null == renderer) {
            renderer = new HTTP404Renderer();
        }

        renderer.render(context);
    }
}
