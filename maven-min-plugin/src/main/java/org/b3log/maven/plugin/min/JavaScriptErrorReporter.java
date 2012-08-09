/*
 * Copyright (c) 2011, 2012, B3log Team
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
package org.b3log.maven.plugin.min;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * Reports any error occurring during JavaScript sources compression.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 26, 2011
 */
public final class JavaScriptErrorReporter implements ErrorReporter {

    private Log logger;
    private String filename;

    public JavaScriptErrorReporter(final Log logger, final String fileName) {
        this.logger = logger;
        this.filename = fileName;
    }

    @Override
    public void warning(final String message, final String sourceName,
                        final int line, final String lineSource,
                        final int lineOffset) {
        if (line < 0) {
            logger.warn(message);
        } else {
            logger.warn("[" + filename + ":" + line + "] " + message);
        }
    }

    @Override
    public void error(final String message, final String sourceName,
                      final int line, final String lineSource,
                      final int lineOffset) {
        if (line < 0) {
            logger.error(message);
        } else {
            logger.error("[" + filename + ":" + line + "] " + message);
        }
    }

    @Override
    public EvaluatorException runtimeError(final String message,
                                           final String sourceName,
                                           final int line,
                                           final String lineSource,
                                           final int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);

        return new EvaluatorException(message);
    }
}
