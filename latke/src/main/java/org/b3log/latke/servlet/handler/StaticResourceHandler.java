package org.b3log.latke.servlet.handler;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestDispatcher;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.util.StaticResources;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午2:56
 */
public class StaticResourceHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticResourceHandler.class.getName());

    /**
     * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish.
     */
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

    /**
     * Default Servlet name used by Google App Engine.
     */
    private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";

    /**
     * Default Servlet name used by Resin.
     */
    private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

    /**
     * Default Servlet name used by WebLogic.
     */
    private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

    /**
     * Default Servlet name used by WebSphere.
     */
    private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

    /**
     * the holder of All option Servlet Name.
     */
    private static final String[] OPTION_SERVLET_NAME = new String[]{
            COMMON_DEFAULT_SERVLET_NAME, GAE_DEFAULT_SERVLET_NAME, RESIN_DEFAULT_SERVLET_NAME, WEBLOGIC_DEFAULT_SERVLET_NAME,
            WEBSPHERE_DEFAULT_SERVLET_NAME};

    /**
     * default servlet which container provide to resolve static resource.
     */
    private RequestDispatcher requestDispatcher;


    private String defaultServletName;


    public StaticResourceHandler(ServletContext servletContext) {

        for (String servletName : OPTION_SERVLET_NAME) {
            requestDispatcher = servletContext.getNamedDispatcher(servletName);
            if (requestDispatcher != null) {
                defaultServletName = servletName;
                break;
            }
        }
        if (requestDispatcher == null) {
            throw new IllegalStateException(
                    "Unable to locate the default servlet for serving static content. "
                            + "Please report this bug on https://github.com/b3log/b3log-latke/issues/new");
        }

        LOGGER.log(Level.DEBUG, "The default servlet for serving static resource is [{0}]", defaultServletName);
    }

    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {

        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        if (StaticResources.isStatic(request)) {
            if (null == requestDispatcher) {
                throw new IllegalStateException(
                        "A RequestDispatcher could not be located for the default servlet [" + this.defaultServletName + "]");
            }
            requestDispatcher.forward(request, response);
        } else {
            httpControl.nextHandler();
        }
    }
}
