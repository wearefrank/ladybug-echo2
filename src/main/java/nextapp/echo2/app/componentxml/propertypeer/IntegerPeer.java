/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo2.app.componentxml.propertypeer;

import org.w3c.dom.Element;

import nextapp.echo2.app.componentxml.ComponentIntrospector;
import nextapp.echo2.app.componentxml.InvalidPropertyException;
import nextapp.echo2.app.componentxml.PropertyXmlPeer;

/**
 * <code>PropertyXmlPeer</code> implementation for 
 * <code>java.lang.Integer</code> properties.
 */
public class IntegerPeer 
implements PropertyXmlPeer {

    /**
     * @see nextapp.echo2.app.componentxml.PropertyXmlPeer#getValue(java.lang.ClassLoader, 
     *      java.lang.Class, org.w3c.dom.Element)
     */
    public Object getValue(ClassLoader classLoader, Class objectClass, Element propertyElement)
    throws InvalidPropertyException {
        String stringValue = propertyElement.getAttribute("value");
        try {
            return new Integer(stringValue);
        } catch (NumberFormatException ex) {
            return introspectConstantValue(classLoader, objectClass, stringValue);
        }
    }
    
    /**
     * @param classLoader the <code>ClassLoader</code> to use for introspection
     * @param objectClass the <code>Class</code> of object containing candidate 
     *        constant values
     * @param value the name of the constant value
     * @return an integer representing the constant value, or null if the 
     *         constant is not found.
     */
    public Integer introspectConstantValue(ClassLoader classLoader, Class objectClass, String value) 
    throws InvalidPropertyException {
        try {
            ComponentIntrospector ci = ComponentIntrospector.forName(objectClass.getName(), classLoader);
            if (value.startsWith(objectClass.getName())) {
                // Remove class name if required.
                value = value.substring(objectClass.getName().length() + 1);
            }
            Object constantValue = ci.getConstantValue(value);
            if (constantValue instanceof Integer) {
                return (Integer) constantValue;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            // Should not occur.
            throw new InvalidPropertyException("Object class not found.", ex);  
        }
    }
}
