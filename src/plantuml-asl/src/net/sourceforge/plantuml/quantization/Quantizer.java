/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.quantization;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

public final class Quantizer {
	private static final int MAX_COLOR_COUNT = 256;

	private static QImage quantizeNow(QImage image) throws IOException {

		Multiset<QColor> originalColors = image.getColors();
		Set<QColor> distinctColors = originalColors.getDistinctElements();
		if (distinctColors.size() > MAX_COLOR_COUNT) {
			// distinctColors = KMeansQuantizer.INSTANCE.quantize(originalColors,
			// MAX_COLOR_COUNT);
			distinctColors = MedianCutQuantizer.INSTANCE.quantize(originalColors, MAX_COLOR_COUNT);
			image = FloydSteinbergDitherer.INSTANCE.dither(image, distinctColors);
		}
		return image;
	}

	public static BufferedImage quantizeNow(ColorMapper mapper, BufferedImage orig) throws IOException {
		final QImage raw = QImage.fromBufferedImage(mapper, orig);
		final QImage result = quantizeNow(raw);

		if (orig.getType() == BufferedImage.TYPE_INT_RGB)
			return result.toBufferedImage();
		else if (orig.getType() == BufferedImage.TYPE_INT_ARGB)
			return result.toBufferedImageKeepTransparency(orig);
		else
			throw new IllegalArgumentException();

	}
}
