/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * LianDi is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.util;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Command execution utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 25, 2018
 * @since 1.0.0
 */
public final class Execs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Execs.class);

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
