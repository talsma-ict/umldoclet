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
 * Influences how Fields are rendered in the UML.
 *
 * @author Sjoerd Talsma
 */
public interface FieldConfig {

    /**
     * How to display the type of the field in the UML diagram (if {@link #include(Visibility) included}).
     * Possible values how to display field types are summarized in the {@link TypeDisplay} description.
     *
     * @return the configured way of displaying the types for fields in the UML diagram.
     */
    TypeDisplay typeDisplay();

    /**
     * Return whether to include fields with the specified {@link Visibility} into
     * the UML diagram.
     *
     * @param fieldVisibility The disibility of the field evaluated for inclusion in the UML diagram.
     * @return {@code true} if fields with the requested visibility should be included in the UML diagram
     * or {@code false} otherwise.
     */
    boolean include(Visibility fieldVisibility);

}
