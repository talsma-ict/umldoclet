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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverPolygonG2d extends DriverShadowedG2d implements UDriver<UPolygon, Graphics2D> {

	private final double dpiFactor;
	private final EnsureVisible visible;

	public DriverPolygonG2d(double dpiFactor, EnsureVisible visible) {
		this.dpiFactor = dpiFactor;
		this.visible = visible;
	}

	public void draw(UPolygon shape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		g2d.setStroke(new BasicStroke((float) param.getStroke().getThickness()));

		final GeneralPath path = new GeneralPath();

		final HColor back = param.getBackcolor();

		XPoint2D last = null;
		for (XPoint2D pt : shape.getPoints()) {
			final double xp = pt.getX() + x;
			final double yp = pt.getY() + y;
			visible.ensureVisible(xp, yp);
			if (last == null)
				path.moveTo((float) xp, (float) yp);
			else
				path.lineTo((float) xp, (float) yp);

			last = new XPoint2D(xp, yp);
		}

		if (last != null)
			path.closePath();

		if (shape.getDeltaShadow() != 0)
			if (back.isTransparent())
				drawOnlyLineShadowSpecial(g2d, path, shape.getDeltaShadow(), dpiFactor);
			else
				drawShadow(g2d, path, shape.getDeltaShadow(), dpiFactor);

		if (back instanceof HColorGradient) {
			final HColorGradient gr = (HColorGradient) back;
			final char policy = gr.getPolicy();
			final GradientPaint paint;
			if (policy == '|')
				paint = new GradientPaint((float) x, (float) (y + shape.getHeight()) / 2,
						gr.getColor1().toColor(mapper), (float) (x + shape.getWidth()),
						(float) (y + shape.getHeight()) / 2, gr.getColor2().toColor(mapper));
			else if (policy == '\\')
				paint = new GradientPaint((float) x, (float) (y + shape.getHeight()), gr.getColor1().toColor(mapper),
						(float) (x + shape.getWidth()), (float) y, gr.getColor2().toColor(mapper));
			else if (policy == '-')
				paint = new GradientPaint((float) (x + shape.getWidth()) / 2, (float) y, gr.getColor1().toColor(mapper),
						(float) (x + shape.getWidth()) / 2, (float) (y + shape.getHeight()),
						gr.getColor2().toColor(mapper));
			else
				// for /
				paint = new GradientPaint((float) x, (float) y, gr.getColor1().toColor(mapper),
						(float) (x + shape.getWidth()), (float) (y + shape.getHeight()),
						gr.getColor2().toColor(mapper));

			g2d.setPaint(paint);
			g2d.fill(path);
		} else if (back.isTransparent() == false) {
			g2d.setColor(back.toColor(mapper));
			DriverRectangleG2d.managePattern(param, g2d);
			g2d.fill(path);
		}

		if (param.getColor().isTransparent() == false) {
			g2d.setColor(param.getColor().toColor(mapper));
			DriverLineG2d.manageStroke(param, g2d);
			g2d.draw(path);
		}
	}

}
