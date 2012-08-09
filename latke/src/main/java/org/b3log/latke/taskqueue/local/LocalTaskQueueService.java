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
package org.b3log.latke.taskqueue.local;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.taskqueue.Queue;
import org.b3log.latke.taskqueue.TaskQueueService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Local task queue service.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Apr 5, 2012
 */
public final class LocalTaskQueueService implements TaskQueueService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LocalTaskQueueService.class.getName());

    /**
     * the queueMap to hold all queue.
     */
    private static Map<String, Queue> queueMap = new Hashtable<String, Queue>();

    /**
     * reading config.
     */
    static {
        final String webRoot = AbstractServletListener.getWebRoot();
        final File queueXml = new File(webRoot
                + File.separator + "WEB-INF" + File.separator + "queue.xml");

        if (!queueXml.exists()) {
            LOGGER.log(Level.INFO, "Not found queue, no cron jobs need to schedule");
        }

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(queueXml);
            final Element root = document.getDocumentElement();
            root.normalize();

            final NodeList queueRoot = root.getElementsByTagName("queue-entries");

            LOGGER.log(Level.CONFIG, "Reading tasks: ");
            for (int i = 0; i < queueRoot.getLength(); i++) {
                final Element queueNode = (Element) queueRoot.item(i);
                final String queueName = queueNode.getElementsByTagName("name").item(0).getTextContent();
                final Element rparamNode =
                        (Element) queueNode.getElementsByTagName("retry-parameters").item(0);
                final String retryLimit = rparamNode.getElementsByTagName("task-retry-limit").item(0).getTextContent();

                queueMap.put(queueName, new LocalTaskQueue(Integer.valueOf(retryLimit)));

            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Reads queue.xml failed", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public Queue getQueue(final String queueName) {
        return queueMap.get(queueName);
    }
}
