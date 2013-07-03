package org.b3log.latke.demo.hello.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;

/**
 * User repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 4, 2011
 */
@Repository
public final class UserRepository extends AbstractRepository {

    /**
     * Constructs a user repository.
     */
    public UserRepository() {
        super("user");
        
        JdbcRepositories.initAllTables(); // Generates database tables
    }
}
