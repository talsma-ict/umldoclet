package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.Diagram;

import javax.lang.model.element.PackageElement;

public interface IPackageDiagramVisitor {
    Diagram visit(PackageElement packageElement);
}
