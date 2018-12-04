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
 * Defines a variable type.
 * 
 * The variable type may be used for any purpose. It is just a mechanism to qualify variables further
 * than merely by name.
 * 
 * Systems may choose to use a variable type to indicate a particular behaviour for the variable or
 * to enforce a particular type of value.
 * 
 * @author Christophe Lauret
 * @version 11 June 2009
 */
public class VariableType {

    /**
     * The pattern for a valid variable name.
     */
    private static final Pattern VALID_NAME = Pattern.compile("[a-zA-Z0-9][\\w.-]*");

    /**
     * The name of this variable type.
     */
    private String _name;

    /**
     * Create a new variable type.
     * 
     * @throws NullPointerException If the specified name is <code>null</code>.
     * @throws IllegalArgumentException If the specified name is an empty string.
     */
    public VariableType(String name) {
        if (name == null) {
            throw new NullPointerException("A variable type must have a name, but was null");
        }
        if (!isValidName(name)) {
            throw new IllegalArgumentException("The variable name is not valid: " + name);
        }
        this._name = name;
    }

    /**
     * Return the name of this variable type.
     *
     * @return The name of this variable type.
     */
    public String getName() {
        return this._name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if ((o == null) || (o.getClass() != this.getClass())) {
            return false;
        }
        VariableType v = (VariableType) o;

        // name and default cannot be null
        return _name.equals(v._name);
    }

    @Override
    public int hashCode() {
        return this._name.hashCode();
    }

    @Override
    public String toString() {
        return this._name;
    }

    /**
     * Indicates whether the name of this variable type is valid.
     * 
     * @param name The name of the variable.
     * 
     * @return <code>true</code> if the name is valid; <code>false</code> otherwise.
     */
    protected static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return VALID_NAME.matcher(name).matches();
    }

}
