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
/// Doclet for the JavaDoc tool that generates UML diagrams from the code.
///
///
/// Released versions can be found in the
/// [maven central repository](http://mvnrepository.com/artifact/nl.talsmasoftware/umldoclet) or
/// [on github](https://github.com/talsma-ict/umldoclet/releases).
///
///
/// This doclet uses JavaDoc metadata available to automatically generate the following
/// [UML diagrams](http://plantuml.com/) and add them to your documentation:
/// <ul>
/// <li>Class diagrams
/// <li>Package diagrams
/// <li>A package dependency diagram
/// </ul>
/// The doclet will warn about (and optionally fail on) cyclic package dependencies.
///
/// <h2>Usage</h2>
///
/// Please see the separate [Usage page](https://github.com/talsma-ict/umldoclet/blob/main/usage.md)
/// on how to use the UML doclet in your own Java projects.
///
/// <h2>Examples</h2>
///
/// The javadoc of the UMLDoclet itself is probably a decent example of what the default settings provide for you:
/// <ul>
/// <li>[Main javadoc page](https://javadoc.io/doc/nl.talsmasoftware/umldoclet)
/// <li>[Simple package example: *nl.talsmasoftware.umldoclet.rendering.writers*](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/writers/package-summary.html)
/// <li>[Complex package example: *nl.talsmasoftware.umldoclet.uml*](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/uml/package-summary.html)
/// <li>[Class example: *nl.talsmasoftware.umldoclet.rendering.indent.Indentation*](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.html)
/// </ul>
package nl.talsmasoftware.umldoclet;
