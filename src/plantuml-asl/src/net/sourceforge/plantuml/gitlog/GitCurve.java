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
package net.sourceforge.plantuml.gitlog;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import h.ST_Agedgeinfo_t;
import h.ST_bezier;
import h.ST_pointf;
import h.ST_splines;
import net.sourceforge.plantuml.jsondiagram.Arrow;
import net.sourceforge.plantuml.jsondiagram.Mirror;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GitCurve {

	private final List<Point2D> points = new ArrayList<>();

	private final Mirror xMirror;

	private final Point2D sp;
	private final Point2D ep;

	public GitCurve(ST_Agedgeinfo_t data, Mirror xMirror) {
		this.xMirror = xMirror;
		final ST_splines splines = data.spl;
		if (splines.size != 1) {
			throw new IllegalStateException();
		}
		final ST_bezier beziers = splines.list.get__(0);
		for (int i = 0; i < beziers.size; i++) {
			final Point2D pt = getPoint(splines, i);
			points.add(pt);
		}

		if (beziers.sp.x == 0 && beziers.sp.y == 0) {
			sp = null;
		} else {
			sp = new Point2D.Double(beziers.sp.x, beziers.sp.y);
		}
		if (beziers.ep.x == 0 && beziers.ep.y == 0) {
			ep = null;
		} else {
			ep = new Point2D.Double(beziers.ep.x, beziers.ep.y);
		}

	}

	private Point2D getPoint(ST_splines splines, int i) {
		final ST_bezier beziers = splines.list.get__(0);
		final ST_pointf pt = beziers.list.get__(i);
		return new Point2D.Double(pt.x, pt.y);
	}

	public void drawCurve(HColor color, UGraphic ug) {
		ug = ug.apply(new UStroke(2, 2, 1));
		final UPath path = new UPath();

		path.moveTo(xMirror.invGit(points.get(0)));

		for (int i = 1; i < points.size(); i += 3) {
			final Point2D pt2 = xMirror.invGit(points.get(i));
			final Point2D pt3 = xMirror.invGit(points.get(i + 1));
			final Point2D pt4 = xMirror.invGit(points.get(i + 2));
			path.cubicTo(pt2, pt3, pt4);
		}
		ug.draw(path);

		if (ep != null) {
			final Point2D last = xMirror.invGit(points.get(points.size() - 1));
			final Point2D trueEp = xMirror.invGit(ep);
			new Arrow(last, trueEp).drawArrow(ug.apply(color.bg()));
		}
	}

}
