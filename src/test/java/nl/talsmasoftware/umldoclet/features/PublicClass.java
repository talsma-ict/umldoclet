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
package nl.talsmasoftware.umldoclet.features;

/// Public class used to test Javadoc creation.
@SuppressWarnings("unused") // Used in Javadoc testing.
public class PublicClass {
    /// Private field.
    private String privateField;
    /// Package-protected field.
    String packageProtectedField;
    /// Protected field.
    protected String protectedField;
    /// Public field.
    public String publicField;

    /// Default constructor.
    public PublicClass() {
        this.privateField = packageProtectedField = protectedField = publicField = null;
    }

    /// Private getter.
    /// @return the private value.
    private String getPrivateValue() {
        return privateField;
    }

    /// Package-protected getter.
    /// @return the package-protected value.
    String getPackageProtectedValue() {
        return packageProtectedField;
    }

    /// Protected getter.
    /// @return the protected value.
    protected String getProtectedValue() {
        return protectedField;
    }

    /// Public getter.
    /// @return the public value.
    public String getPublicValue() {
        return publicField;
    }

    /// Private inner class.
    private static final class PrivateInnerClass {
        /// Private default constructor.
        private PrivateInnerClass() {
            super();
        }
    }

    /// Package-protected inner class.
    static final class PackageProtectedInnerClass {
        /// Package-protected default constructor.
        PackageProtectedInnerClass() {
            super();
        }
    }

    /// Protected inner class.
    protected static final class ProtectedInnerClass {
        /// Protected default constructor.
        protected ProtectedInnerClass() {
            super();
        }
    }

    /// Public inner class.
    public static final class PublicInnerClass {
        /// Default constructor.
        public PublicInnerClass() {
            super();
        }
    }
}
