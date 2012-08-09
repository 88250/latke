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
package org.b3log.latke.repository.jpa;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.jpa.util.EntityClassCheckers;

/**
 * Meta entity.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 14, 2011
 */
public final class MetaEntity {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MetaEntity.class.getName());
    /**
     * The type of entity.
     */
    private Class<?> entityClass;
    /**
     * Repository.
     */
    private String repositoryName;
    /**
     * Fields.
     */
    private Map<String, Field> fields = new HashMap<String, Field>();

    /**
     * Constructs a meta entity with the specified entity class.
     * 
     * @param entityClass the specified entity class
     */
    public MetaEntity(final Class<?> entityClass) {
        LOGGER.log(Level.FINER, "Analysing an entity scheme....");

        // XXX: Resolver checker for checks fail error messages output

        if (!EntityClassCheckers.isValid(entityClass)) {
            throw new IllegalArgumentException("The specified class is not a valid entity class");
        }

        this.entityClass = entityClass;

        final String entityClassName = entityClass.getSimpleName();
        repositoryName = entityClassName.substring(0, 1).toLowerCase() + entityClassName.substring(1);

        LOGGER.log(Level.FINER, "Entity[classSimpleName={0}] to repository[name={1}]",
                   new Object[]{entityClass.getSimpleName(), repositoryName});

        final Field[] allFields = entityClass.getDeclaredFields();

        for (int i = 0; i < allFields.length; i++) {
            final Field field = allFields[i];
            final Class<?> fieldClass = field.getType();

            if (field.isAnnotationPresent(Id.class)) {
                fields.put(Keys.OBJECT_ID, field);
                continue;
            }

            if (fieldClass.isPrimitive() || String.class.equals(fieldClass)) {
                fields.put(field.getName(), field);
                continue;
            }


            if (field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(ManyToOne.class)) {
                fields.put(field.getName() + "Id", field);
                continue;
            }

            if (field.isAnnotationPresent(ManyToMany.class)) {
                final String many2ManyReposName = fieldClass.getSimpleName() + '_' + entityClass.getSimpleName();
                Relationships.addManyToManyRepositoryName(many2ManyReposName);
            }

            // TODO: one to many
        }

        Entities.addMetaEntity(this);
    }

    /**
     * Gets fields.
     * 
     * @return fields
     */
    public Map<String, Field> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    /**
     * Gets the repository name.
     * 
     * @return repository name
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Gets the entity class.
     * 
     * @return entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }
}
