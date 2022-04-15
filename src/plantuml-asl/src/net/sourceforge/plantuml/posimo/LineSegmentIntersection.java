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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LineSegmentIntersection {

	private final Point2D inter;
	
	// http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/

	public LineSegmentIntersection(Line2D segment, Line2D lineB) {
		final double x1 = segment.getX1();
		final double y1 = segment.getY1();
		final double x2 = segment.getX2();
		final double y2 = segment.getY2();
		final double x3 = lineB.getX1();
		final double y3 = lineB.getY1();
		final double x4 = lineB.getX2();
		final double y4 = lineB.getY2();

		final double den = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

		if (den == 0) {
			inter = null;
		} else {

			final double uA1 = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
			final double uA = uA1 / den;

			final double x = x1 + uA * (x2 - x1);
			final double y = y1 + uA * (y2 - y1);

			if (uA >= 0 && uA <= 1) {
				inter = new Point2D.Double(x, y);
			} else {
				inter = null;
			}
		}
	}

	public final Point2D getIntersection() {
		return inter;
	}

}
