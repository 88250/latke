/*
 * Copyright (c) 2015, b3log.org
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.taskqueue.Queue;
import org.b3log.latke.taskqueue.TaskQueueService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Local task queue service.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 15, 2012
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
     * Determines whether the queue has been initialized.
     */
    private static boolean inited;

    /**
     * Initializes the queue.
     */
    public static void init() {
        final File queueXml = Latkes.getWebFile("/WEB-INF/queue.xml");

        if (null == queueXml || !queueXml.exists()) {
            LOGGER.log(Level.INFO, "Not found [queue.xml], assuming queue tasks");

            return;
        }

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(queueXml);
            final Element root = document.getDocumentElement();

            root.normalize();

            final NodeList queueRoot = root.getElementsByTagName("queue-entries");

            LOGGER.log(Level.DEBUG, "Reading tasks: ");
            for (int i = 0; i < queueRoot.getLength(); i++) {
                final Element queueNode = (Element) queueRoot.item(i);
                final String queueName = queueNode.getElementsByTagName("name").item(0).getTextContent();
                final Element rparamNode = (Element) queueNode.getElementsByTagName("retry-parameters").item(0);
                final String retryLimit = rparamNode.getElementsByTagName("task-retry-limit").item(0).getTextContent();

                queueMap.put(queueName, new LocalTaskQueue(Integer.valueOf(retryLimit)));

            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads queue.xml failed", e);
            throw new RuntimeException(e);
        }

        inited = true;
    }

    @Override
    public Queue getQueue(final String queueName) {
        if (!inited) {
            init();
        }

        return queueMap.get(queueName);
    }
}
