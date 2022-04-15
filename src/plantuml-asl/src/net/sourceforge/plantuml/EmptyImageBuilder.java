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
package net.sourceforge.plantuml;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;

public class EmptyImageBuilder {

	private final BufferedImage im;
	private final Graphics2D g2d;
	private final Color background;
	private final StringBounder stringBounder;

	private static EmptyImageBuilder create(String watermark, int width, int height, Color background,
			StringBounder stringBounder, double dpiFactor) {
		EmptyImageBuilder result = new EmptyImageBuilder(watermark, (int) (width * dpiFactor),
				(int) (height * dpiFactor), background, stringBounder);
		if (dpiFactor != 1.0)
			result.g2d.setTransform(AffineTransform.getScaleInstance(dpiFactor, dpiFactor));
		return result;
	}

	public EmptyImageBuilder(String watermark, int width, int height, Color background, StringBounder stringBounder) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("width and height must be positive");

		if (width > GraphvizUtils.getenvImageLimit()) {
			Log.info("Width too large " + width + ". You should set PLANTUML_LIMIT_SIZE");
			width = GraphvizUtils.getenvImageLimit();
		}
		if (height > GraphvizUtils.getenvImageLimit()) {
			Log.info("Height too large " + height + ". You should set PLANTUML_LIMIT_SIZE");
			height = GraphvizUtils.getenvImageLimit();
		}
		this.background = background;
		this.stringBounder = stringBounder;
		Log.info("Creating image " + width + "x" + height);
		im = new BufferedImage(width, height, getType(background));
		g2d = im.createGraphics();
		UAntiAliasing.ANTI_ALIASING_ON.apply(g2d);
		if (background != null) {
			g2d.setColor(background);
			g2d.fillRect(0, 0, width, height);
		}
		if (watermark != null) {
			final int gray = 200;
			g2d.setColor(new Color(gray, gray, gray));
			printWatermark(watermark, width, height);
		}
	}

	private int getType(Color background) {
		if (background == null) {
			return BufferedImage.TYPE_INT_ARGB;
		}
		if (background.getAlpha() != 255) {
			return BufferedImage.TYPE_INT_ARGB;
		}
		return BufferedImage.TYPE_INT_RGB;
	}

	private void printWatermark(String watermark, int maxWidth, int maxHeight) {
		final Font javaFont = g2d.getFont();
		final FontMetrics fm = g2d.getFontMetrics(javaFont);
		final Rectangle2D rect = fm.getStringBounds(watermark, g2d);
		final int height = (int) rect.getHeight();
		final int width = (int) rect.getWidth();
		if (height < 2 || width < 2) {
			return;
		}
		if (width <= maxWidth)
			for (int y = height; y < maxHeight; y += height + 1) {
				for (int x = 0; x < maxWidth; x += width + 10) {
					g2d.drawString(watermark, x, y);
				}
			}
		else {
			final List<String> withBreaks = withBreaks(watermark, javaFont, fm, maxWidth);
			int y = 0;
			while (y < maxHeight) {
				for (String s : withBreaks) {
					g2d.drawString(s, 0, y);
					y += (int) fm.getStringBounds(s, g2d).getHeight();
				}
				y += 10;
			}
		}
	}

	private int getWidth(String line, Font javaFont, FontMetrics fm) {
		final Rectangle2D rect = fm.getStringBounds(line, g2d);
		return (int) rect.getWidth();
	}

	private List<String> withBreaks(String watermark, Font javaFont, FontMetrics fm, int maxWidth) {
		final String[] words = watermark.split("\\s+");
		final List<String> result = new ArrayList<>();
		String pending = "";
		for (String word : words) {
			final String candidate = pending.length() == 0 ? word : pending + " " + word;
			if (getWidth(candidate, javaFont, fm) < maxWidth) {
				pending = candidate;
			} else {
				result.add(pending);
				pending = word;
			}
		}
		if (pending.length() > 0) {
			result.add(pending);
		}
		return result;
	}

	public BufferedImage getBufferedImage() {
		return im;
	}

	public Graphics2D getGraphics2D() {
		return g2d;
	}

	public UGraphicG2d getUGraphicG2d() {
		final HColor back = new HColorSimple(background, false);
		final UGraphicG2d result = new UGraphicG2d(back, new ColorMapperIdentity(), stringBounder, g2d, 1.0);
		result.setBufferedImage(im);
		return result;
	}

}
