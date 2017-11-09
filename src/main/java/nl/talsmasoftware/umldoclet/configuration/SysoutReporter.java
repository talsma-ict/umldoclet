package nl.talsmasoftware.umldoclet.configuration;

import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Locale;

/**
 * Reporter that will have to do until {@link Configuration#init(Locale, Reporter) Configuration.init} has been called.
 *
 * @author Sjoerd Talsma
 */
final class SysoutReporter implements Reporter {
    Diagnostic.Kind threshold = Diagnostic.Kind.WARNING;

    private boolean mustPrint(Diagnostic.Kind kind) {
        return threshold == null || (kind != null && kind.compareTo(threshold) <= 0);
    }

    @Override
    public void print(Diagnostic.Kind kind, String msg) {
        if (mustPrint(kind)) System.out.println(msg);
    }

    @Override
    public void print(Diagnostic.Kind kind, DocTreePath path, String msg) {
        print(kind, msg);
    }

    @Override
    public void print(Diagnostic.Kind kind, Element e, String msg) {
        print(kind, msg);
    }

}
