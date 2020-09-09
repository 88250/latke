/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.http;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.http.handler.ContextHandlerMeta;
import org.b3log.latke.http.handler.InvokeHandler;
import org.b3log.latke.http.handler.RouteHandler;
import org.b3log.latke.http.handler.StaticResourceHandler;
import org.b3log.latke.http.renderer.AbstractResponseRenderer;
import org.b3log.latke.http.renderer.Http404Renderer;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatch-controller for HTTP request dispatching.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 10, 2020
 * @since 2.4.34
 */
public final class Dispatcher {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Dispatcher.class);

    /**
     * Handlers.
     */
    public static final List<Handler> HANDLERS = new ArrayList<>();

    /**
     * Start request handler, handle before all handlers.
     */
    public static Handler startRequestHandler;

    /**
     * End request handler, handle after all handlers.
     */
    public static Handler endRequestHandler;

    static {
        HANDLERS.add(new StaticResourceHandler());
        HANDLERS.add(new RouteHandler());
        HANDLERS.add(new InvokeHandler());
    }

    /**
     * Handle flow.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return context
     */
    public static RequestContext handle(final Request request, final Response response) {
        final RequestContext ret = new RequestContext(request, response);
        response.context = request.context = ret;
        ret.addHandlers(HANDLERS);

        if (null != startRequestHandler) {
            try {
                startRequestHandler.handle(ret);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Start request handle failed", e);
            }
        }

        ret.handle();
        renderResponse(ret);

        if (null != endRequestHandler) {
            try {
                endRequestHandler.handle(ret);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "End request handle failed", e);
            }
        }

        return ret;
    }

    /**
     * Renders HTTP response.
     *
     * @param context {@link RequestContext}
     */
    public static void renderResponse(final RequestContext context) {
        final Response response = context.getResponse();
        if (response.isCommitted()) { // Response sends redirect or error
            return;
        }

        AbstractResponseRenderer renderer = context.getRenderer();
        if (null == renderer) {
            renderer = new Http404Renderer();
        }
        renderer.render(context);
    }

    /**
     * Error handle router.
     */
    static Router errorHandleRouter;

    /**
     * Error status routing.
     *
     * @param uriTemplate the specified request URI template
     * @param handler     the specified handler
     */
    public static void error(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = group();
        if (0 < ArrayUtils.getLength(middlewares)) {
            group.middlewares.addAll(Arrays.asList(middlewares));
        }
        errorHandleRouter = group.get(uriTemplate, handler).routers.get(0);
    }

    /**
     * WebSocket channels.
     */
    static Map<String, WebSocketChannel> webSocketChannels = new ConcurrentHashMap<>();

    /**
     * WebSocket channel routing.
     *
     * @param uri              the specified URI
     * @param webSocketChannel the specified WebSocket channel
     */
    public static void webSocket(final String uri, final WebSocketChannel webSocketChannel) {
        webSocketChannels.put(Latkes.getContextPath() + uri, webSocketChannel);
    }

    /**
     * Router groups.
     */
    static List<RouterGroup> routerGroups = new ArrayList<>();

    /**
     * Creates a new router group.
     *
     * @return router group
     */
    public static RouterGroup group() {
        final RouterGroup ret = new RouterGroup();
        routerGroups.add(ret);

        return ret;
    }

    /**
     * Performs mapping for all routers.
     */
    public static void mapping() {
        for (final RouterGroup group : routerGroups) {
            for (final Router router : group.routers) {
                final ContextHandlerMeta contextHandlerMeta = router.toContextHandlerMeta();
                RouteHandler.addContextHandlerMeta(contextHandlerMeta);
            }
        }
    }

    /**
     * GET routing.
     *
     * @param uriTemplate the specified URI template
     * @param handler     the specified handler
     * @param middlewares the specified middlewares
     */
    public static void get(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = newGroupBindMiddlewares(middlewares);
        group.get(uriTemplate, handler);
    }

    /**
     * POST routing.
     *
     * @param uriTemplate the specified URI template
     * @param handler     the specified handler
     * @param middlewares the specified middlewares
     */
    public static void post(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = newGroupBindMiddlewares(middlewares);
        group.post(uriTemplate, handler);
    }

    /**
     * PUT routing.
     *
     * @param uriTemplate the specified URI template
     * @param handler     the specified handler
     * @param middlewares the specified middlewares
     */
    public static void put(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = newGroupBindMiddlewares(middlewares);
        group.put(uriTemplate, handler);
    }

    /**
     * DELETE routing.
     *
     * @param uriTemplate the specified URI template
     * @param handler     the specified handler
     * @param middlewares the specified middlewares
     */
    public static void delete(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = newGroupBindMiddlewares(middlewares);
        group.delete(uriTemplate, handler);
    }

    /**
     * OPTIONS routing.
     *
     * @param uriTemplate the specified URI template
     * @param handler     the specified handler
     * @param middlewares the specified middlewares
     */
    public static void options(final String uriTemplate, final Handler handler, final Handler... middlewares) {
        final RouterGroup group = newGroupBindMiddlewares(middlewares);
        group.options(uriTemplate, handler);
    }

    private static RouterGroup newGroupBindMiddlewares(final Handler... middlewares) {
        final RouterGroup ret = group();
        if (0 < ArrayUtils.getLength(middlewares)) {
            ret.middlewares.addAll(Arrays.asList(middlewares));
        }

        return ret;
    }

    /**
     * Router group.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Feb 9, 2020
     */
    public static class RouterGroup {
        private final List<Handler> middlewares = new ArrayList<>();
        private final List<Router> routers = new ArrayList<>();

        /**
         * Bind middlewares.
         *
         * @param handler  the specified middleware
         * @param handlers the specified middlewares
         * @return this group
         */
        public RouterGroup middlewares(final Handler handler, final Handler... handlers) {
            this.middlewares.add(handler);
            this.middlewares.addAll(Arrays.asList(handlers));

            return this;
        }

        /**
         * Registers a new router.
         *
         * @return router
         */
        public Router router() {
            final Router ret = new Router();
            routers.add(ret);
            ret.group = this;

            return ret;
        }

        /**
         * HTTP OPTIONS routing.
         *
         * @param uriTemplate the specified request URI template
         * @param handler     the specified handler
         * @return router
         */
        public RouterGroup options(final String uriTemplate, final Handler handler) {
            final Router route = router();
            route.options(uriTemplate, handler);

            return route.group;
        }

        /**
         * HTTP DELETE routing.
         *
         * @param uriTemplate the specified request URI template
         * @param handler     the specified handler
         * @return router
         */
        public RouterGroup delete(final String uriTemplate, final Handler handler) {
            final Router route = router();
            route.delete(uriTemplate, handler);

            return route.group;
        }

        /**
         * HTTP PUT routing.
         *
         * @param uriTemplate the specified request URI template
         * @param handler     the specified handler
         * @return router
         */
        public RouterGroup put(final String uriTemplate, final Handler handler) {
            final Router router = router();
            router.put(uriTemplate, handler);

            return router.group;
        }

        /**
         * HTTP GET routing.
         *
         * @param uriTemplate the specified request URI template
         * @param handler     the specified handler
         * @return router
         */
        public RouterGroup get(final String uriTemplate, final Handler handler) {
            final Router router = router();
            router.get(uriTemplate, handler);

            return router.group;
        }

        /**
         * HTTP POST routing.
         *
         * @param uriTemplate the specified request URI template
         * @param handler     the specified handler
         * @return router
         */
        public RouterGroup post(final String uriTemplate, final Handler handler) {
            final Router router = router();
            router.post(uriTemplate, handler);

            return router.group;
        }

    }

    /**
     * Programmatic router.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Dec 2, 2018
     */
    public static class Router {
        private RouterGroup group;
        private final List<String> uriTemplates = new ArrayList<>();
        private final List<HttpMethod> httpRequestMethods = new ArrayList<>();
        private Handler handler;
        private Method method;

        public void options(final String uriTemplate, final Handler handler) {
            options(new String[]{uriTemplate}, handler);
        }

        public void options(final String[] uriTemplates, final Handler handler) {
            options().uris(uriTemplates).handler(handler);
        }

        public void delete(final String uriTemplate, final Handler handler) {
            delete(new String[]{uriTemplate}, handler);
        }

        public void delete(final String[] uriTemplates, final Handler handler) {
            delete().uris(uriTemplates).handler(handler);
        }

        public void put(final String uriTemplate, final Handler handler) {
            put(new String[]{uriTemplate}, handler);
        }

        public void put(final String[] uriTemplates, final Handler handler) {
            put().uris(uriTemplates).handler(handler);
        }

        public void post(final String uriTemplate, final Handler handler) {
            post(new String[]{uriTemplate}, handler);
        }

        public void post(final String[] uriTemplates, final Handler handler) {
            post().uris(uriTemplates).handler(handler);
        }

        public void get(final String uriTemplate, final Handler handler) {
            get(new String[]{uriTemplate}, handler);
        }

        public void get(final String[] uriTemplates, final Handler handler) {
            get().uris(uriTemplates).handler(handler);
        }

        public Router uris(final String[] uriTemplates) {
            for (final String uriTemplate : uriTemplates) {
                uri(uriTemplate);
            }

            return this;
        }

        public Router uri(final String uriTemplate) {
            if (!uriTemplates.contains(uriTemplate)) {
                uriTemplates.add(uriTemplate);
            }

            return this;
        }

        public Router get() {
            if (!httpRequestMethods.contains(HttpMethod.GET)) {
                httpRequestMethods.add(HttpMethod.GET);
            }

            return this;
        }

        public Router post() {
            if (!httpRequestMethods.contains(HttpMethod.POST)) {
                httpRequestMethods.add(HttpMethod.POST);
            }

            return this;
        }

        public Router delete() {
            if (!httpRequestMethods.contains(HttpMethod.DELETE)) {
                httpRequestMethods.add(HttpMethod.DELETE);
            }

            return this;
        }

        public Router put() {
            if (!httpRequestMethods.contains(HttpMethod.PUT)) {
                httpRequestMethods.add(HttpMethod.PUT);
            }

            return this;
        }

        public Router head() {
            if (!httpRequestMethods.contains(HttpMethod.HEAD)) {
                httpRequestMethods.add(HttpMethod.HEAD);
            }

            return this;
        }

        public Router options() {
            if (!httpRequestMethods.contains(HttpMethod.OPTIONS)) {
                httpRequestMethods.add(HttpMethod.OPTIONS);
            }

            return this;
        }

        public Router trace() {
            if (!httpRequestMethods.contains(HttpMethod.TRACE)) {
                httpRequestMethods.add(HttpMethod.TRACE);
            }

            return this;
        }

        public Router handler(final Handler handler) {
            this.handler = handler;
            final Class<?> clazz = handler.getClass();
            try {
                final Serializable lambda = handler;
                // Latke 框架中 "writeReplace" 是个什么魔数？ https://ld246.com/article/1568102022352
                final Method m = clazz.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                final SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
                final String implClassName = sl.getImplClass().replaceAll("/", ".");
                final Class<?> implClass = Class.forName(implClassName);
                this.method = implClass.getDeclaredMethod(sl.getImplMethodName(), RequestContext.class);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Found lambda method reference impl method failed", e);
            }

            return this;
        }

        ContextHandlerMeta toContextHandlerMeta() {
            final ContextHandlerMeta ret = new ContextHandlerMeta();
            ret.setUriTemplates(uriTemplates.toArray(new String[0]));
            ret.setHttpMethods(httpRequestMethods.toArray(new HttpMethod[0]));
            ret.setInvokeHolder(method);
            ret.setHandler(handler);
            ret.setMiddlewares(group.middlewares);

            return ret;
        }
    }
}
