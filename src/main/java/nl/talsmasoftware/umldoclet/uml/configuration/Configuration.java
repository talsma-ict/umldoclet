/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml.configuration;

import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.util.List;

/**
 * Configuration about <em>how</em> UML should be rendered.
 *
 * @author Sjoerd Talsma
 */
public interface Configuration {

    Logger getLogger();

    /**
     * @return The configured indentation within the generated UML files.
     */
    Indentation getIndentation();

    /**
     * @return The destination directory for the UML diagrams, or the empty string {@code ""} for the current directory.
     */
    String getDestinationDirectory();

    /**
     * @return The part of the configuration that determines how fields are rendered.
     */
    FieldConfig getFieldConfig();

    /**
     * @return The part of the configuration that determines how methods are rendered.
     */
    MethodConfig getMethodConfig();

    /**
     * @return The types (classes, interfaces) that are excluded as references.
     */
    List<String> getExcludedTypeReferences();

}
