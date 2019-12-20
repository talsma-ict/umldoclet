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
package nl.talsmasoftware.umldoclet;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;

public class UMLDoclet extends HtmlDoclet {

    public static LanguageVersion languageVersion() {
        return HtmlDoclet.languageVersion();
    }

    public static int optionLength(String var0) {
        return HtmlDoclet.optionLength(var0);
    }

    public static boolean validOptions(String[][] var0, DocErrorReporter var1) {
        return HtmlDoclet.validOptions(var0, var1);
    }

    public static boolean start(RootDoc var0) {
        System.out.println(">>> UMLDoclet started!");
        return HtmlDoclet.start(var0);
    }

}
