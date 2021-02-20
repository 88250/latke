package org.b3log.latke.repository.postgresql.mapping;

import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;

/**
 * PostgreSQL string type mapping.
 *
 * @author <a href="https://ld246.com/member/Gakkiyomi2019">Gakkiyomi</a>
 * @version 1.0.0.0, Feb 2, 2021
 */
public class StringMapping implements Mapping {
    @Override
    public String toDataBaseString(final FieldDefinition definition) {
        final StringBuilder sqlBuilder = new StringBuilder("\"" + definition.getName() + "\"");
        if (null == definition.getLength()) {
            definition.setLength(0);
        }

        final Integer length = definition.getLength();
        if (255 < length) {
            sqlBuilder.append(" TEXT");
        } else {
            sqlBuilder.append(" VARCHAR(").append(length < 1 ? 255 : length);
            sqlBuilder.append(")");
        }

        if (!definition.getNullable()) {
            sqlBuilder.append(" NOT NULL");
        }
        return sqlBuilder.toString();
    }
}
