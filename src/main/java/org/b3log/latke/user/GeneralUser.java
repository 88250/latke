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
package org.b3log.latke.user;

/**
 * User.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jul 10, 2011
 */
public final class GeneralUser {

    /**
     * Id.
     */
    private String id;
    /**
     * Email.
     */
    private String email;
    /**
     * Nickname.
     */
    private String nickname;

    /**
     * Gets the email.
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email with the specified email.
     * 
     * @param email the specified email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the id.
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id with the specified id.
     * 
     * @param id the specified id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the nickname.
     * 
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname with the specified nickname.
     * 
     * @param nickname the specified nickname
     */
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
}
