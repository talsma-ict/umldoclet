/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.umldoclet.testing.deprecation;

/**
 * This class itself is not deprecated, but contains several items that are (in various ways).
 */
public class ClassWithDeprecatedItems {

    public int notDeprecatedField;

    @Deprecated
    public String deprecatedFieldByAnnotation;

    /**
     * @deprecated Javadoc deprecation
     */
    public Object deprecatedFieldByJavadoc;

    public int notDeprecatedMethod() {
        return notDeprecatedField;
    }

    @Deprecated
    public String deprecatedMethodByAnnotation() {
        return deprecatedFieldByAnnotation;
    }

    /**
     * @deprecated Again, method deprecation by using javadoc.
     */
    public Object deprecatedMethodByJavadoc() {
        return deprecatedFieldByJavadoc;
    }


}
