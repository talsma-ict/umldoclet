/*
 * Copyright 2016-2021 Talsma ICT
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

import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.logging.Message;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Doclet reporter using a specific {@link Locale} to reporte to a delegate {@link Reporter}.
 *
 * @author Sjoerd Talsma
 */
final class LocalizedReporter implements Reporter, Logger {
    private final DocletConfig config;
    private final Reporter delegate;
    private final Locale locale;

    LocalizedReporter(DocletConfig config, Reporter delegate, Locale locale) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.delegate = delegate;
        this.locale = locale;
    }

    @Override
    public void debug(Object key, Object... args) {
        log(Diagnostic.Kind.OTHER, key, args);
    }

    @Override
    public void info(Message key, Object... args) {
        log(Diagnostic.Kind.NOTE, key, args);
    }

    @Override
    public void warn(Message key, Object... args) {
        log(Diagnostic.Kind.WARNING, key, args);
    }

    @Override
    public void error(Message key, Object... args) {
        log(Diagnostic.Kind.ERROR, key, args);
    }

    private void log(Diagnostic.Kind kind, Object key, Object... args) {
        if (mustPrint(kind)) {
            doPrint(kind, key instanceof Message ? localize((Message) key, args)
                    : MessageFormat.format(Objects.toString(key), localizeArgs(args)));
        }
    }

    @Override
    public String localize(Message key, Object... args) {
        String message = key.toString(locale);
        if (args.length > 0) message = MessageFormat.format(message, localizeArgs(args));
        return message;
    }

    private Object[] localizeArgs(Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Message) args[i] = ((Message) args[i]).toString(locale);
        }
        return args;
    }

    private boolean mustPrint(Diagnostic.Kind kind) {
        Diagnostic.Kind threshold = config.quiet ? Diagnostic.Kind.WARNING
                : config.verbose ? Diagnostic.Kind.OTHER
                : Diagnostic.Kind.NOTE;
        return kind != null && kind.compareTo(threshold) <= 0;
    }

    @Override
    public void print(Diagnostic.Kind kind, String msg) {
        if (mustPrint(kind)) doPrint(kind, msg);
    }

    @Override
    public void print(Diagnostic.Kind kind, DocTreePath path, String msg) {
        if (mustPrint(kind)) doPrint(kind, path, msg);
    }

    @Override
    public void print(Diagnostic.Kind kind, Element elem, String msg) {
        if (mustPrint(kind)) doPrint(kind, elem, msg);
    }

    private void doPrint(Diagnostic.Kind kind, String msg) {
        if (delegate == null) System.out.println(msg);
        else delegate.print(kind, msg);
    }

    private void doPrint(Diagnostic.Kind kind, DocTreePath path, String msg) {
        if (delegate == null) System.out.println(msg);
        else delegate.print(kind, path, msg);
    }

    private void doPrint(Diagnostic.Kind kind, Element elem, String msg) {
        if (delegate == null) System.out.println(msg);
        else delegate.print(kind, elem, msg);
    }
}
