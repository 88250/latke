/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.mail.bae;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;

/**
 * Baidu App Engine mail service.
 * 
 * <p>
 *   <b>NOTE</b>: This mail service done NOT send mail at present, do nothing.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 13, 2012
 */
public final class BAEMailService implements MailService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MailServiceFactory.class.getName());

    @Override
    public void send(final Message message) throws IOException {
        LOGGER.log(Level.FINER, "Do not send mail on BAE");
    }
}
