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
package net.sourceforge.plantuml.skin.bluemodern;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class FillRoundShape {

	final private double width;
	final private double height;
	final private double corner;
	final private HtmlColor c1;
	final private HtmlColor c2;

	public FillRoundShape(double width, double height, HtmlColor c1, HtmlColor c2, double corner) {
		this.width = width;
		this.height = height;
		this.c1 = c1;
		this.c2 = c2;
		this.corner = corner;

	}

	public void draw(ColorMapper mapper, Graphics2D g2d) {
		final GradientPaint paint = new GradientPaint(0, 0, mapper.getMappedColor(c1), (float) width, (float) height,
				mapper.getMappedColor(c2));
		final RoundRectangle2D r = new RoundRectangle2D.Double(0, 0, width, height, corner * 2, corner * 2);
		g2d.setPaint(paint);
		g2d.fill(r);
	}

	public void drawU(UGraphic ug) {
		final HtmlColorGradient gradient = new HtmlColorGradient(c1, c2, '\\');
		final URectangle r = new URectangle(width, height, corner * 2, corner * 2);
		ug.apply(new UChangeBackColor(gradient)).draw(r);
	}

}
