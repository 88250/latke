/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.ioc;


import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.b3log.latke.ioc.config.BeanModule;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.*;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;


/**
 * The Latke bean lifecycle functions facade.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Mar 30, 2010
 */
public final class Lifecycle {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Lifecycle.class);

    /**
     * Latke bean manager.
     */
    private static LatkeBeanManager beanManager;

    /**
     * Session contexts.
     */
    private static Map<String, SessionContext> sessionContexts = new ConcurrentHashMap<String, SessionContext>();

    /**
     * Application context.
     */
    private static ApplicationContext applicationContext = new ApplicationContext();

    /**
     * Session context of the current thread.
     */
    private static ThreadLocal<SessionContext> sessionContext = new ThreadLocal<SessionContext>();

    /**
     * Request context of the current thread.
     */
    private static ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>();

    /**
     * Private constructor.
     */
    private Lifecycle() {}

    /**
     * Starts the application with the specified bean class and bean modules.
     * 
     * @param classes the specified bean class, nullable
     * @param beanModule the specified bean modules 
     */
    public static void startApplication(final Collection<Class<?>> classes, final BeanModule... beanModule) {
        LOGGER.log(Level.DEBUG, "Initializing Latke IoC container");

        beanManager = LatkeBeanManagerImpl.getInstance();

        applicationContext.setActive(true);

        beanManager.addContext(applicationContext);
        final Configurator configurator = beanManager.getConfigurator();

        if (null != classes && !classes.isEmpty()) {
            configurator.createBeans(classes);
        }

        if (null != beanModule && 0 < beanModule.length) {
            for (int i = 0; i < beanModule.length; i++) {
                configurator.addModule(beanModule[i]);
            }
        }

        LOGGER.log(Level.DEBUG, "Initialized Latke IoC container");
    }

    /**
     * Ends the application.
     */
    public static void endApplication() {
        endSession();
        endRequest();

        final ApplicationContext applicationCxt = getApplicationContext();

        applicationCxt.destroy();

        sessionContexts.clear();

        beanManager.clearContexts();

        LOGGER.log(Level.DEBUG, "Latke IoC container ended");
    }

    /**
     * Starts a session with the specified HTTP session.
     * 
     * @param httpSession the specified HTTP session
     */
    public static void startSession(final HttpSession httpSession) {
        final String sessionId = httpSession.getId();

        SessionContext currentSessionContext = sessionContexts.get(sessionId);

        if (currentSessionContext == null) {
            currentSessionContext = new SessionContext();
            sessionContexts.put(sessionId, currentSessionContext);
        }

        currentSessionContext.setActive(true);
        sessionContext.set(currentSessionContext);
        beanManager.addContext(sessionContext.get());

        LOGGER.info("Session started!");
    }

    /**
     * Ends a session.
     */
    public static void endSession() {
        final SessionContext sessionCxt = getSessionContext();

        if (null == sessionCxt) {
            return;
        }

        sessionCxt.destroy();
        sessionContext.remove();
        // TODO: remove context from bean manager.

        LOGGER.info("Session ended!");
    }

    /**
     * Starts a request with the specified servlet request event.
     * 
     * @param servletRequestEvent the specified servlet request event
     */
    public static void startRequest(final ServletRequestEvent servletRequestEvent) {
        requestContext.set(new RequestContext());
        requestContext.get().setActive(true);

        if (servletRequestEvent != null) {
            final HttpServletRequest request = (HttpServletRequest) servletRequestEvent.getServletRequest();

            if (request != null) {
                // reinitializes thread local for session
                final HttpSession session = request.getSession(false);

                if (session != null) {
                    startSession(session);
                }
            }
        }

        // TODO: beanManager.addContext(requestContext.get());

        LOGGER.info("Request started!");
    }

    /**
     * Ends a request.
     */
    public static void endRequest() {
        final RequestContext requestCxt = getRequestContext();

        if (null == requestCxt) {
            return;
        }

        requestCxt.destroy();
        requestContext.remove();
        // TODO: remove context from bean manger.

        LOGGER.info("Request ended!");
    }

    /**
     * Gets the current context with the specified scope type.
     * 
     * @param scopeType the specified scope type
     * @return the current context
     */
    public static Context getCurrentContext(final Class<? extends Annotation> scopeType) {
        Context ret = null;

        if (scopeType.equals(RequestScoped.class)) {
            ret = getRequestContext();
        } else if (scopeType.equals(SessionScoped.class)) {
            ret = getSessionContext();
        } else if (scopeType.equals(ApplicationScoped.class)) {
            ret = getApplicationContext();
        }

        return ret;
    }

    /**
     * Gets the application context.
     * 
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the session context of the current thread.
     * 
     * @return session context
     */
    public static SessionContext getSessionContext() {
        return sessionContext.get();
    }

    /**
     * Gets the request context of the current thread.
     * 
     * @return request context
     */
    public static RequestContext getRequestContext() {
        return requestContext.get();
    }

    /**
     * Gets bean manager.
     * 
     * @return bean manager
     */
    public static LatkeBeanManager getBeanManager() {
        return beanManager;
    }
}
