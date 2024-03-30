package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.Diagram;

import javax.lang.model.element.TypeElement;

public interface IClassDiagramVisitor {
    Diagram visit(TypeElement classElement);
}
