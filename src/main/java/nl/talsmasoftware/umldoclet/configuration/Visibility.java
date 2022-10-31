/*
 * Copyright 2016-2022 Talsma ICT
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
package nl.talsmasoftware.umldoclet.configuration;

/**
 * Visibility for classes, methods and fields.
 * <p>
 * See <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html">Access Control</a> description
 * in the official Oracle documentation.
 * In the UML Doclet, the visibility is used for two purposes:
 * <ol>
 * <li>To represent the visibility of classes, methods and fields in the internal model for rendered diagrams.
 * <li>To 'ask' the configuration whether a particular diagram should render classes, methods or fields with
 * a particular visibility.
 * </ol>
 *
 * @author Sjoerd Talsma
 */
public enum Visibility {
    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PRIVATE private} modifier.
     */
    PRIVATE,

    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PROTECTED protected} modifier.
     */
    PROTECTED,

    /**
     * The visibility corresponding with the Java default {@link java.lang.reflect.Modifier}.
     */
    PACKAGE_PRIVATE,

    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PUBLIC public} modifier.
     */
    PUBLIC
}
