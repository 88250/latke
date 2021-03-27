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
package org.b3log.latke.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

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
     * Executes the specified commands with the specified timeout.
     *
     * @param cmds    the specified commands
     * @param timeout the specified timeout in milliseconds
     * @return execution output, returns {@code null} if execution failed
     */
    public static String exec(final String[] cmds, final long timeout) {
        final CommandLine cmdLine = new CommandLine(cmds[0]);
        if (1 < cmds.length) {
            for (int i = 1; i < cmds.length; i++) {
                cmdLine.addArgument(cmds[i], false);
            }
        }

        final DefaultExecutor executor = new DefaultExecutor();
        final ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
            return outputStream.toString();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Exec [" + Arrays.toString(cmds) + "] failed", e);
            return "";
        }
    }

    /**
     * Private constructor.
     */
    private Execs() {
    }
}
