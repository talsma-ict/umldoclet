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
 */
package net.sourceforge.plantuml.posimo;

import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.geom.XLine2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.geom.XRectangle2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;

public class RacorderOrthogonal extends RacorderAbstract implements Racorder {

	@Override
	public DotPath getRacordIn(XRectangle2D rect, XLine2D tangeante) {

		final XPoint2D in = tangeante.getP1();

		final DotPath result = new DotPath();
		XPoint2D inter = null;

		if (in.getX() > rect.getMinX() && in.getX() < rect.getMaxX()) {
			if (in.getY() < rect.getMinY())
				inter = new XPoint2D(in.getX(), rect.getMinY());
			else if (in.getY() > rect.getMaxY())
				inter = new XPoint2D(in.getX(), rect.getMaxY());
			else
				throw new IllegalArgumentException();

		} else if (in.getY() > rect.getMinY() && in.getY() < rect.getMaxY()) {
			if (in.getX() < rect.getMinX())
				inter = new XPoint2D(rect.getMinX(), in.getY());
			else if (in.getX() > rect.getMaxX())
				inter = new XPoint2D(rect.getMaxX(), in.getY());
			else
				throw new IllegalArgumentException();

		} else {
			final XPoint2D p1 = new XPoint2D(rect.getMinX(), rect.getMinY());
			final XPoint2D p2 = new XPoint2D(rect.getMaxX(), rect.getMinY());
			final XPoint2D p3 = new XPoint2D(rect.getMaxX(), rect.getMaxY());
			final XPoint2D p4 = new XPoint2D(rect.getMinX(), rect.getMaxY());

			inter = LineRectIntersection.getCloser(tangeante.getP1(), p1, p2, p3, p4);

		}

		final XCubicCurve2D curv = new XCubicCurve2D(tangeante.getX1(), tangeante.getY1(), tangeante.getX1(),
				tangeante.getY1(), inter.getX(), inter.getY(), inter.getX(), inter.getY());
		return result.addAfter(curv);
	}

}
