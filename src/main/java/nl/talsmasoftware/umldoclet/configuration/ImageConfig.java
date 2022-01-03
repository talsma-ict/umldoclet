/*
 * Copyright 2016-2022 Talsma ICT
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

import java.util.Collection;
import java.util.Optional;

/**
 * Configuration relating to the images that are generated.
 *
 * @author Sjoerd Talsma
 */
public interface ImageConfig {
    /**
     * Supported image formats.
     */
    enum Format {
        /**
         * Value to indicate UML diagrams must be rendered as SVG
         * (<a href="https://wikipedia.org/wiki/Scalable_Vector_Graphics">scalable vector graphics</a>) images.
         * <p>
         * This format is the default and recommended format, as they have the following advantages over the other
         * formats:
         * <ul>
         * <li>Size. UML diagrams can be represented as compact SVG images.
         * <li>Scalability. SVG images can be scaled almost without limits.
         * <li>Links. SVG images allow us to embed links in the UML diagrams.
         * </ul>
         */
        SVG,

        /**
         * Value to indicate UML diagrams must be rendered as PNG
         * (<a href="https://wikipedia.org/wiki/Portable_Network_Graphics">Portable Network Graphics</a>) images.
         * <p>
         * This is a raster-graphics format that supports lossless compression. This format is usually only used if
         * {@link #SVG} is not an option.
         */
        PNG,

        /**
         * Value to indicate UML diagrams must be rendered as EPS
         * (<a href="https://wikipedia.org/wiki/Encapsulated_PostScript">Encapsulated Postscript</a>) images.
         * <p>
         * This format is most appropriate for print.
         */
        EPS
    }

    /**
     * @return The image directory for the UML diagrams, if explicitly specified.
     */
    Optional<String> directory();

    /**
     * By default {@code SVG} images will be generated because they are a lot smaller than for instance {@code PNG}
     * images and they can include links to Javadoc {@code HTML} pages.
     * <p>
     * Providing one or more {@code "-umlImageFormat"} option values overrides this setting.
     *
     * @return The image formats that are generated (by default only {@code SVG}).
     */
    Collection<Format> formats();

}
