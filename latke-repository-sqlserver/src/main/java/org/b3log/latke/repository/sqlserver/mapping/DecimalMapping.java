/*
 * Copyright (c) 2014, B3log Team
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
package org.b3log.latke.repository.sqlserver.mapping;


import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;


/**
 * Decimal mapping.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 10, 2014
 */
public class DecimalMapping implements Mapping {

    @Override
    public String toDataBaseSting(final FieldDefinition definition) {
        final StringBuilder sql = new StringBuilder();

        sql.append(definition.getName());
        sql.append(" dicimal(");
        
        if (null == definition.getLength()) {
            sql.append("9, ");
        } else {
            sql.append(definition.getLength());
        }
        
        if (null == definition.getPresision()) {
            sql.append(2);
        } else {
            sql.append(definition.getPresision());
        }
        
        sql.append(") ");
        
        if (!definition.getNullable()) {
            sql.append(" not null");
        }

        return sql.toString();
    }
}
