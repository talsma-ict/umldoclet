/*
 * Copyright 2016-2026 Talsma ICT
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

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner9;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/// JavaDoc ElementScanner to detect dependencies.
///
/// The packages of the dependencies are remembered and the result of the scan is a set of [PackageDependency]
/// objects. Duplicate dependencies will be automatically removed because the result is a set.
///
/// @author Sjoerd Talsma
public class DependenciesElementScanner extends ElementScanner9<Set<PackageDependency>, String> {

    private final DocletEnvironment docEnv;
    private final Configuration config;

    private String moduleName = null;

        /// Constructor to create a new package dependencies scanner.
    ///
    /// The scanner is stateful, the set of package dependencies is collected in the (mutable) [#DEFAULT_VALUE] set.
    ///
    /// @param docEnv The doclet environment (required, non-null).
    /// This is needed to evalutate whether visited elements are included in the documentation.
    /// @param config The doclet configuration (required, non-null).
    public DependenciesElementScanner(DocletEnvironment docEnv, Configuration config) {
        super(new LinkedHashSet<>());
        this.docEnv = requireNonNull(docEnv, "Doclet environemnt is <null>");
        this.config = requireNonNull(config, "Configuration is <null>");
    }

        /// @return The modulename if found, otherwise `null`.
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public Set<PackageDependency> visitModule(ModuleElement visitedModule, String fromPackage) {
        moduleName = visitedModule.getQualifiedName().toString();
        return super.visitModule(visitedModule, fromPackage);
    }

        /// Visit a package to evalutate all dependencies from its elements to other packages.
    ///
    /// All elements within the package are visited, with the new `fromPackage` set to the qualified name of
    /// this visited package.
    ///
    /// @param visitedPackage The visited package.
    /// @param fromPackage    The 'from' package (possibly from parent elements).
    /// Ignored in this method, as the scan will continue from the visited package.
    /// @return The found package dependencies after scanning the visited package.
    @Override
    public Set<PackageDependency> visitPackage(PackageElement visitedPackage, String fromPackage) {
        boolean included = docEnv.isIncluded(visitedPackage);
        String packageName = visitedPackage.getQualifiedName().toString();
        if (!included) {
            config.logger().debug(Message.DEBUG_PACKAGE_VISITED_BUT_UNDOCUMENTED, packageName);
            return DEFAULT_VALUE;
        }
        return super.visitPackage(visitedPackage, packageName);
    }

        /// Visit a type element to add their package dependencies to the current set.
    ///
    /// First, the package of the superclass is added as a dependency.
    /// Then, the package of each implemented interface is added as a dependency.
    /// Finally, all contained elements within the type are visited for package dependencies.
    ///
    /// Please note, at the moment there is no metadata available in JavaDoc listing the <em>imports</em> of a type.
    /// So unfortunately the imports of a type are currently not included in the package dependencies.
    ///
    /// @param visitedType The visited type.
    /// @param fromPackage The current package (optional, will be resolved from the visited type if null).
    /// @return The found package dependencies after scanning the visited type.
    @Override
    public Set<PackageDependency> visitType(TypeElement visitedType, String fromPackage) {
        String pkg = fromPackage == null && docEnv.isIncluded(visitedType) ? PackageElementVisitor.INSTANCE.visit(visitedType) : fromPackage;
        addDependency(pkg, visitedType.getSuperclass());
        visitedType.getInterfaces().forEach(implemented -> addDependency(pkg, implemented));
        // TODO: figure out if there is a way to add the class' imports dependencies!
        return super.visitType(visitedType, pkg);
    }

        /// Visit a variable element (field, constant or method parameter) to add its package dependency to the current set.
    ///
    /// The package of the <em>type</em> of the variable is added as a package dependency.
    ///
    /// @param visitedVariable The visited variable.
    /// @param fromPackage     The current package.
    /// @return The found package dependencies after scanning the visited type.
    @Override
    public Set<PackageDependency> visitVariable(VariableElement visitedVariable, String fromPackage) {
        addDependency(fromPackage, visitedVariable.asType());
        return super.visitVariable(visitedVariable, fromPackage);
    }

        /// Visit an executable element (method, constructor or initializer) to add its package dependency to the current set.
    ///
    /// First, the package of the <em>return type</em> of the executable is added as a package dependency.
    /// Then, the packages of all thrown exception types are added as package dependencies.
    /// Finally, all child elements of the executable (e.g. parameters) are visited for package dependencies.
    ///
    /// @param visitedExecutable The visited executable.
    /// @param fromPackage       The current package.
    /// @return The found package dependencies after scanning the visited executable.
    @Override
    public Set<PackageDependency> visitExecutable(ExecutableElement visitedExecutable, String fromPackage) {
        addDependency(fromPackage, visitedExecutable.getReturnType());
        visitedExecutable.getThrownTypes().forEach(thrownType -> addDependency(fromPackage, thrownType));
        return super.visitExecutable(visitedExecutable, fromPackage); // will add the argument dependencies
    }

        /// Visit a type parameter element (a generic) to add its package dependency to the current set.
    ///
    /// First, the package of the <em>generic type</em> is added as a package dependency.
    /// Then, the packages of all declared bounds are added as package dependencies.
    ///
    /// @param visitedTypeParameter The visited parameter element.
    /// @param fromPackage          The current package.
    /// @return The found package dependencies after scanning the visited executable.
    @Override
    public Set<PackageDependency> visitTypeParameter(TypeParameterElement visitedTypeParameter, String fromPackage) {
        addDependency(fromPackage, visitedTypeParameter.getGenericElement());
        visitedTypeParameter.getBounds().forEach(bound -> addDependency(fromPackage, bound));
        return super.visitTypeParameter(visitedTypeParameter, fromPackage);
    }

        /// Overrides visiting any <em>unknown</em> element.
    ///
    /// The default visitor throws exception on unknown elements, this visitor just returns the current package
    /// dependencies (without adding any).
    ///
    /// @param visitedUnknown The visited unknown element.
    /// @param fromPackage    The current package (ignored, as unknown elements are not processed any further).
    /// @return The found package dependencies before the unknown element, without adding any.
    @Override
    public Set<PackageDependency> visitUnknown(Element visitedUnknown, String fromPackage) {
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
            DEFAULT_VALUE.add(new PackageDependency(fromPackage, toPackage));
        }
    }

}
