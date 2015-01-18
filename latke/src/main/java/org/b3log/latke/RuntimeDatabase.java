/*
 * Copyright (c) 2009-2015, b3log.org
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
package org.b3log.latke;

/**
 *  Latke runtime JDBC database.
 * 
 * <p>
 * If Latke runs on local environment, Latke will read database configurations from file "local.properties".
 * </p>
 *  
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 2, 2013
 * @see Latkes#getRuntimeDatabase() 
 */
public enum RuntimeDatabase {
    
    /**
     * None.
     */
    NONE,

    /**
     * Oracle.
     */
    ORACLE,
    /**
     * MySQL.
     */
    MYSQL,
    /**
     * H2.
     */
    H2,
    /**
     * SYBASE.
     */
    SYBASE,
    /**
     * MSSQL.
     */
    MSSQL,
    /**
     * DB2.
     */
    DB2
}
