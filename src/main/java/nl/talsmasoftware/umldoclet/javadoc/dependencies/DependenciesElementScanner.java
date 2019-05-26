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

public class DependenciesElementScanner extends ElementScanner9<Set<Dependency>, String> {

    public DependenciesElementScanner() {
        super(new LinkedHashSet<>());
    }

//    @Override
//    public Set<Dependency> visitModule(ModuleElement e, String fromPackage) {
//        return super.visitModule(e, fromPackage);
//    }

    @Override
    public Set<Dependency> visitPackage(PackageElement e, String fromPackage) {
        String visitedPackage = e.getQualifiedName().toString();
        return super.visitPackage(e, visitedPackage);
    }

    @Override
    public Set<Dependency> visitType(TypeElement e, String fromPackage) {
        addDependency(fromPackage, e.getSuperclass());
        e.getInterfaces().forEach(implemented -> addDependency(fromPackage, implemented));
        return super.visitType(e, fromPackage);
    }

    @Override
    public Set<Dependency> visitVariable(VariableElement e, String fromPackage) {
        addDependency(fromPackage, e.asType());
        return super.visitVariable(e, fromPackage);
    }

    @Override
    public Set<Dependency> visitExecutable(ExecutableElement e, String fromPackage) {
        addDependency(fromPackage, e.getReturnType());
        return super.visitExecutable(e, fromPackage); // will add the argument dependencies
    }

    @Override
    public Set<Dependency> visitTypeParameter(TypeParameterElement e, String fromPackage) {
        addDependency(fromPackage, e.getGenericElement());
        e.getBounds().forEach(bound -> addDependency(fromPackage, bound));
        return super.visitTypeParameter(e, fromPackage);
    }

    @Override
    public Set<Dependency> visitUnknown(Element e, String fromPackage) {
        return DEFAULT_VALUE;
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
            DEFAULT_VALUE.add(new Dependency(fromPackage, toPackage));
        }
    }

}
