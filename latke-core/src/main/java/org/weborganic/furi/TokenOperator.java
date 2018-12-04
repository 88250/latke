package org.weborganic.furi;


import java.util.List;


/**
 * Defines tokens which use an operator to handle one or more variables.  
 * 
 * @author Christophe Lauret
 * @version 9 February 2009
 */
public interface TokenOperator extends Token {

    /**
     * Returns the list of variables used in this token.
     * 
     * @return the list of variables.
     */
    List<Variable> variables();

}
