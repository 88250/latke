package org.b3log.latke.demo.hello;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.freemarker.Templates;

/**
 * Hello servlet listener.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jan 8, 2016
 */
public class HelloServletListener extends AbstractServletListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HelloServletListener.class.getName());

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.setScanPath("org.b3log.latke.demo.hello");
        super.contextInitialized(servletContextEvent);

        final ServletContext servletContext = servletContextEvent.getServletContext();

        try {
            Templates.MAIN_CFG.setServletContextForTemplateLoading(servletContext, "skins/classic");
            Templates.MOBILE_CFG.setServletContextForTemplateLoading(servletContext, "skins/classic");
        } catch (final Exception e) {
            throw new IllegalStateException("Can not load the default template directory [skins/classic]");
        }

        LOGGER.info("Initialized the context");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);

        LOGGER.info("Destroyed the context");
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        super.sessionDestroyed(httpSessionEvent);
    }

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        super.requestDestroyed(servletRequestEvent);
    }
}
