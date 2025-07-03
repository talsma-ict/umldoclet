/*
 * Copyright 2016-2025 Talsma ICT
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
/**
 * Doclet for the JavaDoc tool that generates UML diagrams from the code.
 *
 * <p>
 * Released versions can be found in the
 * <a href="http://mvnrepository.com/artifact/nl.talsmasoftware/umldoclet">maven central repository</a> or
 * <a href="https://github.com/talsma-ict/umldoclet/releases">on github</a>.
 *
 * <p>
 * This doclet uses JavaDoc metadata available to automatically generate the following
 * <a href="http://plantuml.com/">UML diagrams</a> and add them to your documentation:
 * <ul>
 *     <li>Class diagrams
 *     <li>Package diagrams
 *     <li>A package dependency diagram
 * </ul>
 * The doclet will warn about (and optionally fail on) cyclic package dependencies.
 *
 * <h2>Usage</h2>
 * <p>
 * Please see the separate <a href="https://github.com/talsma-ict/umldoclet/blob/develop/usage.md">Usage page</a>
 * on how to use the UML doclet in your own Java projects.
 *
 * <h2>Examples</h2>
 * <p>
 * The javadoc of the UMLDoclet itself is probably a decent example of what the default settings provide for you:
 * <ul>
 *     <li><a href="https://javadoc.io/doc/nl.talsmasoftware/umldoclet">Main javadoc page</a>
 *     <li><a href="https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/writers/package-summary.html">Simple package example: <em>nl.talsmasoftware.umldoclet.rendering.writers</em></a>
 *     <li><a href="https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/uml/package-summary.html">Complex package example: <em>nl.talsmasoftware.umldoclet.uml</em></a>
 *     <li><a href="https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.html">Class example: <em>nl.talsmasoftware.umldoclet.rendering.indent.Indentation</em></a>
 * </ul>
 */
package nl.talsmasoftware.umldoclet;
