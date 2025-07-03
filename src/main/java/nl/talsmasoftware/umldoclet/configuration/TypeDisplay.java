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
package nl.talsmasoftware.umldoclet.configuration;

/**
 * How a type name is rendered in UML.
 *
 * <dl>
 * <dt>{@code NONE}</dt><dd>Omit the type</dd>
 * <dt>{@code SIMPLE}</dt><dd>Use the simple type name (without its containing package)</dd>
 * <dt>{@code QUALIFIED}</dt><dd>Use the qualified type name</dd>
 * <dt>{@code QUALIFIED_GENERICS}</dt><dd>Use the qualified type name, also for its generic types</dd>
 * </dl>
 *
 * @author Sjoerd Talsma
 */
public enum TypeDisplay {
    /**
     * Omit the type name.
     */
    NONE,

    /**
     * Use the simple type name without the containing package.
     */
    SIMPLE,

    /**
     * Use the qualified type name.
     */
    QUALIFIED,

    /**
     * Use the qualified type name, also for its generic type variables.
     */
    QUALIFIED_GENERICS
}
