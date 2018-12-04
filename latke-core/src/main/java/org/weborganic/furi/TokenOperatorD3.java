/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A token using the operators defined in draft 3 of the URI templates specifications.
 * 
 * Characters (4.2):
 * 
 * <pre>
 * op          = 1*ALPHA
 * arg         = *(reserved / unreserved / pct-encoded)
 * var         = varname [ &quot;=&quot; vardefault ]
 * vars        = var [ *(&quot;,&quot; var) ]
 * varname     = (ALPHA / DIGIT)*(ALPHA / DIGIT / &quot;.&quot; / &quot;_&quot; / &quot;-&quot; )
 * vardefault  = *(unreserved / pct-encoded)
 * operator    = &quot;-&quot; op &quot;|&quot; arg &quot;|&quot; vars
 * </pre>
 * 
 * @see <a
 *      href="http://bitworking.org/projects/URI-Templates/spec/draft-gregorio-uritemplate-03.html">URI
 *      Template (draft 3)</a>
 * 
 * @author Christophe Lauret
 * @version 9 February 2009
 */
public class TokenOperatorD3 extends TokenBase implements TokenOperator {

  /**
   * The list of operators currently supported.
   */
  public enum Operator {

    /**
     * 4.4.2 The 'opt' operator.
     * 
     * If each variable is undefined or an empty list then substitute the empty string, otherwise
     * substitute the value of 'arg'.
     * 
     * Example:
     * 
     * <pre>
     * foo := &quot;fred&quot;
     * 
     * &quot;{-opt|fred@example.org|foo}&quot; -&gt; &quot;fred@example.org&quot;
     * &quot;{-opt|fred@example.org|bar}&quot; -&gt; &quot;&quot;
     * </pre>
     */
    OPT {
      public String expand(String arg, List<Variable> vars, Parameters parameters) {
        for (Variable v : vars) {
          if (parameters.exists(v.name()))
            return arg;
        }
        return "";
      }
    },

    /**
     * 4.4.3 The 'neg' operator.
     * 
     * If each variable is undefined or an empty list then substitute the value of arg, otherwise
     * substitute the empty string.
     * 
     * Example:
     * 
     * <pre>
     *  foo := &quot;fred&quot;
     * 
     * &quot;{-neg|fred@example.org|foo}&quot; -&gt; &quot;&quot;
     * &quot;{-neg|fred@example.org|bar}&quot; -&gt; &quot;fred@example.org&quot;
     * </pre>
     */
    NEG {
      String expand(String arg, List<Variable> vars, Parameters parameters) {
        for (Variable v : vars) {
          if (parameters.exists(v.name()))
            return "";
        }
        return arg;
      }
    },

    /**
     * 4.4.4 The 'prefix' operator.
     * 
     * The prefix operator MUST only have one variable in its expansion. More than one variable is
     * an error condition. If the variable is undefined or an empty list then substitute the empty
     * string. If the variable is a defined non-list then substitute the value of arg preceeded by
     * the value of the variable. If the variable is a defined list then substitute the
     * concatenation of every list value preceeded by the arg.
     * 
     * Example:
     * 
     * <pre>
     *  foo := &quot;fred&quot;
     *  bar := [&quot;fee&quot;, &quot;fi&quot;, &quot;fo&quot;, &quot;fum&quot;]
     *  baz := []
     *  
     *  &quot;{-prefix|/|foo}&quot; -&gt; &quot;/fred&quot;
     *  &quot;{-prefix|/|bar}&quot; -&gt; &quot;/fee/fi/fo/fum&quot;
     *  &quot;{-prefix|/|baz}&quot; -&gt; &quot;&quot;
     *  &quot;{-prefix|/|qux}&quot; -&gt; &quot;&quot;
     * </pre>
     */
    PREFIX {
      String expand(String arg, List<Variable> vars, Parameters parameters) {
        StringBuffer expansion = new StringBuffer();
        Variable var = vars.get(0);
        String[] values = var.values(parameters);
        if (values.length > 0 && values[0].length() > 0) {
          for (String value : values) {
            expansion.append(arg).append(URICoder.encode(value));
          }
        }
        return expansion.toString();
      }
    },

    /**
     * 4.4.5 The 'suffix' operator.
     * 
     * The prefix operator MUST only have one variable in its expansion. More than one variable is
     * an error condition. If the variable is undefined or an empty list then substitute the empty
     * string. If the variable is a defined non-list then substitute the value of arg followed by
     * the value of the variable. If the variable is a defined list then substitute the
     * concatenation of every list value followed by the arg.
     * 
     * Example:
     * 
     * <pre>
     *  foo := &quot;fred&quot;
     *  bar := [&quot;fee&quot;, &quot;fi&quot;, &quot;fo&quot;, &quot;fum&quot;]
     *  baz := []
     *  
     *  &quot;{-suffix|/|foo}&quot; -&gt; &quot;fred/&quot;
     *  &quot;{-suffix|/|bar}&quot; -&gt; &quot;fee/fi/fo/fum/&quot;
     *  &quot;{-suffix|/|baz}&quot; -&gt; &quot;&quot;
     *  &quot;{-suffix|/|qux}&quot; -&gt; &quot;&quot;
     * </pre>
     */
    SUFFIX {
      String expand(String arg, List<Variable> vars, Parameters parameters) {
        StringBuffer expansion = new StringBuffer();
        Variable var = vars.get(0);
        String[] values = var.values(parameters);
        if (values.length > 0 && values[0].length() > 0) {
          for (String value : values) {
            expansion.append(URICoder.encode(value)).append(arg);
          }
        }
        return expansion.toString();
      }
    },

    /**
     * 4.4.6 The 'join' operator.
     * 
     * Supplying a list variable to the join operator is an error. For each variable that is defined
     * and non-empty create a keyvalue string that is the concatenation of the variable name, "=",
     * and the variable value. Concatenate more than one keyvalue string with intervening values of
     * arg to create the substitution value. The order of variables MUST be preserved during
     * substitution.
     * 
     * Example:
     * 
     * <pre>
     *  foo := &quot;fred&quot;
     *  bar := &quot;barney&quot;
     *  baz := &quot;&quot;
     * 
     *  &quot;{-join|&amp;|foo,bar,baz,qux}&quot; -&gt; &quot;foo=fred&amp;bar=barney&amp;baz=&quot;
     *  &quot;{-join|&amp;|bar}&quot; -&gt; &quot;bar=barney&quot;
     *  &quot;{-join|&amp;|qux}&quot; -&gt; &quot;&quot;
     * </pre>
     */
    JOIN {
      String expand(String arg, List<Variable> vars, Parameters parameters) {
        StringBuffer expansion = new StringBuffer();
        boolean first = true;
        for (Iterator<Variable> i = vars.iterator(); i.hasNext();) {
          Variable var = i.next();
          if (parameters.exists(var.name())) {
            String[] values = var.values(parameters);
            for (String value : values) {
              if (!first)
                expansion.append(arg);
              else
                first = false;
              expansion.append(var.name()).append('=').append(URICoder.encode(value));
            }
          }
        }
        return expansion.toString();
      }
    },

    /**
     * 4.4.7 The 'list' operator.
     * 
     * The list operator MUST have only one variable in its expansion and that variable must be
     * a list. More than one variable is an error. If the list is non-empty then substitute the
     * concatenation of all the list members with intervening values of arg. If the list is empty or
     * the variable is undefined them substitute the empty string.
     * 
     * Example:
     * 
     * <pre>
     *  foo := [&quot;fred&quot;, &quot;barney&quot;, &quot;wilma&quot;]
     *  bar := [&quot;a&quot;, &quot;&quot;, &quot;c&quot;]
     *  baz := [&quot;betty&quot;]
     *  qux := []
     * 
     *  &quot;{-list|/|foo}&quot; -&gt; &quot;fred/barney/wilma&quot;
     *  &quot;{-list|/|bar}&quot; -&gt; &quot;a//c&quot;
     *  &quot;{-list|/|baz}&quot; -&gt; &quot;betty&quot;
     *  &quot;{-list|/|qux}&quot; -&gt; &quot;&quot;
     *  &quot;{-list|/|corge}&quot; -&gt; &quot;&quot;
     * </pre>
     */
    LIST {
      String expand(String arg, List<Variable> vars, Parameters parameters) {
        StringBuffer expansion = new StringBuffer();
        Variable var = vars.get(0);
        String[] values = var.values(parameters);
        if (values.length > 0 && values[0].length() > 0) {
          for (int i = 0; i < values.length; i++) {
            if (i > 0)
              expansion.append(arg);
            expansion.append(URICoder.encode(values[i]));
          }
        }
        return expansion.toString();
      }
    };

    /**
     * Applies the expansion rules defined for the operator given the specified argument, variable and
     * parameters.
     * 
     * @param arg The argument for the operator.
     * @param vars The variables for the operator.
     * @param params The parameters to use.
     */
    abstract String expand(String arg, List<Variable> vars, Parameters params);

  }

  /**
   * The operator.
   */
  private Operator _operator;

  /**
   * The argument for this token.
   */
  private String _arg;

  /**
   * The variables for this token.
   */
  private List<Variable> _vars;

  /**
   * Creates a new operator token.
   * 
   * @param op   The operator to use.
   * @param arg  The argument for this operator.
   * @param vars The variables for this operator.
   * 
   * @throws NullPointerException If any of the argument is <code>null</code>.
   */
  public TokenOperatorD3(Operator op, String arg, List<Variable> vars) throws NullPointerException {
    super(toExpression(op, arg, vars));
    if (op == null || arg == null || vars == null)
      throw new NullPointerException("The operator must have a value");
    this._operator = op;
    this._arg = arg;
    this._vars = vars;
  }

  /**
   * Expands the token operator using the specified parameters.
   * 
   * @param parameters The parameters for variable substitution.
   * 
   * @return The corresponding expanded string.
   */
  public String expand(Parameters parameters) {
    return this._operator.expand(this._arg, this._vars, parameters);
  }

  /**
   * Returns the operator part of this token.
   * 
   * @return the operator.
   */
  public Operator operator() {
    return this._operator;
  }

  /**
   * Returns the argument part of this token.
   * 
   * @return the argument.
   */
  public String argument() {
    return this._arg;
  }

  /**
   * {@inheritDoc}
   */
  public List<Variable> variables() {
    return this._vars;
  }

  /**
   * Returns the operator if it is defined in this class.
   * 
   * @param op The string representation of an operator.
   * 
   * @return The corresponding operator instance.
   */
  public static Operator toOperator(String op) {
    for (Operator o : Operator.values()) {
      if (o.name().toLowerCase().equals(op))
        return o;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public boolean resolve(String expanded, Map<Variable, Object> values) {
    // TODO
    return false;
  }

// static methods -------------------------------------------------------------

  /**
   * Parses the specified string and returns the corresponding token.
   * 
   * This method accepts both the raw expression or the expression wrapped in curly brackets. 
   * 
   * @param exp The expression to parse.
   * 
   * @return The corresponding token.
   * 
   * @throws URITemplateSyntaxException If the string cannot be parsed as a valid
   */
  public static TokenOperatorD3 parse(String exp) throws URITemplateSyntaxException {
    String sexp = strip(exp);
    String[] parts = sexp.split("\\|");
    if (parts.length != 3)
      throw new URITemplateSyntaxException(sexp, "An operator expansion must has three parts");
    String op = parts[0];
    String arg = parts[1];
    String vars = parts[2];
    // parse the variables
    if (!op.startsWith("-"))
      throw new URITemplateSyntaxException(op, "An operator must start with '-'");
    Operator operator = TokenOperatorD3.toOperator(op.substring(1));
    if (operator == null)
      throw new URITemplateSyntaxException(op, "This operator is not supported");
    List<Variable> variables = toVariables(vars);
    return new TokenOperatorD3(operator, arg, variables);
  }

  /**
   * Generate the expression corresponding to the specified operator, argument and variables.
   * 
   * @param op The operator.
   * @param arg the argument.
   * @param vars The variables.
   */
  private static String toExpression(Operator op, String arg, List<Variable> vars) {
    StringBuffer exp = new StringBuffer();
    exp.append('{');
    exp.append(op.name().toLowerCase());
    exp.append('|');
    exp.append(arg);
    exp.append('|');
    for (Variable v : vars) {
      exp.append(v.toString());
    }
    exp.append('}');
    return exp.toString();
  }

}
