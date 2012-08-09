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
package org.b3log.latke.service;

/**
 * Service result.
 * 
 * <p>
 * The {@link #result} holds business processing result. For example query 
 * results.
 * </p>
 * 
 * <p>
 * If business processing failed, the {@link #successful} should be {@code false}.
 * </p>
 *
 * @param <T> the type of result
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 25, 2011
 */
// TODO: 88250, service result?
public final class ServiceResult<T> {

    /**
     * Indicates whether business processing is successful or not.
     */
    private boolean successful;
    /**
     * Business processing result.
     */
    private T result;
    /**
     * Message.
     */
    private String message;

    /**
     * Gets the message.
     * 
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message with the specified message.
     * 
     * @param message the specified message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Gets the business processing result.
     * 
     * @return result
     */
    public T getResult() {
        return result;
    }

    /**
     * Sets the business processing result the the specified result.
     * 
     * @param result the specified result
     */
    public void setResult(final T result) {
        this.result = result;
    }

    /**
     * Determines whether service processing is successful.
     * 
     * @return {@code true} if it is successful, returns {@code false} 
     * otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets the successful flag with the specified successful flag.
     * 
     * @param successful the specified successful flag
     */
    public void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

    /**
     * For the type of {@link ServiceResult#result}, indicates no result to
     * hold.
     * 
     * <p>
     * Callers of a service cares whether the invocation is 
     * {@link ServiceResult#successful successful}or not, so there is no result 
     * need to hold. This 'null' type should be as the type of the result for 
     * clarity.
     * </p>
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 25, 2011
     */
    public static final class Null {

        /**
         * Private constructor.
         */
        private Null() {
        }
    }
}
