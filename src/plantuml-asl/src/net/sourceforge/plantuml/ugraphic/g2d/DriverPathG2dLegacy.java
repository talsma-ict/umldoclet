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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import net.sourceforge.plantuml.golem.MinMaxDouble;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.USegment;
import net.sourceforge.plantuml.ugraphic.USegmentType;
import net.sourceforge.plantuml.ugraphic.UShape;

public class DriverPathG2dLegacy extends DriverShadowedG2d implements UDriver<Graphics2D> {

	private final double dpiFactor;

	public DriverPathG2dLegacy(double dpiFactor) {
		this.dpiFactor = dpiFactor;
	}

	public void draw(UShape ushape, final double x, final double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		final UPath shape = (UPath) ushape;
		DriverLineG2d.manageStroke(param, g2d);

		final Path2D.Double p = new Path2D.Double();
		boolean hasBezier = false;
		final MinMaxDouble minMax = new MinMaxDouble();
		minMax.manage(x, y);
		for (USegment seg : shape) {
			final USegmentType type = seg.getSegmentType();
			final double coord[] = seg.getCoord();
			if (type == USegmentType.SEG_MOVETO) {
				p.moveTo(x + coord[0], y + coord[1]);
				minMax.manage(x + coord[0], y + coord[1]);
			} else if (type == USegmentType.SEG_LINETO) {
				p.lineTo(x + coord[0], y + coord[1]);
				minMax.manage(x + coord[0], y + coord[1]);
			} else if (type == USegmentType.SEG_CUBICTO) {
				p.curveTo(x + coord[0], y + coord[1], x + coord[2], y + coord[3], x + coord[4], y + coord[5]);
				minMax.manage(x + coord[4], y + coord[5]);
				hasBezier = true;
			} else {
				throw new UnsupportedOperationException();
			}
		}

		if (shape.isOpenIconic()) {
			p.closePath();
			g2d.setColor(mapper.getMappedColor(param.getColor()));
			g2d.fill(p);
			return;
		}

		// Shadow
		if (shape.getDeltaShadow() != 0) {
			if (hasBezier) {
				drawShadow(g2d, p, shape.getDeltaShadow(), dpiFactor);
			} else {
				double lastX = 0;
				double lastY = 0;
				for (USegment seg : shape) {
					final USegmentType type = seg.getSegmentType();
					final double coord[] = seg.getCoord();
					// Cast float for Java 1.5
					if (type == USegmentType.SEG_MOVETO) {
						lastX = x + coord[0];
						lastY = y + coord[1];
					} else if (type == USegmentType.SEG_LINETO) {
						final Shape line = new Line2D.Double(lastX, lastY, x + coord[0], y + coord[1]);
						drawShadow(g2d, line, shape.getDeltaShadow(), dpiFactor);
						lastX = x + coord[0];
						lastY = y + coord[1];
					} else {
						throw new UnsupportedOperationException();
					}
				}
			}
		}

		final HtmlColor back = param.getBackcolor();
		if (back instanceof HtmlColorGradient) {
			final HtmlColorGradient gr = (HtmlColorGradient) back;
			final char policy = gr.getPolicy();
			final GradientPaint paint;
			if (policy == '|') {
				paint = new GradientPaint((float) minMax.getMinX(), (float) minMax.getMaxY() / 2,
						mapper.getMappedColor(gr.getColor1()), (float) minMax.getMaxX(), (float) minMax.getMaxY() / 2,
						mapper.getMappedColor(gr.getColor2()));
			} else if (policy == '\\') {
				paint = new GradientPaint((float) minMax.getMinX(), (float) minMax.getMaxY(), mapper.getMappedColor(gr
						.getColor1()), (float) minMax.getMaxX(), (float) minMax.getMinY(), mapper.getMappedColor(gr
						.getColor2()));
			} else if (policy == '-') {
				paint = new GradientPaint((float) minMax.getMaxX() / 2, (float) minMax.getMinY(),
						mapper.getMappedColor(gr.getColor1()), (float) minMax.getMaxX() / 2, (float) minMax.getMaxY(),
						mapper.getMappedColor(gr.getColor2()));
			} else {
				// for /
				paint = new GradientPaint((float) x, (float) y, mapper.getMappedColor(gr.getColor1()),
						(float) minMax.getMaxX(), (float) minMax.getMaxY(), mapper.getMappedColor(gr.getColor2()));
			}
			g2d.setPaint(paint);
			g2d.fill(p);
		} else if (back != null) {
			g2d.setColor(mapper.getMappedColor(back));
			g2d.fill(p);
		}

		if (param.getColor() != null) {
			g2d.setColor(mapper.getMappedColor(param.getColor()));
			g2d.draw(p);
		}
	}

}
