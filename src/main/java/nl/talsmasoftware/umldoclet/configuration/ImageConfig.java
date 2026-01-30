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
package nl.talsmasoftware.umldoclet.configuration;

import java.util.Collection;
import java.util.Optional;

/// Configuration for generated UML Diagram images.
///
/// @author Sjoerd Talsma
public interface ImageConfig {
    /// Supported image formats.
    enum Format {
        /// Render UML diagrams as SVG
        /// ([scalable vector graphics](https://wikipedia.org/wiki/Scalable_Vector_Graphics)) images.
        ///
        /// This format is the default and recommended format, as they have the following advantages over the other
        /// formats:
        ///
        /// - Size: UML diagrams can be represented as compact SVG images.
        /// - Scalability: SVG images can be scaled almost without limits.
        /// - Links: SVG images allow us to embed links in the UML diagrams.
        SVG(".svg"),

        /// Generate the same diagram as the [#SVG] option, but embed it as plain `<img>` tags
        /// in the Javadoc HTML instead of SVG objects.
        ///
        /// This allows loading the documentation in tools like Microsoft Word and keeping the images,
        /// where embedded SVG objects are problematic.
        ///
        /// It is recommended as a fallback option if the default [#SVG] option is giving you issues.
        /// Links *will* be embedded inside the SVG images, but will *not* be clickable
        /// in most browsers when included as image tag.
        SVG_IMG(".svg"),

        /// Render UML diagrams as PNG
        /// ([Portable Network Graphics](https://wikipedia.org/wiki/Portable_Network_Graphics)) images.
        ///
        /// This is a raster-graphics format that supports lossless compression. This format is usually only used if
        /// [#SVG] is not an option. Raster images do not automatically scale and their file size is substantially
        /// larger than [#SVG].
        PNG(".png"),

        /// Render UML diagrams as EPS
        /// ([Encapsulated Postscript](https://wikipedia.org/wiki/Encapsulated_PostScript)) images.
        ///
        /// This format is most appropriate for print.
        EPS(".eps");

        /// The file extension for images of this format.
        public final String fileExtension;

        Format(String fileExtension) {
            this.fileExtension = fileExtension;
        }
    }

    /// A separate directory where all UML diagram images are generated, if explicitly specified.
    ///
    /// If not configured (i.e. [Optional#empty()]), the image will be generated within the same directory als
    /// the corresponding javadoc HTML.
    ///
    /// @return The image directory for UML diagrams, if explicitly specified.
    Optional<String> directory();

    /// Image format(s) to generate UML diagrams in.
    ///
    /// By default `SVG` images will be generated because they are a lot smaller than for instance `PNG`
    /// images and they can include links to Javadoc `HTML` pages.
    ///
    /// Providing one or more `"-umlImageFormat"` option values overrides this setting.
    ///
    /// @return Image formats that will be generated (by default only `SVG`).
    Collection<Format> formats();

}
