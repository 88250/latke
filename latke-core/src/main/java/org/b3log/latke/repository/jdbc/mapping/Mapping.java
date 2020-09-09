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
package org.b3log.latke.repository.jdbc.mapping;

import org.b3log.latke.repository.jdbc.util.FieldDefinition;

/**
 * Data type mapping.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.0, Jan 12, 2012
 */
public interface Mapping {

    /**
     * Mapping from Java definition to database table-definition SQL.
     *
     * @param definition {@link FieldDefinition}
     * @return table-definition SQL.
     */
    String toDataBaseString(FieldDefinition definition);
}
