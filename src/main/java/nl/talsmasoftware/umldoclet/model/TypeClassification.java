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
package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.Renderer;

import java.io.IOException;

/**
 * @author Sjoerd Talsma
 */
public enum TypeClassification implements Renderer {
    ENUM, INTERFACE, ANNOTATION, ABSTRACT_CLASS, CLASS;

    @Override
    public <A extends Appendable> A writeTo(A output) {
        try {
            output.append(name().toLowerCase().replace('_', ' '));
            return output;
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O exception rendering " + this + ": " + ioe.getMessage(), ioe);
        }
    }

}
