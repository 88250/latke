/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
package org.b3log.latke.demo.hello.web;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.freemarker.FreeMarkerRenderer;
import org.b3log.latke.util.Strings;

/**
 * Hello.
 * 
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Oct 9, 2012
 */
@RequestProcessor
public class HelloProcessor {

    private static final Logger LOGGER = Logger.getLogger(HelloProcessor.class.getName());

    @RequestProcessing(value = {"/", "/index", "/**/ant/*/path"}, method = HTTPRequestMethod.GET)
    public void index(final HTTPRequestContext context) {
        LOGGER.entering(HelloProcessor.class.getSimpleName(), "index");

        final AbstractFreeMarkerRenderer render = new FreeMarkerRenderer();
        context.setRenderer(render);

        render.setTemplateName("index.ftl");
        final Map<String, Object> dataModel = render.getDataModel();

        dataModel.put("greeting", "Hello, Latke!");

        LOGGER.exiting(HelloProcessor.class.getSimpleName(), "index");
    }

    @RequestProcessing(value = "/greeting", method = {HTTPRequestMethod.GET, HTTPRequestMethod.POST})
    public void greeting(final HTTPRequestContext context, final HttpServletRequest request) {
        final AbstractFreeMarkerRenderer render = new FreeMarkerRenderer();
        context.setRenderer(render);

        render.setTemplateName("hello.ftl");
        final Map<String, Object> dataModel = render.getDataModel();

        dataModel.put("time", new Date());

        final String name = request.getParameter("name");
        if (!Strings.isEmptyOrNull(name)) {
            LOGGER.log(Level.FINER, "Name[{0}]", name);
            dataModel.put("name", name);
        }
    }
}
