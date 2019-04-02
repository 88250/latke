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
package org.b3log.latke.util;

import org.apache.commons.io.IOUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Command execution utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 25, 2018
 * @since 0.1.0
 */
public final class Execs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Execs.class);

    /**
     * Executes the specified command with the specified timeout.
     *
     * @param cmd     the specified command
     * @param timeout the specified timeout
     * @return execution output, returns {@code null} if execution failed
     */
    public static String exec(final String cmd, final long timeout) {
        final StringTokenizer st = new StringTokenizer(cmd);
        final String[] cmds = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            cmds[i] = st.nextToken();
        }

        return exec(cmds, timeout);
    }

    /**
     * Executes the specified commands with the specified timeout.
     *
     * @param cmds    the specified commands
     * @param timeout the specified timeout in milliseconds
     * @return execution output, returns {@code null} if execution failed
     */
    public static String exec(final String[] cmds, final long timeout) {
        try {
            final Process process = new ProcessBuilder(cmds).redirectErrorStream(true).start();
            final StringWriter writer = new StringWriter();
            new Thread(() -> {
                try {
                    IOUtils.copy(process.getInputStream(), writer, "UTF-8");
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Reads input stream failed: " + e.getMessage());
                }
            }).start();

            if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
                LOGGER.log(Level.WARN, "Executes commands [" + Arrays.toString(cmds) + "] timeout");
                process.destroy();
            }

            return writer.toString();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Executes commands [" + Arrays.toString(cmds) + "] failed", e);

            return null;
        }
    }

    /**
     * Private constructor.
     */
    private Execs() {
    }
}
