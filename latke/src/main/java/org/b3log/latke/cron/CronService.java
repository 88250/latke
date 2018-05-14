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

import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Cron jobs service.
 * <p>
 * Loads cron jobs configurations from cron.xml and schedules tasks.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.2.0, May 14, 2018
 */
public final class CronService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CronService.class);

    /**
     * Cron jobs.
     */
    private static final List<Cron> CRONS = new ArrayList<>();

    /**
     * Timers.
     */
    private static final List<Timer> TIMERS = new ArrayList<>();

    /**
     * Cron setup interval in seconds.
     */
    private static final int SETUP_INTERVAL = 5;

    /**
     * Private default constructor.
     */
    private CronService() {
    }

    /**
     * Constructs cron jobs and schedules them.
     */
    public static void start() {
        new Thread(() -> {
            LOGGER.info("Constructing cron service....");

            try {
                loadCronXML();

                for (final Cron cron : CRONS) {
                    final Timer timer = new Timer();
                    TIMERS.add(timer);

                    cron.setURL(Latkes.getServePath() + cron.getURL());
                    timer.scheduleAtFixedRate(cron, Cron.TEN * Cron.THOUSAND, cron.getPeriod());

                    LOGGER.log(Level.DEBUG, "Scheduled a cron job[url={0}]", cron.getURL());

                    TimeUnit.SECONDS.sleep(SETUP_INTERVAL);
                }

                LOGGER.log(Level.DEBUG, "[{0}] cron jobs totally", CRONS.size());
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Can not initialize Cron Service!", e);

                throw new IllegalStateException(e);
            }

            LOGGER.info("Constructed Cron Service");
        }).start();
    }

    /**
     * Stops all cron jobs and clears cron job list.
     */
    public static void shutdown() {
        CRONS.clear();

        for (final Timer timer : TIMERS) {
            timer.cancel();
        }
        TIMERS.clear();

        LOGGER.log(Level.INFO, "Closed cron service");
    }

    /**
     * Loads cron.xml.
     */
    private static void loadCronXML() {
        final File cronXML = Latkes.getWebFile("/WEB-INF/cron.xml");

        if (null == cronXML || !cronXML.exists()) {
            LOGGER.log(Level.INFO, "Not found cron.xml, no cron jobs need to schedule");

            return;
        }

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(cronXML);
            final Element root = document.getDocumentElement();

            root.normalize();

            final NodeList crons = root.getElementsByTagName("cron");

            for (int i = 0; i < crons.getLength(); i++) {
                final Element cronElement = (Element) crons.item(i);
                final Element urlElement = (Element) cronElement.getElementsByTagName("url").item(0);
                final Element descriptionElement = (Element) cronElement.getElementsByTagName("description").item(0);
                final Element scheduleElement = (Element) cronElement.getElementsByTagName("schedule").item(0);

                final String url = urlElement.getTextContent();
                final String description = descriptionElement.getTextContent();
                final String schedule = scheduleElement.getTextContent();

                CRONS.add(new Cron(url, description, schedule));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads cron.xml failed", e);
            throw new RuntimeException(e);
        }
    }
}
