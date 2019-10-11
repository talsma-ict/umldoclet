/*
 * Copyright 2016-2019 Talsma ICT
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
 * Package dedicated to the HTML postprocessing required to embed UML diagrams into the documentation.
 *
 * <p>
 * This package is implemented as a standalone {@linkplain nl.talsmasoftware.umldoclet.html.HtmlPostprocessor} that
 * uses the {@code Configuration} to find the locations of both
 * the generated <abbr title="Hypertext Markup Language">HTML</abbr> and
 * the generated <abbr title="Unified Modeling Language">UML</abbr> diagrams.<br>
 * Using these locations and knowledge of the naming conventions results in relative paths to
 * the various diagrams from the HTML that documents them.
 * The HTML files are then modified by inserting these relative diagram references.
 *
 * <p>
 * Scalable Vector Graphics ({@code SVG} diagrams) are inserted as {@code <object>} tags,
 * which makes their links clickable from the document.
 * All other images are inserted as normal {@code <img>} tags.
 */
package nl.talsmasoftware.umldoclet.html;