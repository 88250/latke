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
package org.b3log.latke.repository.jdbc;

import org.b3log.latke.Latkes;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * JdbcRepositoriesTestCase.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Mar 15, 2018
 */
public class JdbcRepositoriesTestCase {

    /**
     * jsonToModel.
     */
    @Test
    public void jsonToModel() {
        Latkes.init();
        final List<RepositoryDefinition> repositoryDefinitions = JdbcRepositories.getRepositoryDefinitions();

        assertNotNull(repositoryDefinitions);
    }

    /**
     * jsonToDB.
     */
    @Test(groups = {"jdbc"})
    public void jsonToDB() {
        Latkes.init();
        //JdbcRepositories.initAllTables();
    }
}
