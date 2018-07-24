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

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Link extends UMLPart {

    private final String target;

    public Link(UMLPart parent, String target) {
        super(parent);
        this.target = requireNonNull(target, "Link target is <null>.");
    }

    private Optional<Namespace> diagramPackage() {
        UMLRoot diagram = getRootUMLPart();
        if (diagram instanceof PackageUml) {
            return Optional.of(new Namespace(diagram, ((PackageUml) diagram).packageName));
        } else if (diagram instanceof ClassUml) {
            return Optional.of(((ClassUml) diagram).type.getNamespace());
        }
        return Optional.empty();
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        Optional<String> relativeNameToDiagram = diagramPackage()
                .filter(ns -> target.startsWith(ns.name + '.'))
                .map(ns -> target.substring(ns.name.length() + 1));
        if (relativeNameToDiagram.isPresent()) {
            output.append("[[").append(relativeNameToDiagram.get()).append("]]");
        } else {
            output.append("[[fqn:").append(target).append("]]");
        }
        return output;
    }

}
