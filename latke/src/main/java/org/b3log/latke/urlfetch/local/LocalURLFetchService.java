/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.urlfetch.local;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;

/**
 * Local URL fetch service.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 0.0.0.3, Aug 21, 2011
 */
public final class LocalURLFetchService implements URLFetchService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalURLFetchService.class.getName());

    @Override
    public HTTPResponse fetch(final HTTPRequest request) throws IOException {
        final HTTPRequestMethod requestMethod = request.getRequestMethod();
        if (requestMethod == null) {
            throw new IOException("RequestMethod  for URLFetch should not be null");
        }

        return UrlFetchHandlerFactory.getFetchHandler(requestMethod).doFetch(request);
    }

    @Override
    public Future<?> fetchAsync(final HTTPRequest request) {
        final FutureTask<HTTPResponse> futureTask = new FutureTask<HTTPResponse>(new Callable<HTTPResponse>() {

            @Override
            public HTTPResponse call() throws Exception {
                return fetch(request);
            }
        });

        // no pool
        futureTask.run();

        return futureTask;
    }
}
