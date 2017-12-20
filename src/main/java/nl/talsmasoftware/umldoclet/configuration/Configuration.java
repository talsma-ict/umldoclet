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
package nl.talsmasoftware.umldoclet.configuration;

import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

/**
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

}
