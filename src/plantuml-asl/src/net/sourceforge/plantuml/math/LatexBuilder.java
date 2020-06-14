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
package net.sourceforge.plantuml.math;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

import net.sourceforge.plantuml.SvgString;

public class LatexBuilder implements ScientificEquation {

	private final String tex;

	public LatexBuilder(String tex) {
		this.tex = tex;
	}

	private Dimension2D dimension;

	public Dimension2D getDimension() {
		return dimension;
	}

	private Icon buildIcon(Color foregroundColor) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return new TeXIconBuilder(tex, foregroundColor).getIcon();
	}

	public SvgString getSvg(double scale, Color foregroundColor, Color backgroundColor) throws ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, InstantiationException, IOException {
		final Icon icon = buildIcon(foregroundColor);
		final ConverterSvg converterSvg = new ConverterSvg(icon);
		final String svg = converterSvg.getSvg(scale, true, backgroundColor);
		dimension = converterSvg.getDimension();
		return new SvgString(svg, scale);
	}

	public BufferedImage getImage(double scale, Color foregroundColor, Color backgroundColor)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Icon icon = buildIcon(foregroundColor);
		final BufferedImage image = new BufferedImage((int) (icon.getIconWidth() * scale),
				(int) (icon.getIconHeight() * scale), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = image.createGraphics();
		g2.scale(scale, scale);
		if (backgroundColor != null) {
			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		}
		icon.paintIcon(null, g2, 0, 0);
		return image;
	}

	public String getSource() {
		return tex;
	}

}
