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
package org.b3log.latke.repository.jdbc.util;

/**
 * FieldDefinition of each Filed in *.json.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public class FieldDefinition {

    /**
     * field name.
     */
    private String name;

    /**
     * field type.
     */
    private String type;

    /**
     * the length of the type.
     */
    private Integer length;

    /**
     * if isKey.
     */
    private Boolean isKey;

    /**
     * if null-able.
     */
    private Boolean nullable = true;

    /**
     * getName.
     * 
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * setName.
     * 
     * @param name name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * getType.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * setType.
     * 
     * @param type type.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * getLength.
     * 
     * @return length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * setLength.
     * 
     * @param length length.
     */
    public void setLength(final Integer length) {
        this.length = length;
    }

    /**
     * getIsKey.
     * 
     * @return iskey
     */
    public Boolean getIsKey() {
        return isKey;
    }

    /**
     * setIsKey.
     * 
     * @param isKey isKey
     */
    public void setIsKey(final Boolean isKey) {
        this.isKey = isKey;
    }

    /**
     * getNullable.
     * 
     * @return nullable
     */
    public Boolean getNullable() {
        return nullable;
    }

    /**
     * setNullable.
     * 
     * @param nullable nullable
     */
    public void setNullable(final Boolean nullable) {
        this.nullable = nullable;
    }

}
