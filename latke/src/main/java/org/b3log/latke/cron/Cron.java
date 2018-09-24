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
package org.b3log.latke.cron;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

/**
 * A cron job is a scheduled task, it will invoke {@link #url a URL} via an HTTP GET request, at a given time of day.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.0.0, Sep 24, 2018
 */
public final class Cron extends TimerTask {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Cron.class);

    /**
     * The URL this cron job to invoke.
     */
    private String url;

    /**
     * Description of this cron job.
     */
    private String description;

    /**
     * Schedule of this cron job.
     *
     * <p>
     * Available format: <em>every N (hours|minutes|seconds)</em>, for examples:
     * <ul>
     * <li>every 12 hours</li>
     * <li>every 10 minutes</li>
     * <li>every 30 seconds</li>
     * </ul>
     * </p>
     */
    private String schedule;

    /**
     * Time in milliseconds between successive task executions.
     */
    private long period;

    /**
     * Timeout of this cron job executing in milliseconds.
     */
    private int timeout;

    /**
     * Delay of this cron job the first executing in milliseconds.
     */
    private long delay;

    /**
     * Logging level.
     */
    private Level loggingLevel;

    /**
     * Count of executions.
     */
    private long execCount;

    /**
     * Count of successful executions.
     */
    private long execSuccCount;

    /**
     * Constructs a cron job with the specified URL, description, schedule, timeout, delay and logging level.
     *
     * @param url          the specified URL
     * @param description  the specified description
     * @param schedule     the specified schedule
     * @param timeout      the specified timeout
     * @param delay        the specified delay
     * @param loggingLevel the specified logging level
     */
    public Cron(final String url, final String description, final String schedule, final int timeout, final long delay, final Level loggingLevel) {
        this.url = url;
        this.description = description;
        this.schedule = schedule;
        this.timeout = timeout;
        this.delay = delay;
        this.loggingLevel = loggingLevel;

        parse(schedule);
    }

    @Override
    public void run() {
        try {
            LOGGER.log(loggingLevel, "Executing scheduled task....");

            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("User-Agent", Latkes.USER_AGENT);
            String content;
            try (final InputStream is = conn.getInputStream()) {
                content = IOUtils.toString(is, "UTF-8");
            }
            conn.disconnect();

            LOGGER.log(loggingLevel, "Executed scheduled task [url=" + url + ", response=" + content + "]");
            execSuccCount++;
        } catch (final Exception e) {
            if (1 < execCount) {
                LOGGER.log(Level.ERROR, "Scheduled task execute failed [" + url + ", timeout=" + timeout + "]", e);
            }
        } finally {
            execCount++;
        }
    }

    /**
     * Parses the specified schedule into {@link #period execution period}.
     *
     * @param schedule the specified schedule
     */
    private void parse(final String schedule) {
        final int num = Integer.valueOf(StringUtils.substringBetween(schedule, " ", " "));
        final String timeUnit = StringUtils.substringAfterLast(schedule, " ");

        LOGGER.log(Level.TRACE, "Parsed a cron job [schedule={0}]: [num={1}, timeUnit={2}, description={3}], ",
                new Object[]{schedule, num, timeUnit, description});

        if ("hours".equals(timeUnit)) {
            period = num * 60 * 60 * 1000;
        } else if ("minutes".equals(timeUnit)) {
            period = num * 60 * 1000;
        } else if ("seconds".equals(timeUnit)) {
            period = num * 1000;
        }
    }

    /**
     * Gets the period.
     *
     * @return period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Gets the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the schedule.
     *
     * @return schedule
     */
    public String getSchedule() {
        return schedule;
    }

    /**
     * Gets the URL.
     *
     * @return URL
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the URL with the specified URL.
     *
     * @param url the specified URL
     */
    public void setURL(final String url) {
        this.url = url;
    }

    /**
     * Gets the timeout.
     *
     * @return timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout with the specified timeout.
     *
     * @param timeout the specified timeout
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the delay.
     *
     * @return delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Sets the delay with the specified delay.
     *
     * @param delay the specified delay
     */
    public void setDelay(final long delay) {
        this.delay = delay;
    }

    /**
     * Gets the count of executions.
     *
     * @return count of executions
     */
    public long getExecCount() {
        return execCount;
    }

    /**
     * Gets the count of successful executions.
     *
     * @return count of successful executions
     */
    public long getExecSuccCount() {
        return execSuccCount;
    }
}
