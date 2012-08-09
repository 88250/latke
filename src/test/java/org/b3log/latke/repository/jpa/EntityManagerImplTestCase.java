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

import org.b3log.latke.Latkes;
import javax.persistence.EntityManager;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * {@link EntityManagerImpl} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 11, 2011
 */
public class EntityManagerImplTestCase {

    static {
        Latkes.initRuntimeEnv();
    }
    /**
     * Entity manager.
     */
    private EntityManager entityManager = new EntityManagerImpl();

    /**
     * Tests method {@link EntityManagerImpl#persist(java.lang.Object)}.
     */
    @Test
    public void persist() {
        final MetaEntity metaEntity = new MetaEntity(Entity0.class);

        assertNotNull(metaEntity);

//        final Entity0Repository entity0Repository =
//                new Entity0Repository("entity0");
//
//        final Entity0 entity0 = new Entity0();
//        entity0.setField2(true);
//        final long field3 = 88250;
//        entity0.setField3(field3);

        // FIXfinal EntityTransaction transaction = entityManager.getTransaction();
        //entityManager.persist(entity0);
        //transaction.commit();
    }
}
