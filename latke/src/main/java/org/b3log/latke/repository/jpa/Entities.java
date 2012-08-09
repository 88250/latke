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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Entity utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 9, 2011
 */
public final class Entities {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Entities.class.getName());
    /**
     * Meta entities.
     * 
     * <p>
     * &lt;entityClass,{@link MetaEntity meta entity}&gt;
     * </p>
     */
    private static final Map<Class<?>, MetaEntity> META_ENTITY_HOLDER = new ConcurrentHashMap<Class<?>, MetaEntity>();

    /**
     * Adds the specified meta entity.
     * 
     * @param metaEntity the specified meta entity
     */
    public static void addMetaEntity(final MetaEntity metaEntity) {
        META_ENTITY_HOLDER.put(metaEntity.getEntityClass(), metaEntity);
    }

    /**
     * Gets a meta entity with the specified entity class.
     * 
     * @param entityClass the specified entity class
     * @return meta entity, return {@code null} if not found
     */
    public static MetaEntity getMetaEntity(final Class<?> entityClass) {
        return META_ENTITY_HOLDER.get(entityClass);
    }

    /**
     * Entity scanner.
     */
    private Entities() {
    }
}
