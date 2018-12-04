/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.weborganic.furi.URICoder;
import org.weborganic.furi.Variable.Form;

/**
 * A token based on the operators defined in the latest draft.
 * 
 * <pre>
 *  instruction   = &quot;{&quot; [ operator ] variable-list &quot;}&quot;
 *  operator      = &quot;/&quot; / &quot;+&quot; / &quot;;&quot; / &quot;?&quot; / op-reserve
 *  variable-list =  varspec *( &quot;,&quot; varspec )
 *  varspec       =  [ var-type ] varname [ &quot;:&quot; prefix-len ] [ &quot;=&quot; default ]
 *  var-type      = &quot;@&quot; / &quot;%&quot; / type-reserve
 *  varname       = ALPHA *( ALPHA | DIGIT | &quot;_&quot; )
 *  prefix-len    = 1*DIGIT
 *  default       = *( unreserved / reserved )
 *  op-reserve    = &lt;anything else that isn't ALPHA or operator&gt;
 *  type-reserve  = &lt;anything else that isn't ALPHA, &quot;,&quot;, or operator&gt;
 * </pre>
 * 
 * @see <a href="http://code.google.com/p/uri-templates/source/browse/trunk/spec/draft-gregorio-uritemplate.xml">URI
 * Template Library draft specifications at Google Code</a>
 * 
 * @author Christophe Lauret
 * @version 5 November 2009
 */
public class TokenOperatorDX extends TokenBase implements TokenOperator, Matchable {

  /**
   * The pattern for the URI defined pchar:
   * 
   * <pre>
   * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
   * pct-encoded = "%" HEXDIG HEXDIG
   * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
   * sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
   * </pre>
   * 
   * To avoid side-effects with the resolvers non-capturing groups are used.
   * 
   * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">Uniform Resource Identifier (URI): Generic
   *      Syntax</a>
   */
  protected static final Pattern PCHAR = Pattern.compile("(?:[\\w-_.~!$&'()*+,;=:@]|(?:%[0-9a-fA-F]{2}))");

  /**
   * The list of operators currently supported.
   */
  public enum Operator {

    /**
     * The '?' operator for query parameters.
     * 
     * Example:
     * 
     * <pre>
     *  undef = null;
     *  empty = &quot;&quot;;
     *  x     = &quot;1024&quot;;
     *  y     = &quot;768&quot;;
     * 
     * {?x,y}                    ?x=1024&amp;y=768
     * {?x,y,empty}              ?x=1024&amp;y=768&amp;empty=
     * {?x,y,undef}              ?x=1024&amp;y=768
     * </pre>
     */
    QUERY_PARAMETER('?') {
      @Override
      public String expand(List<Variable> vars, Parameters parameters) {
        if (parameters == null) return "";
        StringBuffer expansion = new StringBuffer();
        boolean first = true;
        for (Variable var : vars) {
          if (parameters.exists(var.name())) {
            String[] values = var.values(parameters);
            // Associative Array: odd indexed values are names, even are values
            if (var.form() == Form.MAP) {
              for (int i = 0; i < values.length; i++) {
                expansion.append(first ? '?' : '&');
                expansion.append(URICoder.encode(values[i])).append('=');
                if (values.length > i+1) {
                  expansion.append(URICoder.encode(values[++i]));
                }
                first = false;
              }
            // List: names, automatically number the names
            } else if (var.form() == Form.LIST) {
              for (int i = 0; i < values.length; i++) {
                expansion.append(first ? '?' : '&');
                expansion.append(var.name());
                if (i > 0) expansion.append(i+1);
                expansion.append('=').append(URICoder.encode(values[i]));
                first = false;
              }
            // String: join the values with a comma
            } else {
              expansion.append(first? '?' : '&');
              expansion.append(var.name()).append('=');
              for (int i = 0; i < values.length; i++) {
                if (i > 0) expansion.append(','); 
                expansion.append(URICoder.encode(values[i]));
              }
              first = false;
            }

          }
        }
        return expansion.toString();
      }

      @Override
      boolean isResolvable(List<Variable> arg0) {
        return true;
      }

      @Override
      boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values) {
        for (Variable var : vars) {
          Pattern p = Pattern.compile("(?<=[&?]"+var.namePatternString()+"=)([^&#]*)");
          Matcher m = p.matcher(value);
          while(m.find()) {
            values.put(var, m.group());
          }
        }
        return true;
      }

      @Override
      Pattern pattern(List<Variable> vars) {
        StringBuffer pattern = new StringBuffer();
        pattern.append("\\?(");
        for (Variable var : vars) {
          pattern.append('(');
          pattern.append(var.namePatternString());
          pattern.append("=[^&#]*)|");
        }
        pattern.append("&)*");
        return Pattern.compile(pattern.toString());
      }
    },

    /**
     * The ';' operator for path parameters.
     * 
     * Example:
     * 
     * <pre>
     *  undef = null;
     *  empty = &quot;&quot;;
     *  x     = &quot;1024&quot;;
     *  y     = &quot;768&quot;;
     * 
     * {;x,y}                    ;x=1024;y=768
     * {;x,y,empty}              ;x=1024;y=768;empty
     * {;x,y,undef}              ;x=1024;y=768
     * </pre>
     */
    PATH_PARAMETER(';') {
      @Override
      String expand(List<Variable> vars, Parameters parameters) {
        if (parameters == null) return "";
        StringBuffer expansion = new StringBuffer();
        for (Variable var : vars) {
          if (parameters.exists(var.name())) {
            // An associative array: odd index for names, even index for values
            if (var.form() == Form.MAP) {
              String[] values = var.values(parameters);
              for (int i = 0; i < values.length; i++) {
                expansion.append(';').append(URICoder.encode(values[i]));
                if (values.length > i+1) {
                  expansion.append('=').append(URICoder.encode(values[++i]));
                }
              }
            // A list
            } else if (var.form() == Form.LIST) {
              // TODO: what should it be?
              String[] values = var.values(parameters);
              for (String value : values) {
                expansion.append(';');
                expansion.append(var.name());
                if (value.length() > 0)
                  expansion.append('=').append(URICoder.encode(value));
              }
            // A string
            } else {
              String[] values = var.values(parameters);
              for (String value : values) {
                expansion.append(';').append(var.name());
                if (value.length() > 0)
                  expansion.append('=').append(URICoder.encode(value));
              }        
            }
          }
        }
        return expansion.toString();
      }

      @Override
      boolean isResolvable(List<Variable> vars) {
        return true;
      }

      @Override
      boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values) {
        for (Variable var : vars) {
          Pattern p = Pattern.compile("(?<=;"+var.namePatternString()+"=)([^;/?#]*)");
          Matcher m = p.matcher(value);
          while(m.find()) {
            values.put(var, m.group());
          }
        }
        return true;
      }

      @Override
      Pattern pattern(List<Variable> vars) {
        StringBuffer pattern = new StringBuffer();
        pattern.append("(?:");
        for (Variable var : vars) {
          pattern.append("(?:;");
          pattern.append(var.namePatternString());
          pattern.append("=[^;/?#]*)|");
        }
        pattern.append(";)*");
        return Pattern.compile(pattern.toString());
      }
    },

    /**
     * The '/' operator for path segments.
     * 
     * Example:
     * 
     * <pre>
     *  list  = [ &quot;val1&quot;, &quot;val2&quot;, &quot;val3&quot; ];
     *  x     = &quot;1024&quot;;
     *  
     *  {/list,x}                 /val1/val2/val3/1024
     * </pre>
     */
    PATH_SEGMENT('/') {

      @Override
      String expand(List<Variable> vars, Parameters parameters) {
        if (parameters == null) return "";
        StringBuffer expansion = new StringBuffer();
        for (Variable var : vars) {
          if (parameters.exists(var.name())) {
            String[] values = var.values(parameters);
            for (String value : values) {
              expansion.append('/');
              expansion.append(URICoder.encode(value));
            }
          }
        }
        return expansion.toString();
      }

      @Override
      boolean isResolvable(List<Variable> arg0) {
        return true;
      }

      @Override
      boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values) {
        if (vars.size() != 1)
          throw new UnsupportedOperationException("Operator + cannot be resolved with multiple variables.");
        values.put(vars.get(0), URICoder.decode(value));
        return true;
      }

      @Override
      Pattern pattern(List<Variable> vars) {
        return Pattern.compile("(?:/[^/?#]*)*");
      }
    },

    /**
     * The '+' operator for URI inserts.
     * 
     * Example:
     * 
     * <pre>
     * empty = &quot;&quot;
     * path  = &quot;/foo/bar&quot;
     * x     = &quot;1024&quot;
     *  
     *  {+path}/here              /foo/bar/here
     *  {+path,x}/here            /foo/bar,1024/here
     *  {+path}{x}/here           /foo/bar1024/here
     *  {+empty}/here             /here
     * </pre>
     */
    URI_INSERT('+') {

      @Override
      String expand(List<Variable> vars, Parameters parameters) {
        if (parameters == null) return "";
        StringBuffer expansion = new StringBuffer();
        for (Iterator<Variable> i = vars.iterator(); i.hasNext();) {
          Variable var = i.next();
          if (parameters.exists(var.name())) {
            String[] values = var.values(parameters);
            for (String value : values) {
              expansion.append(URICoder.minimalEncode(value));
            }
          }
          if (i.hasNext())
            expansion.append(',');
        }
        return expansion.toString();
      }

      @Override
      boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values) {
        // TODO: should we return false instead??
        if (vars.size() != 1)
          throw new UnsupportedOperationException("Operator + cannot be resolved with multiple variables.");
        values.put(vars.get(0), URICoder.decode(value));
        return true;
      }

      @Override
      boolean isResolvable(List<Variable> vars) {
        return vars.size() == 1;
      }

      @Override
      Pattern pattern(List<Variable> vars) {
        return Pattern.compile("[^?#]*");
      }
    },

    /**
     * The substitution operator is only used to aggregate variables.
     */
    SUBSTITUTION(' ') {

      @Override
      String expand(List<Variable> vars, Parameters parameters) {
        if (parameters == null) return "";
        StringBuffer expansion = new StringBuffer();
        for (Iterator<Variable> i = vars.iterator(); i.hasNext();) {
          Variable var = i.next();
          if (parameters.exists(var.name())) {
            String[] values = var.values(parameters);
            for (String value : values) {
              expansion.append(URICoder.encode(value));
            }
          }
          if (i.hasNext())
            expansion.append(',');
        }
        return expansion.toString();
      }

      @Override
      boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values) {
        // TODO: should we return false instead??
        // TODO: could we somewhat support a comma separated list of values?
        if (vars.size() != 1)
          throw new UnsupportedOperationException("Operator cannot be resolved with multiple variables.");
        values.put(vars.get(0), URICoder.decode(value));
        return true;
      }

      @Override
      boolean isResolvable(List<Variable> vars) {
        return vars.size() == 1;
      }

      @Override
      Pattern pattern(List<Variable> vars) {
        return Pattern.compile("[^;/?#,&]*");
      }
    };

    /**
     * The character used to represent this operator.
     */
    private final char _c;

    /**
     * Creates a new operator.
     * 
     * @param c The character used to represent this operator.
     */
    private Operator(char c) {
      this._c = c;
    }

    /**
     * Returns the character.
     * 
     * @return The character used to represent this operator.
     */
    public char character() {
      return this._c;
    }

    /**
     * Indicates whether the operator can be resolved.
     * 
     * @param vars The variables for the operator.
     */
    abstract boolean isResolvable(List<Variable> vars);

    /**
     * Apply the expansion rules defined for the operator given the specified argument, variable and
     * parameters.
     * 
     * @param vars The variables for the operator.
     * @param params The parameters to use.
     */
    abstract String expand(List<Variable> vars, Parameters params);

    /**
     * Returns the pattern for this operator given the specified list of variables.
     * 
     * @param vars The variables for the operator.
     */
    abstract Pattern pattern(List<Variable> vars);

    /**
     * Returns the map of the string to values given  the specified data.
     */
    abstract boolean resolve(List<Variable> vars, String value, Map<Variable, Object> values);

  }

  /**
   * The operator.
   */
  private Operator _operator;

  /**
   * The variables for this token.
   */
  private List<Variable> _vars;

  /**
   * The pattern for this token.
   */
  private Pattern _pattern;

  /**
   * Creates a new operator token for one variable only.
   * 
   * @param op  The operator to use.
   * @param var The variable for this operator.
   * 
   * @throws NullPointerException If any of the argument is <code>null</code>.
   */
  public TokenOperatorDX(Operator op, Variable var) throws NullPointerException {
    super(toExpression(op, var));
    if (op == null || var == null)
      throw new NullPointerException("The operator must have a value");
    this._operator = op;
    this._vars = new ArrayList<Variable>(1);
    this._vars.add(var);
    this._pattern = op.pattern(this._vars);
  }

  /**
   * Creates a new operator token.
   * 
   * @param op The operator to use.
   * @param vars The variables for this operator.
   * 
   * @throws NullPointerException If any of the argument is <code>null</code>.
   */
  public TokenOperatorDX(Operator op, List<Variable> vars) throws NullPointerException {
    super(toExpression(op, vars));
    if (op == null || vars == null)
      throw new NullPointerException("The operator must have a value");
    this._operator = op;
    this._vars = vars;
    this._pattern = op.pattern(vars);
  }

  /**
   * Expands the token operator using the specified parameters.
   * 
   * @param parameters The parameters for variable substitution.
   * 
   * @return The corresponding expanded string.
   */
  public String expand(Parameters parameters) {
    return this._operator.expand(this._vars, parameters);
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
   * Returns the list of variables used in this token.
   * 
   * @return the list of variables.
   */
  public List<Variable> variables() {
    return this._vars;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isResolvable() {
    return this._operator.isResolvable(this._vars);
  }

  /**
   * {@inheritDoc}
   */
  public boolean resolve(String expanded, Map<Variable, Object> values) {
    if (this.isResolvable()) {
      this._operator.resolve(this._vars, expanded, values);
      return true;
    } else {
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean match(String part) {
    return this._pattern.matcher(part).matches();
  }

  /**
   * {@inheritDoc}
   */
  public Pattern pattern() {
    return this._pattern;
  }

  /**
   * Returns the operator if it is defined in this class.
   * 
   * @param c The character representation of the operator.
   * 
   * @return The corresponding operator instance.
   */
  public static Operator toOperator(char c) {
    for (Operator o : Operator.values()) {
      if (o.character() == c)
        return o;
    }
    // default on simple substitution
    return Operator.SUBSTITUTION;
  }

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
  public static TokenOperatorDX parse(String exp) throws URITemplateSyntaxException {
    String sexp = strip(exp);
    if (sexp.length() < 2)
      throw new URITemplateSyntaxException(exp, "Cannot produce a valid token operator.");
    char c = sexp.charAt(0);
    Operator operator = TokenOperatorDX.toOperator(c);
    if (operator == null)
      throw new URITemplateSyntaxException(String.valueOf(c), "This operator is not supported");
    List<Variable> variables = toVariables(operator == Operator.SUBSTITUTION? sexp : sexp.substring(1));
    return new TokenOperatorDX(operator, variables);
  }

// private helpers --------------------------------------------------------------------------------

  /**
   * Generate the expression corresponding to the specified operator and variable.
   * 
   * @param op The operator.
   * @param var The variable.
   */
  private static String toExpression(Operator op, Variable var) {
    return "{"+op.character()+var.name()+'}';
  }

  /**
   * Generate the expression corresponding to the specified operator, argument and variables.
   * 
   * @param op The operator.
   * @param vars The variables.
   */
  private static String toExpression(Operator op, List<Variable> vars) {
    StringBuffer exp = new StringBuffer();
    exp.append('{');
    exp.append(op.character());
    boolean first = true;
    for (Variable v : vars) {
      if (!first) exp.append(','); 
      exp.append(v.toString());
      first = false;
    }
    exp.append('}');
    return exp.toString();
  }

}
