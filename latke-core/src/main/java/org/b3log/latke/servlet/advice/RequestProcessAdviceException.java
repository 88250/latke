/*
 * Copyright (c) 2009-present, b3log.org
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
package org.b3log.latke.servlet.advice;


import org.json.JSONObject;


/**
 * Exception for process advice.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.0, Oct 14, 2012
 */
public class RequestProcessAdviceException extends Exception {

    /**
     * the error message jsonObject.
     */
    private JSONObject jsonObject;

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 4070666571307478762L;

    /**
     * the default constructor.
     *
     * @param jsonObject jsonObject
     */
    public RequestProcessAdviceException(final JSONObject jsonObject) {
        super(jsonObject.toString());
        this.jsonObject = jsonObject;
    }

    /**
     * @return the jsonObject
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
