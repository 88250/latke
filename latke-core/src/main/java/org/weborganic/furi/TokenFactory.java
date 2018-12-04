/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;

import java.util.Hashtable;
import java.util.Map;

import org.weborganic.furi.Variable.Reserved;

/**
 * A factory for URI tokens.
 * 
 * <p>Tokens can be instantiated from an expression which is specific to each token.
 *
 * @see TokenLiteral
 * @see TokenVariable
 * @see TokenOperatorD3
 * @see TokenOperatorPS
 * 
 * @author Christophe Lauret
 * @version 6 November 2009
 */
public class TokenFactory {

  /**
   * A syntax to use for creating tokens.
   * 
   * @author Christophe Lauret
   * @version 6 November 2009
   */
  public enum Syntax {

    /**
     * Use the syntax defined in Draft 3 of the URI templates.
     */
    DRAFT3 {

      @Override
      protected Token newExpansion(String exp) {
        // an operator as defined in Draft 3
        if (exp.indexOf('|') >= 0) {
          return TokenOperatorD3.parse(exp);
        // assume a variable
        } else {
          return new TokenVariable(Variable.parse(exp));
        }
      }

    },

    /**
     * Use a syntax currently used in PageSeeder and based on what was suggested 
     * by Roy T Fielding on the W3C URI listing in October 2008. 
     */
    PAGESEEDER {

      @Override
      protected Token newExpansion(String exp) {
        // possibly Roy Fielding's operators
        if (!Character.isLetter(exp.charAt(0)) && !Character.isDigit(exp.charAt(0))) {
          return TokenOperatorPS.parse(exp);
        // assume a variable
        } else {
          return new TokenVariable(Variable.parse(exp));
        }
      }

    },

    /**
     * Use a syntax based on the draft as of 29 October 2009.
     */
    DRAFTX {

      @Override
      protected Token newExpansion(String exp) {
        // possibly Roy Fielding's operators
        if (!Character.isLetter(exp.charAt(0)) && !Character.isDigit(exp.charAt(0))) {
          return TokenOperatorDX.parse(exp);
        // maybe a collection
        } else if (exp.indexOf(',') >= 0) {
          return TokenOperatorDX.parse(exp);
        // assume a variable
        } else {
          return new TokenVariable(Variable.parse(exp));
        }
      }

    };

    /**
     * Generates a template expansion token corresponding to the specified expression.
     * 
     * @param exp The expression within the curly brackets {}.
     * 
     * @return The corresponding token instance.
     * 
     * @throws URITemplateSyntaxException If the expression could not be parsed as a valid token.
     */
    protected abstract Token newExpansion(String exp);

  }

  /**
   * Factories for reuse.
   */
  private static final Map<Syntax, TokenFactory> FACTORIES = new Hashtable<Syntax, TokenFactory>();
  static {
    for (Syntax syntax : Syntax.values()) {
      FACTORIES.put(syntax, new TokenFactory(syntax));      
    }
  }

  /**
   * The URI template syntax to use for generating tokens.
   */
  private Syntax _syntax; 

  /**
   * Prevents creation of instances.
   * 
   * @param syntax The URI template syntax to use for generating tokens. 
   */
  private TokenFactory(Syntax syntax) {
    this._syntax = syntax;
  }

  /**
   * Generates the token corresponding to the specified expression.
   * 
   * @param exp The expression.
   * 
   * @return The corresponding token instance.
   * 
   * @throws URITemplateSyntaxException If the expression could not be parsed as a valid token.
   */
  public Token newToken(String exp) {
    // no expression: no token!
    if (exp == null || exp.length() == 0)
      return null;
    // intercept the wild card
    if ("*".equals(exp)) 
      return newWildcard();
    // too short to be anything but a literal
    int len = exp.length();
    if (len < 2)
      return new TokenLiteral(exp);
    // a template expansion token
    if (exp.charAt(0) == '{' && exp.charAt(len - 1) == '}') {
      // defer to the underlying syntax
      return this._syntax.newExpansion(exp.substring(1, len - 1));
    }
    // a literal text token
    return new TokenLiteral(exp);
  }

  /**
   * Generates the token corresponding to the specified expression.
   * 
   * @param exp The expression.
   * 
   * @return The corresponding token instance.
   * 
   * @throws URITemplateSyntaxException If the expression could not be parsed as a valid token.
   */
  public static Token newToken(String exp, Syntax syntax) {
    TokenFactory factory = getInstance(syntax);
    return factory.newToken(exp);
  }

  /**
   * Creates a new 'wildcard' token for legacy purposes.
   * 
   * <p>This is used for conventional URI patterns which have been implemented using "*".
   * 
   * @return A new 'wildcard' token.
   */
  private static final Token newWildcard() {
    return new TokenOperatorPS(TokenOperatorPS.Operator.URI_INSERT, new Variable(Reserved.WILDCARD));
  }

  /**
   * Returns a token factory instance using the default syntax (DRAFTX).
   * 
   * @return a token factory instance using the default syntax (DRAFTX).
   */
  public static TokenFactory getInstance() {
    return FACTORIES.get(Syntax.DRAFTX);
  }

  /**
   * Returns a token factory instance.
   * 
   * @return a token factory instance.
   */
  public static TokenFactory getInstance(Syntax syntax) {
    return new TokenFactory(syntax);
  }

}
