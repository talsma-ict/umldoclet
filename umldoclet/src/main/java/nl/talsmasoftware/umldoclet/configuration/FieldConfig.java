/*
 * Copyright 2016-2024 Talsma ICT
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
 * Configuration how Fields are rendered in the UML.
 *
 * @author Sjoerd Talsma
 */
public interface FieldConfig {

    /**
     * Set how field types are rendered in the UML diagram.
     *
     * @return How field types are rendered.
     */
    TypeDisplay typeDisplay();

    /**
     * Whether to include fields with the specified {@link Visibility} in the UML diagram.
     *
     * @param fieldVisibility The visibility of the evaluated field.
     * @return {@code true} if fields with the requested visibility must be included in the UML diagram
     * or {@code false} otherwise.
     */
    boolean include(Visibility fieldVisibility);

}
