/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering.indent;

import nl.talsmasoftware.umldoclet.rendering.UMLPart;

/**
 * Rendere interface that can make use of an {@link IndentingPrintWriter}
 * by virtue of default wrapping behaviour.
 *
 * @author Sjoerd Talsma
 */
public interface IndentingRenderer extends UMLPart {

    /**
     * Renders this object to the given indenting {@code output}.
     *
     * @param <IPW>  The subclass of indenting print writer being written to.
     * @param output The output to render this object to.
     * @return A reference to the output for method chaining purposes.
     */
    <IPW extends IndentingPrintWriter> IPW writeTo(IPW output);

    /**
     * {@inheritDoc}
     */
    @Override
    default <A extends Appendable> A writeTo(A output) {
        writeTo(output instanceof IndentingPrintWriter
                ? (IndentingPrintWriter) output
                : IndentingPrintWriter.wrap(output, null));
        return output;
    }

}
