package org.b3log.latke.servlet.function;

import org.b3log.latke.servlet.HTTPRequestContext;

/**
 * Represents an request handler that accepts a context as the single input argument and returns no result.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 1, 2018
 * @since 2.4.30
 */
@FunctionalInterface
public interface ContextHandler {

    /**
     * Performs request handling with the specified context.
     *
     * @param context the specified context
     */
    void handle(final HTTPRequestContext context);
}
