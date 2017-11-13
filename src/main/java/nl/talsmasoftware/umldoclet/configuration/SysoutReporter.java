/*
 * Copyright 2016-2017 Talsma ICT
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
