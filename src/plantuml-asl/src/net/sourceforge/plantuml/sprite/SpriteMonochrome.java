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
package net.sourceforge.plantuml.sprite;

import java.awt.Color;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class SpriteMonochrome implements Sprite {

	private final int width;
	private final int height;
	private final int grayLevel;
	private final int gray[][];

	public boolean isSameKind(SpriteMonochrome other) {
		if (this.width != other.width)
			return false;

		if (this.height != other.height)
			return false;

		if (this.grayLevel != other.grayLevel)
			return false;

		return true;
	}

	public boolean isSame(SpriteMonochrome other) {
		if (isSameKind(other) == false)
			return false;

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if (this.gray[j][i] != other.gray[j][i])
					return false;

		return true;
	}

	public SpriteMonochrome xor(SpriteMonochrome other) {
		if (this.width != other.width)
			throw new IllegalStateException();

		if (this.height != other.height)
			throw new IllegalStateException();

		if (this.grayLevel != other.grayLevel)
			throw new IllegalStateException();

		final SpriteMonochrome result = new SpriteMonochrome(width, height, grayLevel);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				result.gray[j][i] = this.gray[j][i] ^ other.gray[j][i];

		return result;
	}

	public SpriteMonochrome(int width, int height, int grayLevel) {
		if (grayLevel != 2 && grayLevel != 4 && grayLevel != 8 && grayLevel != 16)
			throw new IllegalArgumentException();

		this.width = width;
		this.height = height;
		this.grayLevel = grayLevel;
		this.gray = new int[height][width];
	}

	public SpriteMonochrome xSymetric() {
		final SpriteMonochrome result = new SpriteMonochrome(width, height, grayLevel);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				result.setGray(i, j, this.getGray(i, j));

		for (int j = 0; j < height; j++)
			for (int i = 0; i < width / 2; i++) {
				final int i2 = width - 1 - i;
				final int level = result.getGray(i, j) ^ result.getGray(i2, j);
				result.setGray(i2, j, level);
			}

		return result;
	}

	public SpriteMonochrome ySymetric() {
		final SpriteMonochrome result = new SpriteMonochrome(width, height, grayLevel);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				result.setGray(i, j, this.getGray(i, j));

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height / 2; j++) {
				final int j2 = height - 1 - j;
				final int level = result.getGray(i, j) ^ result.getGray(i, j2);
				result.setGray(i, j2, level);
			}

		return result;
	}

	public void setGray(int x, int y, int level) {
		if (x < 0 || x >= width)
			return;

		if (y < 0 || y >= height)
			return;

		if (level < 0 || level >= grayLevel)
			throw new IllegalArgumentException("level=" + level + " grayLevel=" + grayLevel);

		gray[y][x] = level;
	}

	public int getGray(int x, int y) {
		if (x >= width)
			throw new IllegalArgumentException("x=" + x + " width=" + width);

		if (y >= height)
			throw new IllegalArgumentException("y=" + y + " height=" + height);

		return gray[y][x];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public UImage toUImage(ColorMapper colorMapper, HColor backcolor, HColor color) {

		if (backcolor == null || HColorUtils.isTransparent(backcolor))
			backcolor = HColorUtils.WHITE.withDark(HColorUtils.BLACK);

		if (color == null || HColorUtils.isTransparent(color))
			color = HColorUtils.BLACK.withDark(HColorUtils.WHITE);

		final BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final HColorGradient gradient = new HColorGradient(backcolor, color, '\0');
		for (int col = 0; col < width; col++) {
			for (int line = 0; line < height; line++) {
				final double coef = 1.0 * gray[line][col] / (grayLevel - 1);
				final Color c = gradient.getColor(colorMapper, coef);
				im.setRGB(col, line, c.getRGB());
			}
		}
		return new UImage(new PixelImage(im, AffineTransformType.TYPE_BILINEAR));
	}

	private UImage special(ColorMapper colorMapper, HColorGradient backcolor, HColor color) {
		final BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < width; col++) {
			for (int line = 0; line < height; line++) {
				final HColor backColorLocal = new HColorSimple(backcolor.getColor(colorMapper, 1.0 * line / height),
						false);
				final HColorGradient gradient = new HColorGradient(backColorLocal, color, '\0');
				final double coef = 1.0 * gray[line][col] / (grayLevel - 1);
				final Color c = gradient.getColor(colorMapper, coef);
				im.setRGB(col, line, c.getRGB());
			}
		}
		return new UImage(new PixelImage(im, AffineTransformType.TYPE_BILINEAR));
	}

	public TextBlock asTextBlock(final HColor color, final double scale, ColorMapper colorMapper) {
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

	public void exportSprite1(OutputStream fos) throws IOException {
		for (int y = 0; y < this.getHeight(); y += 2) {
			for (int x = 0; x < this.getWidth(); x += 1) {
				int b1 = this.getGray(x, y);
				int b2 = y + 1 < this.getHeight() ? this.getGray(x, y + 1) : b1;
				fos.write(b1 * 16 + b2);
			}
		}
	}

}
