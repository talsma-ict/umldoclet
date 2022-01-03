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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static nl.talsmasoftware.umldoclet.uml.Type.Classification.ENUM;

/**
 * Model object for a Field in an UML class.
 *
 * @author Sjoerd Talsma
 */
public class Field extends TypeMember {

    public Field(Type containingType, String name, TypeName type) {
        super(containingType, name, type);
    }

    private boolean isEnumType() {
        return isStatic
                && getParent() instanceof Type
                && ENUM.equals(((Type) getParent()).getClassfication())
                && ((Type) getParent()).getName().equals(type);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        return isEnumType() ? output : super.writeTypeTo(output);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (!getConfiguration().fields().include(getVisibility())) return output;
        return super.writeTo(output);
    }

}
