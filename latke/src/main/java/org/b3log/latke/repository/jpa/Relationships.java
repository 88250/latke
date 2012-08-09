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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.Strings;

/**
 * Relationship utilities.
 * 
 * <p>
 * The utilities maintains the many to many relationship repository names with 
 * {@link #manyToManyRepositoryNames}, 
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 10, 2011
 */
final class Relationships {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Relationships.class.getName());
    /**
     * Many to many relationship repository names.
     */
    private static Set<String> manyToManyRepositoryNames = new HashSet<String>();

    /**
     * Gets a many to many repository name with the specified part of name.
     * 
     * @param repositoryName the specified part of many to many repository name
     * @return many to many repository name, returns {@code null} if not found
     */
//    public String getManyToManyRepositoryName(final String repositoryName) {
//        for (final String manyToManyRepositoryName : manyToManyRepositoryNames) {
//            final String[] parts = manyToManyRepositoryName.split("_");
//            
//            
//        }
//    }
    /**
     * Adds the specified many to many repository name.
     * 
     * <p>
     * The specified many to many repository name has two parts with a separator
     * underline '{@code _}', each part of them is the name of one repository.
     * </p>
     * 
     * <p>
     * The many to many repository name "a_b" is equals to "b_a", which could 
     * be added only once.
     * </p>
     * 
     * @param manyToManyRepositoryName the specified many to many repository 
     * name
     */
    public static void addManyToManyRepositoryName(
            final String manyToManyRepositoryName) {
        if (Strings.isEmptyOrNull(manyToManyRepositoryName)) {
            throw new IllegalArgumentException("The many to many repository name is empty or null");
        }

        final String m2mReposName = manyToManyRepositoryName.toLowerCase();

        if (!m2mReposName.matches("[a-z]+_[a-z]+")) {
            throw new IllegalArgumentException("The many to many repository name[" + m2mReposName + "] is invalid");
        }

        if (manyToManyRepositoryNames.contains(m2mReposName)) {
            return;
        }

        final String[] repositoryNames = m2mReposName.split("_");
        final String repositoryName1 = repositoryNames[0];
        final String repositoryName2 = repositoryNames[1];

        if (manyToManyRepositoryNames.contains(repositoryName2 + '_' + repositoryName1)) {
            return;
        }

        LOGGER.log(Level.FINER, "Found a new many to many relationship[repositoryName={0}]", manyToManyRepositoryName);
        manyToManyRepositoryNames.add(manyToManyRepositoryName);
    }

    /**
     * Private constructor.
     */
    private Relationships() {
    }
}
