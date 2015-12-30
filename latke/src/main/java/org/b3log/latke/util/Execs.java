/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;


/**
 * Command execution utilities.
 *
 * <p>
 * Uses {@link Runtime#exec(java.lang.String)} to execute command, to avoid the execution be blocked, starts a thread to read error stream
 * from th executing sub process.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Nov 26, 2013
 * @since 0.1.0
 */
public final class Execs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Execs.class);

    /**
     * Private constructor.
     */
    private Execs() {}

    /**
     * Executes the specified command.
     *
     * @param cmd the specified command
     * @return execution output, returns {@code null} if execution failed
     */
    public static String exec(final String cmd) {
        InputStream inputStream = null;

        try {
            final Process p = Runtime.getRuntime().exec(cmd);

            // Starts a thread for error stream
            final Thread t = new Thread(new InputStreamRunnable(p.getErrorStream()));

            t.start();

            inputStream = p.getInputStream();
            final String result = IOUtils.toString(inputStream);

            inputStream.close();
            p.destroy();

            return result;
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Executes command [" + cmd + "] failed", e);

            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Executes the specified commands.
     *
     * @param cmds the specified commands
     * @return execution output, returns {@code null} if execution failed
     */
    public static String exec(final String[] cmds) {
        InputStream inputStream = null;

        try {
            final Process p = Runtime.getRuntime().exec(cmds);

            // Starts a thread for error stream
            final Thread t = new Thread(new InputStreamRunnable(p.getErrorStream()));

            t.start();

            inputStream = p.getInputStream();
            final String result = IOUtils.toString(inputStream);

            inputStream.close();
            p.destroy();

            return result;
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Executes command [" + cmds + "] failed", e);

            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Input stream handle thread.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, May 8, 2013
     * @since 0.1.0
     */
    private static class InputStreamRunnable implements Runnable {

        /**
         * Reader.
         */
        private BufferedReader bufferedReader;

        /**
         * Constructs a input stream handle thread with the specified input stream.
         *
         * @param is the specified input stream
         */
        public InputStreamRunnable(final InputStream is) {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalStateException("Constructs input stream handle thread failed", e);
            }
        }

        @Override
        public void run() {
            try {
                String s;
                
                while (null != (s = bufferedReader.readLine())) {}

                IOUtils.closeQuietly(bufferedReader);
            } catch (final IOException e) {}
        }
    }
}
