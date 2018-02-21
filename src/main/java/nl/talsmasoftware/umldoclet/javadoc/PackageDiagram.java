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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.*;

import javax.lang.model.element.PackageElement;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Sjoerd Talsma
 */
class PackageDiagram extends UMLDiagram {

    //    private final Namespace pkg;
    private final String packageName;
    private File pumlFile = null;

    PackageDiagram(UMLFactory factory, PackageElement packageElement) {
        super(factory.config);
        factory.diagram.set(this);
        Map<Namespace, Collection<Type>> foreignTypes = new LinkedHashMap<>();
        List<Reference> references = new ArrayList<>();
        packageName = packageElement.getQualifiedName().toString();
        children.add(factory.createPackage(this, packageElement, foreignTypes, references));

        // TODO: Should we filter "java.lang" or "java.util" references that occur >= 3 times?
        // Maybe somehow make this configurable as well.
        foreignTypes.entrySet().stream()
                .filter(entry -> "java.lang".equals(entry.getKey().name) || "java.util".equals(entry.getKey().name))
                .map(Map.Entry::getValue)
                .forEach(types -> {
                    for (Iterator<Type> it = types.iterator(); it.hasNext(); ) {
                        Type type = it.next();
                        if (references.stream().filter(ref -> ref.contains(type.name)).limit(3).count() > 2) {
                            references.removeIf(ref -> ref.contains(type.name));
                            it.remove();
                        }
                    }
                });

        foreignTypes.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> {
                    Namespace foreignPackage = entry.getKey();
                    entry.getValue().forEach(type -> UMLFactory.addChild(foreignPackage, type));
                    return foreignPackage;
                })
                .flatMap(foreignPackage -> Stream.of(UMLPart.NEWLINE, foreignPackage))
                .forEach(children::add);

        children.add(UMLPart.NEWLINE);
        references.stream().map(Reference::canonical).forEach(children::add);
    }

    @Override
    protected File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(getConfiguration().getDestinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(packageName.replace('.', '/'));
            result.append("/package.puml");
            pumlFile = ensureParentDir(new File(result.toString()));
        }
        return pumlFile;
    }

}
