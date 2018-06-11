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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

/**
 * Model object for a Field in an UML class.
 *
 * @author Sjoerd Talsma
 */
public class Field extends TypeMember {

    private Field(Type containingType, Visibility visibility, boolean isStatic, boolean isDeprecated, String name,
                  TypeName type) {
        super(containingType, visibility, false, isStatic, isDeprecated, name, type);
    }

    public Field(Type containingType, Visibility visibility, boolean isStatic, String name, TypeName type) {
        this(containingType, visibility, isStatic, false, name, type);
    }

    public Field deprecated() {
        return new Field(containingType, visibility, isStatic, true, name, type);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (getConfiguration().fields().include(visibility)) super.writeTo(output);
        return output;
    }

}
