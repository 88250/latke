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
package org.b3log.latke.cron;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.servlet.AbstractServletListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Cron jobs service.
 * 
 * <p>
 * Loads cron jobs configurations from cron.xml and schedules tasks.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, May 2, 2012
 */
public final class CronService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CronService.class.getName());
    /**
     * Cron jobs.
     */
    private static final List<Cron> CRONS = new ArrayList<Cron>();

    /**
     * Constructs cron jobs and schedules them.
     */
    public static void start() {
        LOGGER.info("Constructing Cron Service....");

        CRONS.clear();

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        try {
            switch (runtimeEnv) {
                case LOCAL:
                    loadCronXML();

                    for (final Cron cron : CRONS) {
                        cron.setURL(Latkes.getServer() + Latkes.getContextPath() + cron.getUrl());

                        final Timer timer = new Timer();
                        timer.scheduleAtFixedRate(cron, Cron.SIXTY * Cron.THOUSAND, cron.getPeriod());

                        LOGGER.log(Level.FINER, "Scheduled a cron job[url={0}]", cron.getUrl());
                    }

                    LOGGER.log(Level.FINER, "[{0}] cron jobs", CRONS.size());

                    break;
                case GAE:
                    break;
                default:
                    throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize Cron Service!", e);
        }

        LOGGER.info("Constructed Cron Service");
    }

    /**
     * Loads cron.xml.
     */
    private static void loadCronXML() {
        final String webRoot = AbstractServletListener.getWebRoot();
        final File cronXML = new File(webRoot + File.separator + "WEB-INF" + File.separator + "cron.xml");

        if (!cronXML.exists()) {
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

            LOGGER.log(Level.CONFIG, "Reading cron jobs: ");
            for (int i = 0; i < crons.getLength(); i++) {
                final Element cronElement = (Element) crons.item(i);
                final Element urlElement = (Element) cronElement.getElementsByTagName("url").item(0);
                final Element descriptionElement = (Element) cronElement.getElementsByTagName("description").item(0);
                final Element scheduleElement = (Element) cronElement.getElementsByTagName("schedule").item(0);

                final String url = urlElement.getTextContent();
                final String description = descriptionElement.getTextContent();
                final String schedule = scheduleElement.getTextContent();
                LOGGER.log(Level.CONFIG, "Cron[url={0}, description={1}, schedule={2}]", new Object[]{url, description, schedule});

                CRONS.add(new Cron(url, description, schedule));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Reads cron.xml failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Private default constructor.
     */
    private CronService() {
    }
}
