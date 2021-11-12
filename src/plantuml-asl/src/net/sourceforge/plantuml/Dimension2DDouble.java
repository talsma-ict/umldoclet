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
package net.sourceforge.plantuml;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.utils.MathUtils;

public class Dimension2DDouble extends Dimension2D {

	final private double width;
	final private double height;

	public Dimension2DDouble(double width, double height) {
		if (Double.isNaN(width) || Double.isNaN(height)) {
			throw new IllegalArgumentException();
		}
		this.width = width;
		this.height = height;
	}

	public Dimension2DDouble(Point2D point) {
		this(point.getX(), point.getY());
	}

	@Override
	public String toString() {
		return "[" + width + "," + height + "]";
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setSize(double width, double height) {
		throw new UnsupportedOperationException();
	}

	public static Dimension2D delta(Dimension2D dim, double delta) {
		return delta(dim, delta, delta);
	}

	public Dimension2DDouble withWidth(double newWidth) {
		return new Dimension2DDouble(newWidth, height);
	}

	public static Dimension2D delta(Dimension2D dim, double deltaWidth, double deltaHeight) {
		if (deltaHeight == 0 && deltaWidth == 0) {
			return dim;
		}
		return new Dimension2DDouble(dim.getWidth() + deltaWidth, dim.getHeight() + deltaHeight);
	}

	public static Dimension2D mergeTB(Dimension2D top, Dimension2D bottom) {
		final double width = Math.max(top.getWidth(), bottom.getWidth());
		final double height = top.getHeight() + bottom.getHeight();
		return new Dimension2DDouble(width, height);
	}

	public static Dimension2D mergeTB(Dimension2D a, Dimension2D b, Dimension2D c) {
		final double width = MathUtils.max(a.getWidth(), b.getWidth(), c.getWidth());
		final double height = a.getHeight() + b.getHeight() + c.getHeight();
		return new Dimension2DDouble(width, height);
	}

	public static Dimension2D mergeLR(Dimension2D left, Dimension2D right) {
		final double height = Math.max(left.getHeight(), right.getHeight());
		final double width = left.getWidth() + right.getWidth();
		return new Dimension2DDouble(width, height);
	}

	public static Dimension2D mergeLayoutT12B3(Dimension2D top1, Dimension2D top2, Dimension2D bottom) {
		final double width = MathUtils.max(top1.getWidth(), top2.getWidth(), bottom.getWidth());
		final double height = top1.getHeight() + top2.getHeight() + bottom.getHeight();
		return new Dimension2DDouble(width, height);
	}

	public static Dimension2D max(Dimension2D dim1, Dimension2D dim2) {
		return atLeast(dim1, dim2.getWidth(), dim2.getHeight());
	}

	public static Dimension2D atLeast(Dimension2D dim, double minWidth, double minHeight) {
		double h = dim.getHeight();
		double w = dim.getWidth();
		if (w > minWidth && h > minHeight) {
			return dim;
		}
		if (h < minHeight) {
			h = minHeight;
		}
		if (w < minWidth) {
			w = minWidth;
		}
		return new Dimension2DDouble(w, h);
	}

}
