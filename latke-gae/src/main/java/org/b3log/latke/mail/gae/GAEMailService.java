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
package org.b3log.latke.mail.gae;

import com.google.appengine.api.mail.MailServiceFactory;
import java.io.IOException;
import org.b3log.latke.mail.MailService;

/**
 * Google App Engine mail service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public final class GAEMailService implements MailService {

    /**
     * Mail service.
     */
    private static final com.google.appengine.api.mail.MailService MAIL_SERVICE = MailServiceFactory.getMailService();

    @Override
    public void send(final Message message) throws IOException {
        final com.google.appengine.api.mail.MailService.Message gaeMsg = toGAEMailMsg(message);

        MAIL_SERVICE.send(gaeMsg);
    }

    /**
     * Converts the specified message to a Google App Engine mail message.
     * 
     * @param message the specified message
     * @return GAE mail message
     */
    private static com.google.appengine.api.mail.MailService.Message toGAEMailMsg(final Message message) {
        final com.google.appengine.api.mail.MailService.Message ret = new com.google.appengine.api.mail.MailService.Message();

        ret.setSender(message.getFrom());
        ret.setTo(message.getRecipients());
        ret.setSubject(message.getSubject());
        ret.setHtmlBody(message.getHtmlBody());

        return ret;
    }
}
