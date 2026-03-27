/*
 * Copyright 2016-2026 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.features.beans;

/// Standard java bean class.
public class StandardJavaBean {

    private String stringValue;
    private int intValue;
    private boolean booleanValue;
    private StandardJavaBean child;

    /// Default constructor.
    public StandardJavaBean() {
        super();
    }

    /// Getter for a string value.
    ///
    /// @return the string value.
    public String getStringValue() {
        return stringValue;
    }

    /// Setter for a string value.
    ///
    /// @param stringValue the string value to set.
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /// Getter for an integer value.
    ///
    /// @return the integer value.
    public int getIntValue() {
        return intValue;
    }

    /// Setter for an integer value.
    ///
    /// @param intValue the integer value to set.
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    /// Getter for a boolean value.
    ///
    /// @return the boolean value.
    public boolean isBooleanValue() {
        return booleanValue;
    }

    /// Setter for a boolean value.
    ///
    /// @param booleanValue the boolean value to set.
    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /// Getter for an object value.
    ///
    /// @return the object value.
    public StandardJavaBean getChild() {
        return child;
    }

    /// Setter for an object value.
    ///
    /// @param child the object value.
    public void setChild(StandardJavaBean child) {
        this.child = child;
    }

}
