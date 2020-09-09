/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.repository.jdbc.util;

/**
 * FieldDefinition of each Filed in *.json.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Mar 15, 2018
 */
public class FieldDefinition {

    /**
     * Field name.
     */
    private String name;

    /**
     * Field description.
     */
    private String description;

    /**
     * Field type.
     */
    private String type;

    /**
     * The length of the type.
     */
    private Integer length;

    /**
     * Precision.
     */
    private Integer precision;

    /**
     * if isKey.
     */
    private Boolean isKey = false;

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
     * Gets the precision.
     *
     * @return precision
     */
    public Integer getPresision() {
        return precision;
    }

    /**
     * Sets the precision with the specified precision.
     *
     * @param presision the specified precision
     */
    public void setPresision(final Integer presision) {
        this.precision = presision;
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
