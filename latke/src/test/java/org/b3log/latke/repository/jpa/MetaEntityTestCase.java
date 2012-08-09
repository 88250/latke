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

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.b3log.latke.repository.AbstractRepository;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * {@link MetaEntity} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 11, 2011
 */
public class MetaEntityTestCase {

    /**
     * Test constructor.
     */
    @Test
    public void constructor() {
        final MetaEntity metaEntity = new MetaEntity(Entity0.class);

        assertNotNull(metaEntity);
    }
}

/**
 * Dummy entity0 for testing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 10, 2011
 */
@Entity
class Entity0 {

    /**
     * Field 1, id.
     */
    @Id
    private String field1;
    /**
     * Field 2.
     */
    private boolean field2;
    /**
     * Field 3.
     */
    private long field3;

    /**
     * Gets the field 1.
     * 
     * @return field 1
     */
    public String getField1() {
        return field1;
    }

    /**
     * Sets field 1 with the specified field 1.
     * 
     * @param field1 the specified field 1
     */
    public void setField1(final String field1) {
        this.field1 = field1;
    }

    /**
     * Determines whether the field 2 is {@code true}.
     * 
     * @return field 2
     */
    public boolean isField2() {
        return field2;
    }

    /**
     * Sets the field 2 with the specified field 2.
     * 
     * @param field2 the specified field 2
     */
    public void setField2(final boolean field2) {
        this.field2 = field2;
    }

    /**
     * Gets field 3.
     * 
     * @return field 3
     */
    public long getField3() {
        return field3;
    }

    /**
     * Sets the field 3 withe the specified field 3.
     * 
     * @param field3 the specified field 3
     */
    public void setField3(final long field3) {
        this.field3 = field3;
    }
}

/**
 * Repository for entity0.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 11, 2011
 */
class Entity0Repository extends AbstractRepository {

    /**
     * Constructs a entity0 repository with the specified repository name.
     * 
     * @param name the specified repository name
     */
    public Entity0Repository(final String name) {
        super(name);
    }
}

/**
 * Dummy entity1 for testing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 10, 2011
 */
@Entity
class Entity1 {

    /**
     * Field 1, id.
     */
    @Id
    private String field1;
    /**
     * Field 2.
     */
    private boolean field2;
    /**
     * One to many relationship.
     */
    @OneToMany
    private Set<Entity2> entities2;

    /**
     * Gets the field 1.
     * 
     * @return field 1
     */
    public String getField1() {
        return field1;
    }
}

/**
 * Dummy entity2 for testing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 10, 2011
 */
@Entity
class Entity2 {

    /**
     * Field 1, id.
     */
    @Id
    private String field1;
    /**
     * Many to one relationship.
     */
    @ManyToOne
    private Entity1 entity1;

    /**
     * Gets the field 1.
     * 
     * @return field 1
     */
    public String getField1() {
        return field1;
    }
}
