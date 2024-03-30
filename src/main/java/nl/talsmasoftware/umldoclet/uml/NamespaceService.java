package nl.talsmasoftware.umldoclet.uml;

/**
 * To break Cyclically-dependent Modularization between Namespace and TypeName and to
 * change bidirectional association to unidirectional association this class
 * is created with contains method.
 */
public class NamespaceService {
    private NamespaceService() {
    }

    public static boolean contains(Namespace namespace, TypeName typeName) {
        return typeName != null && typeName.qualified.startsWith(namespace.name + ".");
    }
}
