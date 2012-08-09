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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Id utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 29, 2011
 */
public final class Ids {

    /**
     * Lock for unique id generation.
     */
    private static final Lock ID_GEN_LOCK = new ReentrantLock();
    /**
     * Sleep millisecond.
     */
    private static final long ID_GEN_SLEEP_MILLIS = 50;

    /**
     * Private default constructor.
     */
    private Ids() {
    }

    /**
     * Gets current date time string.
     *
     * <p>
     *   <b>Note</b>: This method is not safe in cluster environment.
     * </p>
     *
     * @return a time millis string
     */
    public static synchronized String genTimeMillisId() {
        String ret = null;

        ID_GEN_LOCK.lock();
        try {
            ret = String.valueOf(System.currentTimeMillis());

            try {
                Thread.sleep(ID_GEN_SLEEP_MILLIS);
            } catch (final InterruptedException e) {
                throw new RuntimeException("Generates time millis id fail");
            }
        } finally {
            ID_GEN_LOCK.unlock();
        }

        return ret;
    }
}
