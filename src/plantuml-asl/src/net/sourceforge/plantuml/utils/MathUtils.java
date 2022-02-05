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
package net.sourceforge.plantuml.utils;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.Dimension2DDouble;

public class MathUtils {

	public static double max(double a, double b) {
		return Math.max(a, b);
	}

	public static double max(double a, double b, double c) {
		return max(max(a, b), c);
	}

	public static double max(double a, double b, double c, double d) {
		return max(max(a, b), max(c, d));
	}

	public static double max(double a, double b, double c, double d, double e) {
		return max(max(a, b, c), max(d, e));
	}

	public static double min(double a, double b) {
		return Math.min(a, b);
	}

	public static double min(double a, double b, double c) {
		return min(min(a, b), c);
	}

	public static double min(double a, double b, double c, double d) {
		return min(min(a, b), min(c, d));
	}

	public static double min(double a, double b, double c, double d, double e) {
		return min(min(a, b, c), min(d, e));
	}

	public static double limitation(double v, double min, double max) {
		if (min >= max) {
			// assert false : "min="+min+" max="+max+" v="+v;
			return v;
			// throw new IllegalArgumentException("min="+min+" max="+max+" v="+v);
		}
		if (v < min) {
			return min;
		}
		if (v > max) {
			return max;
		}
		return v;
	}

	public static Dimension2D max(Dimension2D dim1, Dimension2D dim2) {
		return new Dimension2DDouble(Math.max(dim1.getWidth(), dim2.getWidth()),
				Math.max(dim1.getHeight(), dim2.getHeight()));
	}

	public static Dimension2D max(Dimension2D dim1, Dimension2D dim2, Dimension2D dim3) {
		return new Dimension2DDouble(MathUtils.max(dim1.getWidth(), dim2.getWidth(), dim3.getWidth()),
				MathUtils.max(dim1.getHeight(), dim2.getHeight(), dim3.getHeight()));
	}

	public static Point2D max(Point2D pt1, Point2D pt2) {
		return new Point2D.Double(Math.max(pt1.getX(), pt2.getX()), Math.max(pt1.getY(), pt2.getY()));
	}

	public static Point2D max(Point2D pt1, Point2D pt2, Point2D pt3) {
		return new Point2D.Double(max(pt1.getX(), pt2.getX(), pt3.getX()), max(pt1.getY(), pt2.getY(), pt3.getY()));
	}

}
