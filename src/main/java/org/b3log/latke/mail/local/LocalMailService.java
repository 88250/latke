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
package org.b3log.latke.mail.local;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.b3log.latke.mail.MailService;

/**
 * Implementation of the {@link MailService} interface.
 * 
 * @author <a href="mailto:jiangzezhou1989@gmail.com">zezhou jiang</a>
 * @version 1.0.0.3, Sep 29, 2011
 */
public final class LocalMailService implements MailService {

    @Override
    public void send(final Message message) throws IOException {
        // TODO: zezhou jiang, throws ioexception while send fails

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    new MailSender().sendMail(message);
                } catch (final MessagingException e) {
                    Logger.getLogger(LocalMailService.class.getName()).log(Level.SEVERE, "Sends mail failed", e);
                }
            }
        }).start();
    }
}