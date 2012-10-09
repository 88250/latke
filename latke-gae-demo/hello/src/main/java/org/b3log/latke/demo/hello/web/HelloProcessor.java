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
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Oct 9, 2012
 */
@RequestProcessor
public final class HelloProcessor {

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
