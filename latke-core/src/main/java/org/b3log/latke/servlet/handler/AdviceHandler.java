/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.servlet.handler;

import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.servlet.renderer.AbstractResponseRenderer;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * The handler to do the advice work in configs.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Dec 3, 2018
 */
public class AdviceHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdviceHandler.class);

    @Override
    public void handle(final RequestContext context, final HttpControl httpControl) throws Exception {
        final MatchResult result = (MatchResult) httpControl.data(RequestDispatchHandler.MATCH_RESULT);
        final Map<String, Object> args = (Map<String, Object>) httpControl.data(ArgsHandler.PREPARE_ARGS);

        final ProcessorInfo processorInfo = result.getProcessorInfo();
        final List<AbstractResponseRenderer> rendererList = result.getRendererList();

        try {
            final List<BeforeRequestProcessAdvice> beforeRequestProcessAdvices = processorInfo.getBeforeRequestProcessAdvices();
            for (final BeforeRequestProcessAdvice beforeRequestProcessAdvice : beforeRequestProcessAdvices) {
                beforeRequestProcessAdvice.doAdvice(context, args);
            }
        } catch (final RequestProcessAdviceException e) {
            final JSONObject exception = e.getJsonObject();
            final String msg = exception.optString(Keys.MSG);
            LOGGER.log(Level.WARN, "Occurred an exception before request processing [errMsg={0}]", msg);

            final int statusCode = exception.optInt(Keys.STATUS_CODE, -1);
            if (-1 != statusCode && HttpServletResponse.SC_OK != statusCode) {
                final HttpServletResponse response = context.getResponse();
                response.sendError(statusCode, msg);
            } else {
                final JsonRenderer ret = new JsonRenderer();
                ret.setJSONObject(exception);
                context.setRenderer(ret);
            }

            return;
        }

        for (AbstractResponseRenderer renderer : rendererList) {
            renderer.preRender(context, args);
        }

        httpControl.nextHandler();

        for (int j = rendererList.size() - 1; j >= 0; j--) {
            rendererList.get(j).postRender(context, httpControl.data(MethodInvokeHandler.INVOKE_RESULT));
        }

        final List<AfterRequestProcessAdvice> afterRequestProcessAdvices = processorInfo.getAfterRequestProcessAdvices();
        for (final AfterRequestProcessAdvice afterRequestProcessAdvice : afterRequestProcessAdvices) {
            afterRequestProcessAdvice.doAdvice(context, httpControl.data(MethodInvokeHandler.INVOKE_RESULT));
        }
    }
}
