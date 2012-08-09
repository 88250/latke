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
package org.b3log.latke.mail;

import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;

/**
 * Mail service factory.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
@SuppressWarnings("unchecked")
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
        LOGGER.info("Constructing Mail Service....");

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        try {
            Class<MailService> mailServiceClass = null;

            switch (runtimeEnv) {
                case LOCAL:
                    mailServiceClass = (Class<MailService>) Class.forName("org.b3log.latke.mail.local.LocalMailService");
                    MAIL_SERVICE = mailServiceClass.newInstance();
                    break;
                case GAE:
                    mailServiceClass = (Class<MailService>) Class.forName("org.b3log.latke.mail.gae.GAEMailService");
                    MAIL_SERVICE = mailServiceClass.newInstance();
                    break;
                default:
                    throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize Mail Service!", e);
        }

        LOGGER.info("Constructed Mail Service");
    }

    /**
     * Gets mail service.
     * 
     * @return mail service
     */
    public static MailService getMailService() {
        return MAIL_SERVICE;
    }

    /**
     * Private default constructor.
     */
    private MailServiceFactory() {
    }
}
