/*
 * Copyright (c) 2015, b3log.org
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
package org.b3log.latke.repository.gae;


import java.util.ConcurrentModificationException;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;


/**
 * Google App Engine datastore transaction. Just wraps {@link com.google.appengine.api.datastore.Transaction} simply.
 * 
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.9, Oct 14, 2013
 * @see GAERepository
 */
public final class GAETransaction implements Transaction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GAETransaction.class.getName());

    /**
     * Underlying Google App Engine transaction.
     */
    private com.google.appengine.api.datastore.Transaction appEngineDatastoreTx;

    /**
     * Times of commit retries.
     */
    public static final int COMMIT_RETRIES = 3;

    /**
     * Constructs a {@link GAETransaction} object with the specified Google App Engine datastore 
     * {@link com.google.appengine.api.datastore.Transaction transaction}.
     *
     * @param appEngineDatastoreTx the specified Google App Engine datastore transaction
     */
    public GAETransaction(final com.google.appengine.api.datastore.Transaction appEngineDatastoreTx) {
        this.appEngineDatastoreTx = appEngineDatastoreTx;
    }

    @Override
    public String getId() {
        return appEngineDatastoreTx.getId();
    }

    /**
     * Commits this transaction with {@value #COMMIT_RETRIES} times of retries.
     * 
     * <p>
     * <b>Throws</b>:<br/>
     * {@link java.util.ConcurrentModificationException} - if commits failed
     * </p>
     * @see #COMMIT_RETRIES
     */
    @Override
    public void commit() {
        int retries = COMMIT_RETRIES;

        while (true) {
            try {
                appEngineDatastoreTx.commit();

                GAERepository.TX.set(null);

                break;
            } catch (final ConcurrentModificationException e) {
                if (retries == 0) {
                    throw e;
                }

                --retries;
                LOGGER.log(Level.WARN, "Retrying to commit this transaction[id={0}, app={1}]",
                    new Object[] {appEngineDatastoreTx.getId(), appEngineDatastoreTx.getApp()});
            }
        }
    }

    @Override
    public void rollback() {
        appEngineDatastoreTx.rollback();

        GAERepository.TX.set(null);
    }

    @Override
    public boolean isActive() {
        return appEngineDatastoreTx.isActive();
    }
}
