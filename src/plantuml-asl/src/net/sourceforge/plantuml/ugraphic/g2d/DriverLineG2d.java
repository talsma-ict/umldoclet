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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;

import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class DriverLineG2d extends DriverShadowedG2d implements UDriver<Graphics2D> {

	private final double dpiFactor;

	public DriverLineG2d(double dpiFactor) {
		this.dpiFactor = dpiFactor;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		final ULine shape = (ULine) ushape;

		final Shape line = new Line2D.Double(x, y, x + shape.getDX(), y + shape.getDY());
		manageStroke(param, g2d);
		// Shadow
		if (shape.getDeltaShadow() != 0) {
			drawShadow(g2d, line, shape.getDeltaShadow(), dpiFactor);
		}
		final HColor color = param.getColor();
		DriverRectangleG2d.drawBorder(param, color, mapper, shape, line, g2d, x, y);
//		g2d.setColor(mapper.getMappedColor(color));
//		g2d.draw(line);
	}

	static void manageStroke(UParam param, Graphics2D g2d) {
		final UStroke stroke = param.getStroke();
		final float thickness = (float) (stroke.getThickness() * param.getScale());
		if (stroke.getDashVisible() == 0) {
			g2d.setStroke(new BasicStroke(thickness));
		} else {
			final float dash1 = (float) stroke.getDashVisible();
			final float dash2 = (float) stroke.getDashSpace();
			final float[] style = { dash1, dash2 };
			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, style, 0));
		}
	}
}
