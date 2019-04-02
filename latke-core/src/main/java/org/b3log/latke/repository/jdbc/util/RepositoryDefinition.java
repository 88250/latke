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
package org.b3log.latke.repository.jdbc.util;

import java.util.List;

/**
 * Repository definition.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Mar 2, 2019
 */
public class RepositoryDefinition {

    /**
     * Repository name, mapping to table name including prefix if exists.
     */
    private String name;

    /**
     * Repository description, mapping to table comment.
     */
    private String description;

    /**
     * Key definitions, mapping to table files.
     */
    private List<FieldDefinition> keys;

    /**
     * Repository charset.
     */
    private String charset;

    /**
     * Repository collate.
     */
    private String collate;

    /**
     * Gets the name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name with the specified name.
     *
     * @param name the specified name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description with the specified description.
     *
     * @param description the specified description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the keys.
     *
     * @return keys
     */
    public List<FieldDefinition> getKeys() {
        return keys;
    }

    /**
     * Sets the keys whit the specified keys.
     *
     * @param keys the specified keys
     */
    public void setKeys(final List<FieldDefinition> keys) {
        this.keys = keys;
    }

    /**
     * Gets the charset.
     *
     * @return charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the charset with the specified charset.
     *
     * @param charset the specified charset
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Gets the collate.
     *
     * @return collate
     */
    public String getCollate() {
        return collate;
    }

    /**
     * Sets the collate with the specified collate.
     *
     * @param collate the specified collate
     */
    public void setCollate(final String collate) {
        this.collate = collate;
    }
}
