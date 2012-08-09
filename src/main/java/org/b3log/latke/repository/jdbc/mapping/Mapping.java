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
package org.b3log.latke.repository.jdbc.mapping;

import org.b3log.latke.repository.jdbc.util.FieldDefinition;

/**
 * the interface for mapping.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Jan 12, 2012
 */
public interface Mapping {

    /**
     * for mapping from java definition to database table-definition sql.
     * 
     * 
     * @param definition {@link FieldDefinition}
     * @return table-definition sql.
     */
    String toDataBaseSting(FieldDefinition definition);
}
