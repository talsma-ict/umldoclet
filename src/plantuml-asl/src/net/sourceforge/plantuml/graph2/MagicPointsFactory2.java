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
package net.sourceforge.plantuml.graph2;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MagicPointsFactory2 {

	private final Point2D.Double p1;
	private final Point2D.Double p2;

	private final List<Point2D.Double> result = new ArrayList<Point2D.Double>();

	public MagicPointsFactory2(Point2D.Double p1, Point2D.Double p2) {
		this.p1 = p1;
		this.p2 = p2;
		final double dx = p2.x - p1.x;
		final double dy = p2.y - p1.y;

		final int interv = 5;
		final int intervAngle = 10;
		final double theta = Math.PI * 2 / intervAngle;
		for (int a = 0; a < 10; a++) {
			final AffineTransform at = AffineTransform.getRotateInstance(theta * a, p1.x, p1.y);
			for (int i = 0; i < interv * 2; i++) {
				final Point2D.Double p = new Point2D.Double(p1.x + dx * i / interv, p1.y + dy * i / interv);
				result.add((Point2D.Double) at.transform(p, null));
			}
		}

	}

	public List<Point2D.Double> get() {
		return result;
	}

}
