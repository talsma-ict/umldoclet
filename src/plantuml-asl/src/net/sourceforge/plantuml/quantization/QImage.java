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

import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.klimt.color.ColorMapper;

/**
 * An immutable grid of pixel colors.
 */
public final class QImage {
	/**
	 * The first index corresponds to the row, while the second index corresponds
	 * the column.
	 */
	private final QColor[][] colors;

	private QImage(QColor[][] colors) {
		this.colors = colors;
	}

	public static QImage fromBufferedImage(ColorMapper mapper, BufferedImage img) {
		final int height = img.getHeight();
		final int width = img.getWidth();
		final QColor[][] colors = new QColor[height][width];

		if (img.getType() == BufferedImage.TYPE_INT_ARGB) {
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					colors[y][x] = QColor.fromArgbInt(mapper, img.getRGB(x, y));
		} else if (img.getType() == BufferedImage.TYPE_INT_RGB) {
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					colors[y][x] = QColor.fromRgbInt(img.getRGB(x, y));
		} else {
			throw new IllegalArgumentException();
		}

		return new QImage(colors);
	}

	public static QImage fromColors(QColor[][] colors) {
		return new QImage(colors);
	}

	public QColor getColor(int x, int y) {
		return colors[y][x];
	}

	public QColor getColor(int index) {
		return colors[index / getWidth()][index % getWidth()];
	}

	Multiset<QColor> getColors() {
		final Multiset<QColor> colorCounts = new HashMultiset<>();
		for (int i = 0; i < getNumPixels(); ++i) {
			final QColor color = getColor(i);
			colorCounts.add(color);
		}
		return colorCounts;
	}

	public int getWidth() {
		return colors[0].length;
	}

	public int getHeight() {
		return colors.length;
	}

	public int getNumPixels() {
		return getWidth() * getHeight();
	}

	public BufferedImage toBufferedImage() {
		final BufferedImage result = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < result.getWidth(); i++)
			for (int j = 0; j < result.getHeight(); j++)
				result.setRGB(i, j, colors[j][i].getRgbInt());
		return result;
	}

	public BufferedImage toBufferedImageKeepTransparency(BufferedImage orig) {
		final BufferedImage result = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < result.getWidth(); i++)
			for (int j = 0; j < result.getHeight(); j++) {
				if ((orig.getRGB(i, j)) != 0x00000000)
					result.setRGB(i, j, colors[j][i].getRgbInt() | 0xFF000000);
			}
		return result;
	}

}
