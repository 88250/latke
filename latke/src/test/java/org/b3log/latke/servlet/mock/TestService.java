package org.b3log.latke.servlet.mock;

import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;

/**
 * User: steveny
 * Date: 13-9-25
 * Time: 下午1:23
 */
@RequestProcessor
public class TestService {

    /**
     * getString.
     *
     * @return a String
     */
    @RequestProcessing(value = "string")
    public String getString() {
        return "string";
    }


}
