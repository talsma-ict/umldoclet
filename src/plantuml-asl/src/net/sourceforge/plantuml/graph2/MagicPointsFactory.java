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
package net.sourceforge.plantuml.graph2;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MagicPointsFactory {

	private MagicPointsFactory() {

	}

	public static List<Point2D.Double> get(Rectangle2D.Double rect) {
		final List<Point2D.Double> result = new ArrayList<Point2D.Double>();
		result.add(new Point2D.Double(rect.x - rect.width, rect.y - rect.height));
		result.add(new Point2D.Double(rect.x, rect.y - rect.height));
		result.add(new Point2D.Double(rect.x + rect.width, rect.y - rect.height));
		result.add(new Point2D.Double(rect.x + 2 * rect.width, rect.y - rect.height));

		result.add(new Point2D.Double(rect.x - rect.width, rect.y));
		result.add(new Point2D.Double(rect.x + 2 * rect.width, rect.y));

		result.add(new Point2D.Double(rect.x - rect.width, rect.y + rect.height));
		result.add(new Point2D.Double(rect.x + 2 * rect.width, rect.y + rect.height));

		result.add(new Point2D.Double(rect.x - rect.width, rect.y + 2 * rect.height));
		result.add(new Point2D.Double(rect.x, rect.y + 2 * rect.height));
		result.add(new Point2D.Double(rect.x + rect.width, rect.y + 2 * rect.height));
		result.add(new Point2D.Double(rect.x + 2 * rect.width, rect.y + 2 * rect.height));
		return result;
	}

	public static List<Point2D.Double> get(Point2D.Double p1, Point2D.Double p2) {
		final List<Point2D.Double> result = new ArrayList<Point2D.Double>();
		result.add(new Point2D.Double(p1.x, p2.y));
		result.add(new Point2D.Double(p2.x, p1.y));
		return result;
	}
}
