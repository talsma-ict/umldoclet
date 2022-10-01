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
package net.sourceforge.plantuml.posimo;

import net.sourceforge.plantuml.awt.geom.XLine2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;

public class LineRectIntersection {

	private final XPoint2D inter;

	public LineRectIntersection(XLine2D line, XRectangle2D rect) {
		final XPoint2D p1 = new XPoint2D(rect.getMinX(), rect.getMinY());
		final XPoint2D p2 = new XPoint2D(rect.getMaxX(), rect.getMinY());
		final XPoint2D p3 = new XPoint2D(rect.getMaxX(), rect.getMaxY());
		final XPoint2D p4 = new XPoint2D(rect.getMinX(), rect.getMaxY());

		final XPoint2D inter1 = new LineSegmentIntersection(new XLine2D(p1, p2), line).getIntersection();
		final XPoint2D inter2 = new LineSegmentIntersection(new XLine2D(p2, p3), line).getIntersection();
		final XPoint2D inter3 = new LineSegmentIntersection(new XLine2D(p3, p4), line).getIntersection();
		final XPoint2D inter4 = new LineSegmentIntersection(new XLine2D(p4, p1), line).getIntersection();

		final XPoint2D o = line.getP1();
		inter = getCloser(o, inter1, inter2, inter3, inter4);

	}

	public static XPoint2D getCloser(final XPoint2D o, final XPoint2D... other) {
		double minDist = Double.MAX_VALUE;
		XPoint2D result = null;

		for (XPoint2D pt : other)
			if (pt != null) {
				final double dist = pt.distanceSq(o);
				if (dist < minDist) {
					minDist = dist;
					result = pt;
				}
			}

		return result;
	}

	public final XPoint2D getIntersection() {
		return inter;
	}

}
