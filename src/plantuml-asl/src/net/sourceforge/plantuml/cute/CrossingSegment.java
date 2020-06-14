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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.geom.LineSegmentDouble;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CrossingSegment {

	private final Balloon balloon;
	private final LineSegmentDouble segment;

	public CrossingSegment(Balloon balloon, LineSegmentDouble segment) {
		this.balloon = balloon;
		this.segment = segment;
	}

	public List<Point2D> intersection() {
		final List<Point2D> result = new ArrayList<Point2D>();

		final UTranslate tr = new UTranslate(balloon.getCenter());
		final UTranslate trInverse = tr.reverse();

		final CrossingSimple simple = new CrossingSimple(balloon.getRadius(),
				new InfiniteLine(segment).translate(trInverse));
		for (Point2D pt : simple.intersection()) {
			pt = tr.getTranslated(pt);
			if (segment.isPointOnSegment(pt)) {
				result.add(pt);
			}
		}

		return result;
	}

}
