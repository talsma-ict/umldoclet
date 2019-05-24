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

import nl.talsmasoftware.umldoclet.uml.Namespace;
import nl.talsmasoftware.umldoclet.uml.Reference;

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

import static nl.talsmasoftware.umldoclet.uml.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.uml.Reference.Side.to;

public class PackageDependenciesElementScanner extends ElementScanner9<Set<Reference>, Namespace> {

    public PackageDependenciesElementScanner() {
        super(new LinkedHashSet<>());
    }

//    @Override
//    public Set<Reference> visitModule(ModuleElement e, Namespace fromPackage) {
//        return super.visitModule(e, fromPackage);
//    }

    @Override
    public Set<Reference> visitPackage(PackageElement e, Namespace fromPackage) {
        Namespace visitedPackage = new Namespace(fromPackage, e.getQualifiedName().toString());
        return super.visitPackage(e, visitedPackage);
    }

    @Override
    public Set<Reference> visitType(TypeElement e, Namespace fromPackage) {
        Set<Reference> dependencies = DEFAULT_VALUE;
        addIfNonNull(dependencies, packageDependency(e.getSuperclass(), fromPackage));
        e.getInterfaces().stream()
                .map(implemented -> packageDependency(implemented, fromPackage))
                .forEach(dependency -> addIfNonNull(dependencies, dependency));
        // dependencies.addAll(super.visitType(e, fromPackage)); // Shared single-set optimization
        super.visitType(e, fromPackage);
        return dependencies;
    }

    @Override
    public Set<Reference> visitVariable(VariableElement e, Namespace fromPackage) {
        Set<Reference> dependencies = DEFAULT_VALUE;
        TypeMirror variableType = e.asType();
        addIfNonNull(dependencies, packageDependency(variableType, fromPackage));
        // dependencies.addAll(super.visitVariable(e, fromPackage)); // Shared single-set optimization;
        super.visitVariable(e, fromPackage);
        return dependencies;
    }

    @Override
    public Set<Reference> visitExecutable(ExecutableElement e, Namespace fromPackage) {
        Set<Reference> dependencies = DEFAULT_VALUE;
        addIfNonNull(dependencies, packageDependency(e.getReturnType(), fromPackage));
        // dependencies.addAll(super.visitExecutable(e, fromPackage)); // Shared single-set optimization;
        super.visitExecutable(e, fromPackage);
        return dependencies;
    }

    @Override
    public Set<Reference> visitTypeParameter(TypeParameterElement e, Namespace fromPackage) {
        Set<Reference> dependencies = DEFAULT_VALUE;
        addIfNonNull(dependencies, packageDependency(e.getGenericElement(), fromPackage));
        e.getBounds().stream()
                .map(bound -> packageDependency(bound, fromPackage))
                .forEach(dependency -> addIfNonNull(dependencies, dependency));
        // dependencies.addAll(super.visitTypeParameter(e, fromPackage));
        super.visitTypeParameter(e, fromPackage);
        return dependencies;
    }

    @Override
    public Set<Reference> visitUnknown(Element e, Namespace fromPackage) {
        return DEFAULT_VALUE;
    }

    private static void addIfNonNull(Set<Reference> set, Reference item) {
        if (item != null) set.add(item);
    }

    private Reference packageDependency(TypeMirror toType, Namespace fromPackage) {
        Namespace toPackage = NamespaceTypeVisitor.INSTANCE.visit(toType);
        return packageDependency(toPackage, fromPackage);
    }

    private Reference packageDependency(Element toElement, Namespace fromPackage) {
        Namespace toPackage = NamespaceElementVisitor.INSTANCE.visit(toElement);
        return packageDependency(toPackage, fromPackage);
    }

    private static Reference packageDependency(Namespace toPackage, Namespace fromPackage) {
        if (toPackage != null && fromPackage != null && !toPackage.equals(fromPackage)) {
            final String toName = toPackage.name.isEmpty() ? "unnamed" : toPackage.name;
            final String fromName = fromPackage.name.isEmpty() ? "unnamed" : fromPackage.name;
            return new Reference(from(fromName, null), "..>", to(toName, null));
        }
        return null;
    }

}
