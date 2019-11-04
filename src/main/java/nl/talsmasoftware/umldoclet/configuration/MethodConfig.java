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
package nl.talsmasoftware.umldoclet.configuration;

/**
 * Influences how Methods are rendered in the UML.
 *
 * @author Sjoerd Talsma
 */
public interface MethodConfig {
    /**
     * How parameter names must be rendered.
     *
     * <p>
     * {@code NONE} will omit the name for the parameter names,
     * {@code BEFORE_TYPE} will render the name first, followed by the type, while
     * {@code AFTER_TYPE} renders the type first followed by the parameter name (java-style).
     */
    enum ParamNames {
        /**
         * Omit names of method parameters.
         */
        NONE,

        /**
         * Render the method parameter name first, followed by its type.
         */
        BEFORE_TYPE,

        /**
         * Render the method parameter type first, followed by its name.
         */
        AFTER_TYPE
    }

    /**
     * How method parameter names must be rendered.
     *
     * @return How method parameter names must be rendered.
     */
    ParamNames paramNames();

    /**
     * How parameter types must be rendered.
     *
     * @return How parameter types must be rendered.
     */
    TypeDisplay paramTypes();

    /**
     * How method return types must be rendered.
     *
     * @return How method return types must be rendered.
     */
    TypeDisplay returnType();

    /**
     * Return whether the method with specified visibility should be included
     * in the UML diagram.
     *
     * @param methodVisibility The method visibility.
     * @return {@code true} if the method should be included in the UML diagram
     * based on its visibility, or {@code false} if it should be omitted.
     */
    boolean include(Visibility methodVisibility);

    /**
     * Whether JavaBean property accessor methods
     * such as {@code getXyz()}, {@code isXyz()}, {@code setXyz(Xyz xyz)}
     * should be rendered as Fields in UML.
     *
     * @return {@code true} if JavaBean accessor methods should be rendered as Fields
     * in the UML diagram, {@code false} to render them as normal methods.
     */
    boolean javaBeanPropertiesAsFields();
}
