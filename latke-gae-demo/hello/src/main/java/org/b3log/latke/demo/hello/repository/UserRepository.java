package org.b3log.latke.demo.hello.repository;

import org.b3log.latke.repository.AbstractRepository;

/**
 * User repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 4, 2011
 */
public final class UserRepository extends AbstractRepository {

    /**
     * Constructs a user repository with the specified name.
     * 
     * @param name the specified name
     */
    public UserRepository(final String name) {
        super(name);
    }
}
