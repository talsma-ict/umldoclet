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
package net.sourceforge.plantuml.png;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import net.sourceforge.plantuml.ugraphic.UAntiAliasing;

public class PngFlashcoder {

	private final List<BufferedImage> flashcodes;

	public PngFlashcoder(List<BufferedImage> flashcodes) {
		this.flashcodes = flashcodes;
	}

	public BufferedImage processImage(BufferedImage im, Color background) {
		if (flashcodes != null) {
			im = addImage(im, background);
		}
		return im;

	}

	private BufferedImage addImage(BufferedImage im, Color background) {

		final double width = Math.max(im.getWidth(), getWidth(flashcodes));
		final double height = im.getHeight() + getHeight(flashcodes);

		final BufferedImage newIm = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = newIm.createGraphics();

		UAntiAliasing.ANTI_ALIASING_OFF.apply(g2d);
		g2d.setColor(background);
		g2d.fillRect(0, 0, newIm.getWidth(), newIm.getHeight());
		g2d.drawImage(im, null, 0, 0);
		int x = 0;
		for (BufferedImage f : flashcodes) {
			g2d.drawImage(f, null, x, (int) im.getHeight());
			x += f.getWidth();
		}
		g2d.dispose();
		return newIm;

	}

	public static int getHeight(List<BufferedImage> flashcodes) {
		int result = 0;
		for (BufferedImage im : flashcodes) {
			result = Math.max(result, im.getWidth());
		}
		return result;
	}

	public static int getWidth(List<BufferedImage> flashcodes) {
		int result = 0;
		for (BufferedImage im : flashcodes) {
			result += im.getWidth();
		}
		return result;
	}
}
