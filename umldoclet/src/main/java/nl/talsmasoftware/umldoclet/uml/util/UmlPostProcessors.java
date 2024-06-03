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
package nl.talsmasoftware.umldoclet.uml.util;

import nl.talsmasoftware.umldoclet.uml.Type;

import java.util.function.Consumer;

/**
 * Utility class providing postprocessing functionality for generated UML models.
 */
public class UmlPostProcessors {

    /**
     * A post-processor for a uml {@linkplain Type} to replace getter and setter
     * {@linkplain nl.talsmasoftware.umldoclet.uml.Method methods}
     * into {@linkplain nl.talsmasoftware.umldoclet.uml.Field fields}.
     *
     * @return The postprocessor that updates types.
     */
    public Consumer<Type> javaBeanPropertiesAsFieldsPostProcessor() {
        return type -> JavaBeanProperty.detectFrom(type).forEach(JavaBeanProperty::replaceGetterAndSetterByField);
    }

}
