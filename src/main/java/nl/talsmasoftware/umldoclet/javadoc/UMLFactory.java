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
package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.Visibility;
import nl.talsmasoftware.umldoclet.uml.*;
import nl.talsmasoftware.umldoclet.uml.util.UmlPostProcessors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.ENUM;

/**
 * One big factory to produce UML from analyzed Javadoc elements.
 * <p>
 * TODO: This should be refactored into ClassDiagram and PackageDiagram visitor implementations.
 * This increases flexibility in supporting future language features however may introduce additional risk
 * with regard to unbounded recursion (see <a href="https://github.com/talsma-ict/umldoclet/issues/75">Issue 75</a>
 * for example).
 *
 * @author Sjoerd Talsma
 */


/**
 * This class was containing large number of methods and that was creating Insufficient Modularization
 * So now this is refactored into ClassDiagram and PackageDiagram visitor implementations.
 * For this UMLFactoryUtil, PackageDiagramVisitor, ClassDiagramVisitor class has been created and generateDiagram
 * method is now entry point to create diagram and this method was in UMLDoclet.
 */
public class UMLFactory {

    public UMLFactory(Configuration config, DocletEnvironment env) {
        UMLFactoryUtil.config =  requireNonNull(config, "Configuration is <null>.");
        UMLFactoryUtil.env = requireNonNull(env, "Doclet environment is <null>.");
        UMLFactoryUtil.typeNameWithCardinality = TypeNameWithCardinality.function(env.getTypeUtils());
    }

    public Diagram generateDiagram(Element element) {
        if (element instanceof PackageElement) {
            return new PackageDiagramVisitor().visit((PackageElement) element);
        } else if (element instanceof TypeElement && (element.getKind().isClass() || element.getKind().isInterface())) {
            return new ClassDiagramVisitor().visit((TypeElement) element);
        }
        return null;
    }
}
