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

import nl.talsmasoftware.umldoclet.uml.Namespace;
import nl.talsmasoftware.umldoclet.uml.Reference;
import nl.talsmasoftware.umldoclet.uml.Type;
import nl.talsmasoftware.umldoclet.uml.UMLDiagram;
import nl.talsmasoftware.umldoclet.rendering.CharSequenceRenderer;

import javax.lang.model.element.PackageElement;
import java.io.File;
import java.util.*;

/**
 * @author Sjoerd Talsma
 */
class PackageDiagram extends UMLDiagram {

    private final Namespace pkg;
    private File pumlFile = null;

    PackageDiagram(UMLFactory factory, PackageElement packageElement) {
        super(factory.config);
        Map<Namespace, Collection<Type>> foreignTypes = new LinkedHashMap<>();
        List<Reference> references = new ArrayList<>();
        pkg = factory.createPackage(packageElement, foreignTypes, references);
        children.add(pkg);

        foreignTypes.forEach((pkg, types) -> {
            children.add(CharSequenceRenderer.NEWLINE);
            types.forEach(type -> UMLFactory.addChild(pkg, type));
            children.add(pkg);
        });

        children.add(CharSequenceRenderer.NEWLINE);
        references.stream().map(Reference::canonical).forEach(children::add);
    }

    @Override
    protected File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(config.getDestinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(pkg.name.replace('.', '/'));
            result.append("/package.puml");
            pumlFile = ensureParentDir(new File(result.toString()));
        }
        return pumlFile;
    }

}
