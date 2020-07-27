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
package net.sourceforge.plantuml.math;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Dimension2D;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Icon;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ConverterSvg {

	private final Icon icon;

	public ConverterSvg(Icon icon) {
		this.icon = icon;
	}

	static {
		try {
			// DefaultTeXFont.registerAlphabet(new CyrillicRegistration());
			// DefaultTeXFont.registerAlphabet(new GreekRegistration());
			final Class<?> clDefaultTeXFont = Class.forName("org.scilab.forge.jlatexmath.DefaultTeXFont");
			final Class<?> clAlphabetRegistration = Class.forName("org.scilab.forge.jlatexmath.AlphabetRegistration");
			final Method registerAlphabet = clDefaultTeXFont.getMethod("registerAlphabet", clAlphabetRegistration);
			registerAlphabet.invoke(null, Class.forName("org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration")
					.newInstance());
			registerAlphabet.invoke(null, Class.forName("org.scilab.forge.jlatexmath.greek.GreekRegistration")
					.newInstance());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Dimension dimension;

	public String getSvg(double scale, boolean fontAsShapes, Color backgroundColor) throws ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, InstantiationException, IOException {
		// DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		final Class<?> clGenericDOMImplementation = Class.forName("org.apache.batik.dom.GenericDOMImplementation");
		final DOMImplementation domImpl = (DOMImplementation) clGenericDOMImplementation.getMethod(
				"getDOMImplementation").invoke(null);
		final String svgNS = "http://www.w3.org/2000/svg";
		final Document document = domImpl.createDocument(svgNS, "svg", null);

		// SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
		final Class<?> clSVGGeneratorContext = Class.forName("org.apache.batik.svggen.SVGGeneratorContext");
		final Object ctx = clSVGGeneratorContext.getMethod("createDefault", Document.class).invoke(null, document);

		// SVGGraphics2D g2 = new SVGGraphics2D(ctx, fontAsShapes);
		final Class<?> clSVGGraphics2D = Class.forName("org.apache.batik.svggen.SVGGraphics2D");
		final Graphics g2 = (Graphics) clSVGGraphics2D.getConstructor(clSVGGeneratorContext, boolean.class)
				.newInstance(ctx, fontAsShapes);

		dimension = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		// g2.setSVGCanvasSize(dimension);
		g2.getClass().getMethod("setSVGCanvasSize", Dimension.class).invoke(g2, dimension);
		if (backgroundColor != null) {
			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		}

		icon.paintIcon(null, g2, 0, 0);

		final Writer out = new CharArrayWriter();

		final boolean useCSS = true;
		// g2.stream(out, useCSS);
		g2.getClass().getMethod("stream", Writer.class, boolean.class).invoke(g2, out, useCSS);

		out.flush();
		out.close();

		return out.toString();
	}

	public Dimension2D getDimension() {
		return dimension;
	}

}
