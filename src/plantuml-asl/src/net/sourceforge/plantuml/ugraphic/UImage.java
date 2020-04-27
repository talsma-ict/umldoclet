/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import net.sourceforge.plantuml.ugraphic.color.ColorChangerMonochrome;

public class UImage implements UShape {

	private final BufferedImage image;
	private final String formula;
	private final String rawFileName;

	public String getRawFileName() {
		return rawFileName;
	}

	public UImage(BufferedImage image) {
		this(null, image, null);
	}

	public UImage(String rawFileName, BufferedImage image) {
		this(rawFileName, image, null);
	}

	public UImage(String rawFileName, BufferedImage image, String formula) {
		this.image = image;
		this.formula = formula;
		this.rawFileName = rawFileName;
	}

	public UImage scale(double scale) {
		return scale(scale, AffineTransformOp.TYPE_BILINEAR);
	}

	public UImage scaleNearestNeighbor(double scale) {
		return scale(scale, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	}

	private UImage scale(double scale, final int type) {
		if (scale == 1) {
			return this;
		}
		final int w = (int) Math.round(image.getWidth() * scale);
		final int h = (int) Math.round(image.getHeight() * scale);
		final BufferedImage after = new BufferedImage(w, h, image.getType());
		final AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		final AffineTransformOp scaleOp = new AffineTransformOp(at, type);
		return new UImage(rawFileName, scaleOp.filter(image, after), formula);
	}

	public final BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return image.getWidth() - 1;
	}

	public int getHeight() {
		return image.getHeight() - 1;
	}

	public final String getFormula() {
		return formula;
	}

	public UImage muteColor(Color newColor) {
		if (newColor == null) {
			return this;
		}
		int darkerRgb = getDarkerRgb();
		final BufferedImage copy = deepCopy2();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				final int color = image.getRGB(i, j);
				final int rgb = getRgb(color);
				final int a = getA(color);
				if (a != 0 && rgb == darkerRgb) {
					copy.setRGB(i, j, newColor.getRGB() + a);
				}
			}
		}
		return new UImage(rawFileName, copy, formula);
	}

	public UImage muteTransparentColor(Color newColor) {
		if (newColor == null) {
			newColor = Color.WHITE;
		}
		final BufferedImage copy = deepCopy2();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				final int color = image.getRGB(i, j);
				// final int rgb = getRgb(color);
				final int a = getA(color);
				if (a == 0) {
					copy.setRGB(i, j, newColor.getRGB());
				}
			}
		}
		return new UImage(rawFileName, copy, formula);
	}

	private int getDarkerRgb() {
		int darkerRgb = -1;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				final int color = image.getRGB(i, j);
				// System.err.println("i="+i+" j="+j+" "+Integer.toHexString(color)+"
				// "+isTransparent(color));
				final int rgb = getRgb(color);
				final int a = getA(color);
				if (a != mask_a__) {
					continue;
				}
				// if (isTransparent(color)) {
				// continue;
				// }
				final int grey = ColorChangerMonochrome.getGrayScale(rgb);
				if (darkerRgb == -1 || grey < ColorChangerMonochrome.getGrayScale(darkerRgb)) {
					darkerRgb = rgb;
				}
			}
		}
		return darkerRgb;
	}

	private static final int mask_a__ = 0xFF000000;
	private static final int mask_rgb = 0x00FFFFFF;

	private int getRgb(int color) {
		return color & mask_rgb;
	}

	private int getA(int color) {
		return color & mask_a__;
	}

	// private boolean isTransparent(int argb) {
	// if ((argb & mask) == mask) {
	// return false;
	// }
	// return true;
	// }

	// From
	// https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	private static BufferedImage deepCopyOld(BufferedImage bi) {
		final ColorModel cm = bi.getColorModel();
		final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		final WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	private BufferedImage deepCopy2() {
		final BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < this.image.getWidth(); i++) {
			for (int j = 0; j < this.image.getHeight(); j++) {
				result.setRGB(i, j, image.getRGB(i, j));
			}
		}
		return result;
	}

}
