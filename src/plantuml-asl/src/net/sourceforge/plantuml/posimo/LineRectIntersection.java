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
package net.sourceforge.plantuml.posimo;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LineRectIntersection {

	private final Point2D inter;

	public LineRectIntersection(Line2D line, Rectangle2D rect) {
		final Point2D p1 = new Point2D.Double(rect.getMinX(), rect.getMinY());
		final Point2D p2 = new Point2D.Double(rect.getMaxX(), rect.getMinY());
		final Point2D p3 = new Point2D.Double(rect.getMaxX(), rect.getMaxY());
		final Point2D p4 = new Point2D.Double(rect.getMinX(), rect.getMaxY());

		final Point2D inter1 = new LineSegmentIntersection(new Line2D.Double(p1, p2), line).getIntersection();
		final Point2D inter2 = new LineSegmentIntersection(new Line2D.Double(p2, p3), line).getIntersection();
		final Point2D inter3 = new LineSegmentIntersection(new Line2D.Double(p3, p4), line).getIntersection();
		final Point2D inter4 = new LineSegmentIntersection(new Line2D.Double(p4, p1), line).getIntersection();

		final Point2D o = line.getP1();
		inter = getCloser(o, inter1, inter2, inter3, inter4);

	}

	public static Point2D getCloser(final Point2D o, final Point2D... other) {
		double minDist = Double.MAX_VALUE;
		Point2D result = null;

		for (Point2D pt : other) {
			if (pt != null) {
				final double dist = pt.distanceSq(o);
				if (dist < minDist) {
					minDist = dist;
					result = pt;
				}
			}
		}

		return result;
	}

	public final Point2D getIntersection() {
		return inter;
	}

}
