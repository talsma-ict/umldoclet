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
package net.sourceforge.plantuml.jsondiagram;

import java.awt.geom.Point2D;

public class Mirror {

	private final double max;

	public Mirror(double max) {
		this.max = max;
	}

	public double inv(double v) {
		if (v < 0 || v > max) {
			System.err.println("BAD VALUE IN Mirror");
		}
		return max - v;
	}

	public Point2D invAndXYSwitch(Point2D pt) {
		final double x = inv(pt.getY());
		final double y = pt.getX();
		return new Point2D.Double(x, y);
	}

	public Point2D invGit(Point2D pt) {
		final double x = pt.getX();
		final double y = inv(pt.getY());
		return new Point2D.Double(x, y);
	}

}
