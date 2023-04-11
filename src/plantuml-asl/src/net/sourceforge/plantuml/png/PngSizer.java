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
package net.sourceforge.plantuml.png;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.utils.Log;

public class PngSizer {

	static public BufferedImage process(BufferedImage im, int minsize) {

		if (minsize != Integer.MAX_VALUE) {
			return resize(im, minsize);
		}
		return im;

	}

	static private BufferedImage resize(BufferedImage im, int minsize) {
		Log.info("Resizing file to " + minsize);

		if (im.getWidth() >= minsize) {
			return im;
		}

		final BufferedImage newIm = new BufferedImage(minsize, im.getHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = newIm.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, newIm.getWidth(), newIm.getHeight());
		final int delta = (minsize - im.getWidth()) / 2;
		g2d.drawImage(im, delta, 0, null);

		g2d.dispose();

		return newIm;

	}

}
