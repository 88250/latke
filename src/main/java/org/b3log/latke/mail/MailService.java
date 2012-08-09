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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Mail service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public interface MailService {

    /**
     * Sends the specified message as a mail asynchronously.
     * 
     * @param message the specified message
     * @throws IOException if internal errors 
     */
    void send(final Message message) throws IOException;

    /**
     * Mail message.
     * 
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Aug 8, 2011
     */
    class Message {

        /**
         * From.
         */
        private String from;
        /**
         * Recipients.
         */
        private Set<String> recipients = new HashSet<String>();
        /**
         * HTML body. 
         */
        private String htmlBody;
        /**
         * Subject.
         */
        private String subject;

        /**
         * Gets the recipients.
         * 
         * @return recipients
         */
        public Set<String> getRecipients() {
            return Collections.unmodifiableSet(recipients);
        }

        /**
         * Adds the specified recipient.
         * 
         * @param recipient the specified recipient
         */
        public void addRecipient(final String recipient) {
            recipients.add(recipient);
        }

        /**
         * Gets the HTML body.
         * 
         * @return HTML body
         */
        public String getHtmlBody() {
            return htmlBody;
        }

        /**
         * Sets the HTML body with the specified HTML body.
         * 
         * @param htmlBody the specified HTML body
         */
        public void setHtmlBody(final String htmlBody) {
            this.htmlBody = htmlBody;
        }

        /**
         * Gets the from.
         * 
         * @return from
         */
        public String getFrom() {
            return from;
        }

        /**
         * Sets the from with the specified from.
         * 
         * @param from the specified from
         */
        public void setFrom(final String from) {
            this.from = from;
        }

        /**
         * Gets the subject.
         * 
         * @return subject
         */
        public String getSubject() {
            return subject;
        }

        /**
         * Sets the subject with the specified subject.
         * 
         * @param subject the specified subject
         */
        public void setSubject(final String subject) {
            this.subject = subject;
        }
    }
}
