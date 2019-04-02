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
package org.b3log.latke.repository.jdbc.mapping;


import org.b3log.latke.repository.jdbc.util.FieldDefinition;


/**
 * Boolean data type mapping.
 *
 * <p>
 * The data type is CHAR(1), we could INSERT INTO `test`(`test`) VALUES (false/true) in MySQL database, the actual value is '0' for false,
 * '1' for true.
 * </p>
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.1, Dec 27, 2012
 */
public class BooleanMapping implements Mapping {

    @Override
    public String toDataBaseSting(final FieldDefinition definition) {
        final StringBuilder sql = new StringBuilder();

        sql.append(definition.getName());
        sql.append(" char(1)");
        if (!definition.getNullable()) {
            sql.append(" not null");

        }

        return sql.toString();
    }
}
