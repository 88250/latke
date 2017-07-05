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
package org.b3log.latke.mail;

import org.b3log.latke.logging.Logger;

/**
 * Mail service factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.2, Jul 5, 2017
 */
public final class MailServiceFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MailServiceFactory.class.getName());

    /**
     * Mail service.
     */
    private static final MailService MAIL_SERVICE;

    static {
        LOGGER.info("Constructing mail service....");

        try {
            final Class<MailService> mailServiceClass = (Class<MailService>) Class.forName(
                    "org.b3log.latke.mail.local.LocalMailService");
            MAIL_SERVICE = mailServiceClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize mail service!", e);
        }

        LOGGER.info("Constructed mail service");
    }

    /**
     * Private default constructor.
     */
    private MailServiceFactory() {
    }

    /**
     * Gets mail service.
     *
     * @return mail service
     */
    public static MailService getMailService() {
        return MAIL_SERVICE;
    }
}
