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

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.util.Strings;

/**
 * Email sender.
 * 
 * @author <a href="mailto:jiangzezhou1989@gmail.com">zezhou jiang</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jan 4, 2013
 */
final class MailSender {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MailSender.class.getName());
    /**
     * Mail configurations.
     * 
     * <ul>
     *   <li>mail.user</li>
     *   <li>mail.password</li>
     *   <li>mail.smtp.host</li>
     *   <li>mail.smtp.port</li>
     *   <li>mail.smtp.auth</li>
     *   <li>mail.debug</li>
     * </ul>
     */
    private final ResourceBundle mailProperties = ResourceBundle.getBundle("mail");

    /**
     * Create session based on the mail properties.
     * 
     * @return session session from mail properties
     */
    private Session getSession() {
        final Properties props = new Properties();
        props.setProperty("mail.smtp.host", mailProperties.getString("mail.smtp.host"));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", mailProperties.getString("mail.smtp.port"));
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", mailProperties.getString("mail.debug"));
        props.put("mail.smtp.socketFactory.class", mailProperties.getString("mail.smtp.socketFactory.class"));
        props.put("mail.smtp.socketFactory.fallback", mailProperties.getString("mail.smtp.socketFactory.fallback"));
        props.put("mail.smtp.socketFactory.port", mailProperties.getString("mail.smtp.socketFactory.port"));

        return Session.getDefaultInstance(props, new SMTPAuthenticator());
    }

    /**
     * Converts the specified message into a {@link javax.mail.Message 
     * javax.mail.Message}.
     * 
     * @param message the specified message
     * @return a {@link javax.mail.internet.MimeMessage}
     * @throws MessagingException if converts error 
     */
    public javax.mail.Message convert2JavaMailMsg(final Message message)
            throws MessagingException {
        if (message == null) {
            return null;
        }

        if (Strings.isEmptyOrNull(message.getFrom())) {
            throw new MessagingException("Null from");
        }

        if (null == message.getRecipients() || message.getRecipients().isEmpty()) {
            throw new MessagingException("Null recipients");
        }

        final MimeMessage ret = new MimeMessage(getSession());
        ret.setFrom(new InternetAddress(message.getFrom()));
        final String subject = message.getSubject();
        ret.setSubject(subject != null ? subject : "");
        final String htmlBody = message.getHtmlBody();
        ret.setContent(htmlBody != null ? htmlBody : "", "text/html;charset=UTF-8");
        ret.addRecipients(RecipientType.TO, transformRecipients(message.getRecipients()));

        return ret;
    }

    /**
     * Transport recipients to InternetAddress array.
     * 
     * @param recipients the set of all recipients
     * @return  InternetAddress array of all recipients internetAddress
     * @throws MessagingException messagingException from javax.mail
     */
    private InternetAddress[] transformRecipients(final Set<String> recipients) throws MessagingException {
        if (recipients.isEmpty()) {
            throw new MessagingException("recipients of mail should not be empty");
        }

        final InternetAddress[] ret = new InternetAddress[recipients.size()];
        int i = 0;
        for (String recipient : recipients) {
            ret[i] = new InternetAddress(recipient);
            i++;
        }

        return ret;
    }

    /**
     * Sends email.
     * 
     * @param message  the specified message
     * @throws MessagingException message exception
     */
    void sendMail(final Message message) throws MessagingException {
        final javax.mail.Message msg = convert2JavaMailMsg(message);
        Transport.send(msg);
    }

    /**
     * Inner class for Authenticator.
     */
    private class SMTPAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailProperties.getString("mail.user"),
                    mailProperties.getString("mail.password"));
        }
    }
}
