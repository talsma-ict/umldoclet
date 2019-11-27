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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner9;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class DependenciesElementScanner extends ElementScanner9<Set<PackageDependency>, String> {

    private final DocletEnvironment docEnv;
    private final Configuration config;

    public DependenciesElementScanner(DocletEnvironment docEnv, Configuration config) {
        super(new LinkedHashSet<>());
        this.docEnv = docEnv;
        this.config = requireNonNull(config, "Configuration is <null>");
    }

    @Override
    public Set<PackageDependency> visitPackage(PackageElement packageElement, String fromPackage) {
        boolean included = docEnv.isIncluded(packageElement);
        String visitedPackage = packageElement.getQualifiedName().toString();
        if (!included) {
            config.logger().debug(Message.DEBUG_PACKAGE_VISITED_BUT_UNDOCUMENTED, visitedPackage);
            return DEFAULT_VALUE;
        }
        return super.visitPackage(packageElement, visitedPackage);
    }

    @Override
    public Set<PackageDependency> visitType(TypeElement typeElement, String fromPackage) {
        String pkg = fromPackage == null && docEnv.isIncluded(typeElement)
                ? PackageElementVisitor.INSTANCE.visit(typeElement) : fromPackage;
        addDependency(pkg, typeElement.getSuperclass());
        typeElement.getInterfaces().forEach(implemented -> addDependency(pkg, implemented));
        // TODO: figure out if there is a way to add the class' imports dependencies!
        return super.visitType(typeElement, pkg);
    }

    @Override
    public Set<PackageDependency> visitVariable(VariableElement variableElement, String fromPackage) {
        addDependency(fromPackage, variableElement.asType());
        return super.visitVariable(variableElement, fromPackage);
    }

    @Override
    public Set<PackageDependency> visitExecutable(ExecutableElement executableElement, String fromPackage) {
        addDependency(fromPackage, executableElement.getReturnType());
        return super.visitExecutable(executableElement, fromPackage); // will add the argument dependencies
    }

    @Override
    public Set<PackageDependency> visitTypeParameter(TypeParameterElement typeParameterElement, String fromPackage) {
        addDependency(fromPackage, typeParameterElement.getGenericElement());
        typeParameterElement.getBounds().forEach(bound -> addDependency(fromPackage, bound));
        return super.visitTypeParameter(typeParameterElement, fromPackage);
    }

    @Override
    public Set<PackageDependency> visitUnknown(Element element, String fromPackage) {
        return scan(element.getEnclosedElements(), fromPackage);
    }

    private void addDependency(String fromPackage, TypeMirror toType) {
        String toPackage = PackageTypeVisitor.INSTANCE.visit(toType);
        addDependency(fromPackage, toPackage);
    }

    private void addDependency(String fromPackage, Element toElement) {
        String toPackage = PackageElementVisitor.INSTANCE.visit(toElement);
        addDependency(fromPackage, toPackage);
    }

    private void addDependency(String fromPackage, String toPackage) {
        if (fromPackage != null && toPackage != null && !fromPackage.equals(toPackage)) {
            DEFAULT_VALUE.add(new PackageDependency(fromPackage, toPackage));
        }
    }

}
