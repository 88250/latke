/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.urlfetch.bae;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;

/**
 * Baidu App Engine URL fetch service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 4, 2012
 */
public final class BAEURLFetchService implements URLFetchService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BAEURLFetchService.class.getName());

    private URLFetchService svc;

    /**
     * Constructs a BAE URL fetch service.
     */
    public BAEURLFetchService() {
        try {
            svc = ((Class<URLFetchService>) Class.forName("org.b3log.latke.urlfetch.local.LocalURLFetchService")).newInstance();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public HTTPResponse fetch(final HTTPRequest request) throws IOException {
        return svc.fetch(request);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * <b>Note</b>: Dose <em>NOT</em> support async URL fetch at present, calls this method is equivalent to call 
     * {@link #fetch(org.b3log.latke.urlfetch.HTTPRequest)}.
     * </p>
     */
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
