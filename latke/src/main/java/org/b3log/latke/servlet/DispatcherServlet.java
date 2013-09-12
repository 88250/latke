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


import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.handler.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * NEW core dispatch-controller for HTTP request dispatching .
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
    private static final Logger LOGGER = Logger.getLogger(HTTPRequestDispatcher.class.getName());

    private static final List<Ihandler> sysHandler = new ArrayList<Ihandler>();

    @Override
    public void init() throws ServletException {

        sysHandler.add(new StaticResourceHandler(getServletContext()));
        sysHandler.add(new RequestMatchHandler());
        sysHandler.add(new AdviceHandler());
        sysHandler.add(new MethodInvokeHandler());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HTTPRequestContext httpRequestContext = new HTTPRequestContext();

        httpRequestContext.setRequest(req);
        httpRequestContext.setResponse(resp);
        HttpControl httpControl = new HttpControl(sysHandler.iterator(), httpRequestContext);

        httpControl.nextHandler();
    }
}
