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


import com.baidu.bae.api.bcms.BaeBcms;
import com.baidu.bae.api.bcms.core.type.QueueType;
import com.baidu.bae.api.bcms.model.concrete.CreateQueueRequest;
import com.baidu.bae.api.bcms.model.concrete.DropQueueRequest;
import com.baidu.bae.api.bcms.model.concrete.MailRequest;
import com.baidu.bae.api.bcms.model.response.CreateQueueResponse;
import com.baidu.bae.api.exception.BaeException;
import com.baidu.bae.api.factory.BaeFactory;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;


/**
 * Baidu App Engine mail service.
 * 
 * <p>
 * Uses Baidu Cloud Message Service (BCMS) to send mail, see 
 * <a href="http://developer.baidu.com/wiki/index.php?title=docs/cplat/mq/sdk/java">here</a>
 * for more details.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, May 16, 2013
 */
public final class BAEMailService implements MailService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MailServiceFactory.class.getName());

    @Override
    public void send(final Message message) throws IOException {
        final BaeBcms bcms = BaeFactory.getBaeBcms();

        // Creates a queue
        final CreateQueueRequest createQueueRequest = new CreateQueueRequest();

        createQueueRequest.setAliasQueueName("mail_queue");
        createQueueRequest.setQueueType(QueueType.BCMS_QUEUE_TYPE);
        final CreateQueueResponse createQueueResponse = bcms.createQueue(createQueueRequest);

        final String queueName = createQueueResponse.getQueueName();

        try {
            // Sends a mail
            final MailRequest mailRequest = new MailRequest();

            mailRequest.setQueueName(queueName);
            mailRequest.setSubject(message.getSubject());
            mailRequest.setMessage("<!--HTML-->" + message.getHtmlBody());
            final Set<String> recipients = message.getRecipients();

            for (final String recipient : recipients) {
                mailRequest.addMailAddress(recipient);
            }

            mailRequest.setFrom(message.getFrom());

            bcms.mail(mailRequest);
        } catch (final BaeException e) {
            LOGGER.log(Level.SEVERE, "Mail send failed", e);
        } finally {
            // Removes the queue
            final DropQueueRequest dropQueueRequest = new DropQueueRequest();

            dropQueueRequest.setQueueName(queueName);

            bcms.dropQueue(dropQueueRequest);
        }
    }
}
