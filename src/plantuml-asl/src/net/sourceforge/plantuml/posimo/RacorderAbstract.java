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
import java.awt.geom.Rectangle2D;

public abstract class RacorderAbstract implements Racorder {

	public final DotPath getRacordOut(Rectangle2D rect, Line2D tangeante) {
		tangeante = symetric(tangeante);
		return getRacordIn(rect, tangeante).reverse();
	}

	private static Line2D symetric(Line2D line) {
		final double x1 = line.getX1();
		final double y1 = line.getY1();
		final double x2 = line.getX2();
		final double y2 = line.getY2();
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		return new Line2D.Double(x1, y1, x1 - dx, y1 - dy);
	}

}
