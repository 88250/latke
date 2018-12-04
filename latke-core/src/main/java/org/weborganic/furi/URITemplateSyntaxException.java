/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;


/**
 * Thrown to indicate that a URI Template or URI Template fragment does not follow the appropriate
 * syntax.
 * 
 * <p>
 * This exception would typically be used for errors when parsing an expression supposed to follow
 * the URI template syntax. 
 * 
 * @author Christophe Lauret
 * @version 31 December 2008
 */
public class URITemplateSyntaxException extends IllegalArgumentException {

    /**
     * For serialisation.
     */
    private static final long serialVersionUID = -8924504091165837799L;

    /**
     * The input string.
     */
    private final String _input;

    /**
     * The reason string.
     */
    private final String _reason;

    /**
     * Constructs an instance from the given input string, reason.
     * 
     * @param input
     *          The input string.
     * @param reason
     *          A string explaining why the input could not be parsed.
     */
    public URITemplateSyntaxException(String input, String reason) {
        super(reason + " : " + input);
        if ((input == null) || (reason == null)) {
            throw new NullPointerException();
        }
        this._input = input;
        this._reason = reason;
    }

    /**
     * Returns the input string.
     * 
     * @return The input string.
     */
    public String getInput() {
        return this._input;
    }

    /**
     * Returns the reason explaining why the input string could not be parsed.
     * 
     * @return The reason string.
     */
    public String getReason() {
        return this._reason;
    }
}
