/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this license can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;

import java.util.regex.Pattern;

/**
 * A variable in a URL pattern or template.
 * 
 * The variables can be typed by prefixing the variable name. Types are not required, if no type is 
 * specified, the variable is considered untyped.
 * 
 * Note: there is no predefined list of types as the handling of types is out of scope. The syntax 
 * simply allows variables to be associated with a type.
 * 
 * Examples of variables:
 * <pre>
 *   foo         - An untyped variable named 'foo'
 *   bar         - An untyped variable named 'bar'
 *   ping:foo    - A variable named 'foo' typed 'ping'
 *   ping:foo=1  - A variable named 'foo' typed 'ping' which default value is '1'
 *   foo=pong    - An untyped variable named 'foo' which default value is 'pong'
 * </pre>
 * 
 * Variables only appear in the context of the a template expansion.
 * 
 * Expansion rule (4.4.1):
 * 
 * <pre>
 * &quot;In a variable ('var') expansion, if the variable is defined then substitute the value of 
 * the variable, otherwise substitute the default value. 
 * If no default value is given then substitute with the empty string.&quot;
 * </pre>
 * 
 * Syntax for variables:
 * <pre>
 * var         = [ vartype &quot;:&quot; ]  varname [ &quot;=&quot; vardefault ]
 * vars        = var [ *(&quot;,&quot; var) ]
 * vartype     = (ALPHA / DIGIT)* (ALPHA / DIGIT / &quot;.&quot; / &quot;_&quot; / &quot;-&quot; )
 * varname     = (ALPHA / DIGIT)* (ALPHA / DIGIT / &quot;.&quot; / &quot;_&quot; / &quot;-&quot; )
 * vardefault  = *(unreserved / pct-encoded)
 * </pre>
 * 
 * @see <a
 *      href="http://bitworking.org/projects/URI-Templates/spec/draft-gregorio-uritemplate-03.html">URI
 *      Template (Internet Draft 3)</a>
 * @see <a href="http://tools.ietf.org/html/rfc3986">RFC 3986 - Uniform Resource Identifier (URI):
 *      Generic Syntax<a/>
 * 
 * @author Christophe Lauret
 * @version 11 June 2009
 */
public class Variable {

  /**
   * Used for reserved variable names.
   */
  public enum Reserved {

    /**
     * The wildcard represented by the 'asterisk'
     */
    WILDCARD("*");

    /**
     * The symbol for this reserved.
     */
    private String _symbol;

    /**
     * Construct a new reserved variable - keep it private.
     * 
     * @param symbol The symbol used for this reserved variable name.
     */
    private Reserved(String symbol) {
      this._symbol = symbol;
    }

    /**
     * @return the symbol used for this reserved variable name.
     */
    String symbol() {
      return this._symbol;
    }
  };

  /**
   * Indicate that the variable's value should be processed as a list ("@") or an associative array ("%").
   * 
   * This variable type is an instruction for the template processor.
   * It is not an indication of language or implementation type.
   */
  public enum Form {

    /**
     * Indicate that this variable can be expanded as a simple string (default).
     */
    STRING,

    /**
     * Indicate that this variable can be expanded as a list of strings.
     */
    LIST,

    /**
     * Indicates that this variable can be expanded as an associated array. 
     */
    MAP;

    /**
     * Returns the type of this variable from the specified expression.
     * 
     * <p>
     * This method does not return <code>null</code>
     * 
     * @param exp The expression.
     * @return The type of this expression.
     */
    protected static Form getType(String exp) {
      if (exp.length() == 0) return STRING;
      char c = exp.charAt(0);
      if (c == '@') return LIST;
      if (c == '%') return MAP;
      return STRING;
    }

  }

  /**
   * Indicate that the variable's value should be processed as a list ("@") or an associative array ("%").
   * 
   * This variable type is an instruction for the template processor.
   * It is not an indication of language or implementation type.
   */
  public enum Modifier {

    /**
     * Indicate that this variable can be expanded as a simple string (default).
     */
    SUBSTRING,

    /**
     * Indicate that this variable can be expanded as a list of strings.
     */
    REMAINDER;

  }

  /**
   * The pattern for a valid variable name.
   */
  private static final Pattern VALID_NAME = Pattern.compile("[a-zA-Z0-9][\\w.-]*");

  /**
   * The pattern for a valid normalised variable value: any unreserved character or an escape
   * sequence. This pattern contains non-capturing parentheses to make it easier to get variable
   * values as a group.
   */
  protected static final Pattern VALID_VALUE = Pattern.compile("(?:[\\w.~-]|(?:%[0-9a-fA-F]{2}))+");

  /**
   * The default value is an empty string.
   */
  private static final String DEFAULT_VALUE = "";

  /**
   * The type of this variable.
   */
  private Form _form = Form.STRING;

  /**
   * The implementation type of this variable (eg. string, integer, etc... can be user-defined).
   * 
   * <p>
   * Use <code>null</code> for untyped.
   */
  private VariableType _type;

  /**
   * The name of this variable.
   */
  private String _name;

  /**
   * The default value for this variable.
   */
  private String _default;

  /**
   * Creates a new untyped reserved variable.
   * 
   * @param reserved The name of the variable.
   * 
   * @throws NullPointerException If the specified name is <code>null</code>.
   * @throws IllegalArgumentException If the specified name is an empty string.
   */
  public Variable(Reserved reserved) throws NullPointerException, IllegalArgumentException {
    this._name = reserved.symbol();
    this._default = DEFAULT_VALUE;
    this._form = Form.STRING;
    this._type = null;
  }

  /**
   * Creates a new untyped variable.
   * 
   * @param name The name of the variable.
   * 
   * @throws NullPointerException If the specified name is <code>null</code>.
   * @throws IllegalArgumentException If the specified name is an empty string.
   */
  public Variable(String name) throws NullPointerException, IllegalArgumentException {
    this(name, DEFAULT_VALUE);
  }

  /**
   * Creates a new untyped variable.
   * 
   * @param name The name of the variable.
   * @param def The default value for the variable.
   * 
   * @throws NullPointerException If the specified name is <code>null</code>.
   * @throws IllegalArgumentException If the specified name is an empty string.
   */
  public Variable(String name, String def) throws NullPointerException, IllegalArgumentException {
    this(name, def, null);
  }

  /**
   * Creates a new variable.
   * 
   * @param name The name of the variable.
   * @param def  The default value for the variable.
   * @param type The type of the variable.
   * 
   * @throws NullPointerException If the specified name is <code>null</code>.
   * @throws IllegalArgumentException If the specified name is an empty string.
   */
  public Variable(String name, String def, VariableType type) throws NullPointerException,
      IllegalArgumentException {
    if (name == null)
      throw new NullPointerException("A variable must have a name, but was null");
    if (!isValidName(name))
      throw new IllegalArgumentException("The variable name is not valid: " + name);
    this._name = name;
    this._default = def != null ? def : DEFAULT_VALUE;
    this._type = type;
    this._form = Form.getType(name);
  }

  /**
   * Creates a new variable.
   * 
   * @param name The name of the variable.
   * @param def  The default value for the variable.
   * @param type The type of the variable.
   * 
   * @throws NullPointerException If the specified name is <code>null</code>.
   * @throws IllegalArgumentException If the specified name is an empty string.
   */
  public Variable(String name, String def, VariableType type, Form form) throws NullPointerException,
      IllegalArgumentException {
    if (name == null)
      throw new NullPointerException("A variable must have a name, but was null");
    if (!isValidName(name))
      throw new IllegalArgumentException("The variable name is not valid: " + name);
    this._name = name;
    this._default = def != null ? def : DEFAULT_VALUE;
    this._type = type;
    this._form = form != null? form : Form.STRING;
  }

  /**
   * Returns the form of this variable.
   * 
   * <p>
   * This method will never return <code>null</code>.
   * 
   * @return The form of this variable.
   */
  public Form form() {
    return this._form;
  }

  /**
   * Returns the name of this variable.
   * 
   * <p>
   * This method never return <code>null</code>.
   * 
   * @return The name of this variable.
   */
  public String name() {
    return this._name;
  }

  /**
   * Returns the default value for this variable.
   * 
   * This method never return <code>null</code>.
   * 
   * @return The default value for this variable.
   */
  public String defaultValue() {
    return this._default;
  }

  /**
   * Returns the implementation type of this variable.
   * 
   * <p>
   * This method will return <code>null</code> if the variable is untyped.
   * 
   * @return The type of this variable.
   */
  public VariableType type() {
    return this._type;
  }

  /**
   * Returns the expanded value of this variable.
   * 
   * If no value is specified for this variable, the default value is returned instead.
   * 
   * @param parameters The parameters.
   * 
   * @return The value.
   */
  public String value(Parameters parameters) {
    // No parameters: use the default value
    if (parameters == null)
      return this._default;
    // Defined and non-empty: return the first value in a list
    String[] values = parameters.getValues(this._name);
    if (values != null && values.length > 0 && values[0] != null) {
      return values[0];
      // Empty or undefined: return the default
    } else {
      return this._default;
    }
  }

  /**
   * Returns the expanded value of this variable.
   * 
   * If no values are specified for this variable, the default value is returned instead.
   * 
   * @param parameters The parameters.
   * 
   * @return The values.
   */
  public String[] values(Parameters parameters) {
    // No parameters: use the default value
    if (parameters == null)
      return new String[] { this._default };
    String[] values = parameters.getValues(this._name);
    // Defined and non-empty: return the values
    if (values != null && values.length > 0 && values[0].length() > 0) {
      return values;
      // Empty or undefined: return the default
    } else {
      return new String[] { this._default };
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if ((o == null) || (o.getClass() != this.getClass()))
      return false;
    Variable v = (Variable) o;
    // name and default cannot be null
    return _name.equals(v._name) && _default.equals(v._default);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this._name.hashCode() + 7 * this._default.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (this._default.length() > 0)
      return this._name + '=' + this._default;
    else
      return this._name;
  }

  // Static helpers
  // ==============================================================================================

  /**
   * Parses the given expression and returns the corresponding instance.
   * 
   * @param exp The expression to parse.
   * 
   * @return the corresponding variable.
   * 
   * @throws URITemplateSyntaxException If the expression cannot be parsed
   */
  public static Variable parse(String exp) throws URITemplateSyntaxException {
    // Capture the form if any
    Form f = Form.getType(exp);
    if (f != Form.STRING) exp = exp.substring(1);
    int colon = exp.indexOf(':');
    // untyped
    if (colon < 0) {
      Variable v = parseUntyped(exp);
      v._form = f;
      return v;
    // ignore the empty type and treat as untyped
    } else if (colon == 0) {
      Variable v = parseUntyped(exp.substring(1));
      v._form = f;
      return v;
    // a type is specified
    } else {
      Variable v = parseUntyped(exp.substring(colon + 1));
      v._type = new VariableType(exp.substring(0, colon));
      v._form = f;
      return v;
    }
  }

  /**
   * Parses the given expression and returns the corresponding instance.
   * 
   * @param exp The expression to parse.
   * 
   * @return the corresponding variable.
   * 
   * @throws URITemplateSyntaxException If the expression cannot be parsed
   */
  private static Variable parseUntyped(String exp) throws URITemplateSyntaxException {
    int equal = exp.indexOf('=');
    if (equal == 0)
      throw new URITemplateSyntaxException(exp, "Variable name is empty string");
    if (equal > 0) {
      return new Variable(exp.substring(0, equal), exp.substring(equal + 1));
    } else {
      return new Variable(exp, null);
    }
  }

  /**
   * Indicates whether the variable has a valid name according to the specifications.
   * 
   * @param name The name of the variable.
   * 
   * @return <code>true</code> if the name is valid; <code>false</code> otherwise.
   */
  public static boolean isValidName(String name) {
    if (name == null)
      return false;
    return VALID_NAME.matcher(name).matches();
  }

  /**
   * Indicates whether the variable has a valid value according to the specifications.
   * 
   * @param value The value of the variable.
   * 
   * @return <code>true</code> if the name is not valid; <code>false</code> otherwise.
   */
  public static boolean isValidValue(String value) {
    if (value == null)
      return false;
    return VALID_VALUE.matcher(value).matches();
  }

  // helpers -------------------------------------------------------------------

  /**
   * Returns the name of this variable as a regular expression pattern string for use in a regular
   * expression.
   * 
   * <p>
   * Implementation note: this method replaces any character that could be interpreted as a regex
   * meta-character, it is more efficient than using quotation (\Q...\E) for the whole string.
   * 
   * @return The regex pattern corresponding to this name.
   */
  protected String namePatternString() {
    return this._name.indexOf('.') < 0 ? this._name : this.name().replaceAll("\\.", "\\\\.");
  }

}
