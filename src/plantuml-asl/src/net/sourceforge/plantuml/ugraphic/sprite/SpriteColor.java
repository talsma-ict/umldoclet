/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.ugraphic.sprite;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;

public class SpriteColor implements Sprite {

	private final int width;
	private final int height;
	private final int grey[][];
	private final int color[][];

	public SpriteColor(int width, int height) {
		this.width = width;
		this.height = height;
		this.grey = new int[height][width];
		this.color = new int[height][width];
	}

	public void setGrey(int x, int y, int level) {
		if (x < 0 || x >= width) {
			return;
		}
		if (y < 0 || y >= height) {
			return;
		}
		if (level < 0 || level >= 16) {
			throw new IllegalArgumentException();
		}
		grey[y][x] = level;
		color[y][x] = -1;
	}

	public void setColor(int x, int y, int col) {
		if (x < 0 || x >= width) {
			return;
		}
		if (y < 0 || y >= height) {
			return;
		}
		grey[y][x] = -1;
		color[y][x] = col;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public UImage toUImage(ColorMapper colorMapper, HtmlColor backcolor, HtmlColor forecolor) {
		final BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		if (backcolor == null) {
			backcolor = HtmlColorUtils.WHITE;
		}
		if (forecolor == null) {
			forecolor = HtmlColorUtils.BLACK;
		}
		final HtmlColorGradient gradient = new HtmlColorGradient(backcolor, forecolor, '\0');
		for (int col = 0; col < width; col++) {
			for (int line = 0; line < height; line++) {
				final int localColor = color[line][col];
				if (localColor == -1) {
					final double coef = 1.0 * grey[line][col] / (16 - 1);
					final Color c = gradient.getColor(colorMapper, coef);
					im.setRGB(col, line, c.getRGB());
				} else {
					im.setRGB(col, line, localColor);
				}
			}
		}
		return new UImage(im);
	}

	public TextBlock asTextBlock(final HtmlColor color, final double scale) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final UImage image = toUImage(ug.getColorMapper(), ug.getParam().getBackcolor(), color);
				ug.draw(image.scale(scale));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(getWidth() * scale, getHeight() * scale);
			}
		};
	}

}
