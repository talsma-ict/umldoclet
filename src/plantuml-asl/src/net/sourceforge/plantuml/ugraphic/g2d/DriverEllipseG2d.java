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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverEllipseG2d extends DriverShadowedG2d implements UDriver<Graphics2D> {

	private final double dpiFactor;
	private final EnsureVisible visible;

	public DriverEllipseG2d(double dpiFactor, EnsureVisible visible) {
		this.dpiFactor = dpiFactor;
		this.visible = visible;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		final UEllipse ellipse = (UEllipse) ushape;
		g2d.setStroke(new BasicStroke((float) param.getStroke().getThickness()));
		visible.ensureVisible(x, y);
		visible.ensureVisible(x + ellipse.getWidth(), y + ellipse.getHeight());
		final HColor color = param.getColor();
		if (ellipse.getStart() == 0 && ellipse.getExtend() == 0) {
			final Shape shape = new Ellipse2D.Double(x, y, ellipse.getWidth(), ellipse.getHeight());

			// Shadow
			if (ellipse.getDeltaShadow() != 0) {
				drawShadow(g2d, shape, ellipse.getDeltaShadow(), dpiFactor);
			}

			final HColor back = param.getBackcolor();
			if (back instanceof HColorGradient) {
				final GradientPaint paint = DriverRectangleG2d.getPaintGradient(x, y, mapper, ellipse.getWidth(),
						ellipse.getHeight(), back);
				g2d.setPaint(paint);
				g2d.fill(shape);
				DriverRectangleG2d.drawBorder(param, color, mapper, ellipse, shape, g2d, x, y);
			} else {
				if (back != null) {
					g2d.setColor(mapper.toColor(param.getBackcolor()));
					DriverRectangleG2d.managePattern(param, g2d);
					g2d.fill(shape);
				}
				if (color != null && color.equals(param.getBackcolor()) == false) {
					DriverRectangleG2d.drawBorder(param, color, mapper, ellipse, shape, g2d, x, y);
				}
			}
		} else {
			final Shape arc = new Arc2D.Double(x, y, ellipse.getWidth(), ellipse.getHeight(),
					round(ellipse.getStart()), round(ellipse.getExtend()), Arc2D.OPEN);
			if (color != null) {
				g2d.setColor(mapper.toColor(color));
				g2d.draw(arc);
			}
		}
	}

	private static final double ROU = 5.0;

	static double round(double value) {
		return value;
		// final int v = (int) Math.round(value / ROU);
		// return v * ROU;
	}

}
