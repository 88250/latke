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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Entity manager factory implementation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 11, 2011
 */
public final class EntityManagerFactoryImpl implements EntityManagerFactory {

    @Override
    public EntityManager createEntityManager() {
        return new EntityManagerImpl();
    }

    @Override
    public EntityManager createEntityManager(final Map map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
