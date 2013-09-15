package org.b3log.latke.servlet.handler;

import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.HTTP404Renderer;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * User: mainlove
 * Date: 13-9-15
 * Time: 下午5:38
 */
public class ResultRenderHandler implements Ihandler {

    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {

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
