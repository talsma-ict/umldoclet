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
 * Creator:  Hisashi Miyashita
 */
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.klimt.UBackground;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;

abstract class ExtremityExtendsLike extends Extremity {
	private static final double XLEN = -19;// 8 * 2.4;
	private static final double HALF_WIDTH = 7;// 3 * 2.4;

	private final UPolygon trig;
	private final UBackground back;
	private final XPoint2D contact;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	private static class Point {
		public double x;
		public double y;

		public void rotate(double theta) {
			double ct = Math.cos(theta);
			double st = -Math.sin(theta);
			double nx = x * ct - y * st;
			y = -x * st - y * ct;
			x = nx;
		}

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public UTranslate getPos(XPoint2D pt) {
			return new UTranslate(x + pt.getX(), y + pt.getY());
		}

		public void translate(XPoint2D pt) {
			x += pt.getX();
			y += pt.getY();
		}

		public void add(UPolygon p) {
			p.addPoint(x, y);
		}
	}

	static class Redefines extends ExtremityExtendsLike {
		private static final double XSUFFIX = XLEN * 1.2;
		private final UStroke barStroke = UStroke.withThickness(2.0);
		private final UTranslate pos;
		private final ULine bar;

		public Redefines(XPoint2D porig, double angle, HColor backgroundColor) {
			super(porig, angle, backgroundColor);

			Point p1 = new Point(XSUFFIX, -HALF_WIDTH);
			Point p2 = new Point(XSUFFIX, +HALF_WIDTH);
			p1.rotate(angle);
			p2.rotate(angle);
			this.bar = new ULine(p2.x - p1.x, p2.y - p1.y);
			this.pos = p1.getPos(porig);
		}

		public void drawU(UGraphic ug) {
			super.drawU(ug);
			ug.apply(barStroke).apply(pos).draw(bar);
		}
	}

	static class DefinedBy extends ExtremityExtendsLike {
		private static final double XSUFFIX = XLEN * 1.3;
		private static final double DOTHSIZE = 2;
		private final UTranslate pos1, pos2;
		private final UEllipse dot;

		private static UTranslate getDotPos(double x, double y, double angle, double size, XPoint2D porig) {
			Point p = new Point(x, y);
			p.rotate(angle);
			p.x -= size;
			p.y -= size;
			return p.getPos(porig);
		}

		public DefinedBy(XPoint2D porig, double angle, HColor backgroundColor) {
			super(porig, angle, backgroundColor);
			double w = HALF_WIDTH - DOTHSIZE;

			this.pos1 = getDotPos(XSUFFIX, -w, angle, DOTHSIZE, porig);
			this.pos2 = getDotPos(XSUFFIX, +w, angle, DOTHSIZE, porig);

			double s = DOTHSIZE + DOTHSIZE;
			this.dot = UEllipse.build(s, s);
		}

		public void drawU(UGraphic ug) {
			super.drawU(ug);
			if (ug.getParam().getColor() != null) {
				ug = ug.apply(ug.getParam().getColor().bg());
			}
			ug.apply(pos1).draw(dot);
			ug.apply(pos2).draw(dot);
		}
	}

	private static void addTrigPoint(UPolygon up, double x, double y, double angle, XPoint2D porig) {
		Point p = new Point(x, y);
		p.rotate(angle);
		p.translate(porig);
		p.add(up);
	}

	private ExtremityExtendsLike(XPoint2D porig, double angle, HColor backgroundColor) {
		this.back = backgroundColor.bg();
		this.contact = new XPoint2D(porig.getX(), porig.getY());
		angle = manageround(angle);

		this.trig = new UPolygon();
		trig.addPoint(porig);
		addTrigPoint(trig, XLEN, -HALF_WIDTH, angle, porig);
		addTrigPoint(trig, XLEN, +HALF_WIDTH, angle, porig);
		trig.addPoint(porig);
	}

	public void drawU(UGraphic ug) {
		ug.apply(back).draw(trig);
	}

}
