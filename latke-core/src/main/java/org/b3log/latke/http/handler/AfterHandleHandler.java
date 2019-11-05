/*
 * Copyright (c) 2009-present, b3log.org
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
package org.b3log.latke.http.handler;

import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.advice.ProcessAdvice;
import org.b3log.latke.http.advice.RequestProcessAdviceException;
import org.b3log.latke.http.renderer.AbstractResponseRenderer;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.json.JSONObject;

import java.util.List;

/**
 * After processing method ({@link org.b3log.latke.http.function.ContextHandler#handle(RequestContext)} or method annotated {@link org.b3log.latke.http.annotation.RequestProcessing}).
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 3, 2019
 * @since 2.4.34
 */
public class AfterHandleHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AfterHandleHandler.class);

    @Override
    public void handle(final RequestContext context) throws Exception {
        final MatchResult result = (MatchResult) context.attr(RouteHandler.MATCH_RESULT);

        final ContextHandlerMeta contextHandlerMeta = result.getContextHandlerMeta();
        final List<AbstractResponseRenderer> rendererList = result.getRendererList();

        for (int j = rendererList.size() - 1; j >= 0; j--) {
            rendererList.get(j).postRender(context);
        }

        try {
            final List<ProcessAdvice> afterRequestProcessAdvices = contextHandlerMeta.getAfterRequestProcessAdvices();
            for (final ProcessAdvice afterRequestProcessAdvice : afterRequestProcessAdvices) {
                afterRequestProcessAdvice.doAdvice(context);
            }
        } catch (final RequestProcessAdviceException e) {
            final JSONObject exception = e.getJsonObject();
            final String msg = exception.optString(Keys.MSG);
            LOGGER.log(Level.WARN, "Occurred an exception after request processing: " + msg);

            final int statusCode = exception.optInt(Keys.STATUS_CODE, -1);
            if (-1 != statusCode && 200 != statusCode) {
                final Response response = context.getResponse();
                response.sendError(statusCode);
            } else {
                final JsonRenderer ret = new JsonRenderer();
                ret.setJSONObject(exception);
                context.setRenderer(ret);
            }

            context.abort();
        }

        context.handle();
    }
}
