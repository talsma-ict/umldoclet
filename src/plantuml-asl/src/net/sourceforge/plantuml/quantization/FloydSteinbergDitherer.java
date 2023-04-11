/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import java.util.Set;

public final class FloydSteinbergDitherer implements Ditherer {
	public static final FloydSteinbergDitherer INSTANCE = new FloydSteinbergDitherer();

	private static final ErrorComponent[] ERROR_DISTRIBUTION = { new ErrorComponent(1, 0, 7.0 / 16.0),
			new ErrorComponent(-1, 1, 3.0 / 16.0), new ErrorComponent(0, 1, 5.0 / 16.0),
			new ErrorComponent(1, 1, 1.0 / 16.0) };

	private FloydSteinbergDitherer() {
	}

	@Override
	public QImage dither(QImage image, Set<QColor> newColors) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		QColor[][] colors = new QColor[height][width];
		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x)
				colors[y][x] = image.getColor(x, y);

		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x) {
				final QColor originalColor = colors[y][x];
				final QColor replacementColor = originalColor.getNearestColor(newColors);
				colors[y][x] = replacementColor;
				final QColor error = originalColor.minus(replacementColor);

				for (ErrorComponent component : ERROR_DISTRIBUTION) {
					int siblingX = x + component.deltaX, siblingY = y + component.deltaY;
					if (siblingX >= 0 && siblingY >= 0 && siblingX < width && siblingY < height) {
						QColor errorComponent = error.scaled(component.errorFraction);
						colors[siblingY][siblingX] = colors[siblingY][siblingX].plus(errorComponent);
					}
				}
			}

		return QImage.fromColors(colors);
	}

	private static final class ErrorComponent {
		final int deltaX, deltaY;
		final double errorFraction;

		ErrorComponent(int deltaX, int deltaY, double errorFraction) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			this.errorFraction = errorFraction;
		}
	}
}
