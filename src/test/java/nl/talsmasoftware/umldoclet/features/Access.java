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

/// Class to test different Access modifiers in Javadoc.
@SuppressWarnings("unused") // used in Javadoc.
public class Access {
    /// Private inner class.
    private static class PrivateClass {
        /// Private field.
        private String privateField;
        /// Package-protected field.
        String packageProtectedField;
        /// Protected field.
        protected String protectedField;
        /// Public field.
        public String publicField;

        /// Private constructor.
        private PrivateClass() {
            this.privateField = packageProtectedField = protectedField = publicField = null;
        }

        /// Private getter.
        ///
        /// @return the private value.
        private String getPrivateValue() {
            return privateField;
        }

        /// Package-protected getter.
        ///
        /// @return the package-protected value.
        String getPackageProtectedValue() {
            return packageProtectedField;
        }

        /// Protected getter.
        ///
        /// @return the protected value.
        protected String getProtectedValue() {
            return protectedField;
        }

        /// Public getter.
        ///
        /// @return the public value.
        public String getPublicValue() {
            return publicField;
        }
    }

    /// Protected inner class.
    protected static class ProtectedClass {
        /// Private field.
        private String privateField;
        /// Package-protected field.
        String packageProtectedField;
        /// Protected field.
        protected String protectedField;
        /// Public field.
        public String publicField;

        /// Protected constructor.
        protected ProtectedClass() {
            this.privateField = packageProtectedField = protectedField = publicField = null;
        }

        /// Private getter.
        ///
        /// @return the private value.
        private String getPrivateValue() {
            return privateField;
        }

        /// Package-protected getter.
        ///
        /// @return the package-protected value.
        String getPackageProtectedValue() {
            return packageProtectedField;
        }

        /// Protected getter.
        ///
        /// @return the protected value.
        protected String getProtectedValue() {
            return protectedField;
        }

        /// Public getter.
        ///
        /// @return the public value.
        public String getPublicValue() {
            return publicField;
        }
    }

    /// Default constructor.
    public Access() {
        super();
    }
}
