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
package org.b3log.latke.util;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email sender.
 * 
 * @author <a href="mailto:toughPatient5@gmail.com">Gang Liu</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jun 15, 2010
 */
public final class EmailSender implements Runnable {

    /**
     * Mail sender account host.
     */
    public static final String MAIL_HOST = "smtp.gmail.com";
    /**
     * Email from.
     */
    private String from;
    /**
     * Email to.
     */
    private String to;
    /**
     * Email message.
     */
    private String message;
    /**
     * Email subject.
     */
    private String subject;
    /**
     * Email user name.
     */
    private String userName;
    /**
     * Email password.
     */
    private String password;

    /**
     * Public constructor with parameters.
     * 
     * @param userName username
     * @param password password
     * @param from email from
     * @param to email to
     * @param message message about
     * @param subject email subject
     */
    public EmailSender(final String userName, final String password, final String from, final String to,
                       final String message, final String subject) {
        this.userName = userName;
        this.password = password;
        this.from = from;
        this.to = to;
        this.message = message;
        this.subject = subject;
    }

    /**
     * Sends email.
     * 
     * @throws MessagingException message exception
     */
    private void sendMail() throws MessagingException {
        /*
         * Properties used to construct a email sending connection
         * protocal.
         */
        final Properties props = new Properties();
        props.put("mail.smtp.host", MAIL_HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final Authenticator auth = new SMTPAuthenticator();
        final MimeMessage msg = new MimeMessage(Session.getDefaultInstance(props, auth));
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(message);
        Transport.send(msg);
    }

    @Override
    public void run() {
        try {
            sendMail();
        } catch (final MessagingException ex) {
            Logger.getLogger(EmailSender.class.getName()).severe(ex.getMessage());
        }
    }

    /**
     * Inner class for Authenticator.
     */
    private class SMTPAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }
}
