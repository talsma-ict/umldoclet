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
package net.sourceforge.plantuml.awt.geom;

import net.sourceforge.plantuml.awt.Shape;

public class Rectangle2D implements Shape {

	public final double x;
	public final double y;
	public final double width;
	public final double height;

	public Rectangle2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}

	public static class Double extends Rectangle2D {

		public Double(double x, double y, double width, double height) {
			super(x, y, width, height);

		}

	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getCenterX() {
		return x;
	}

	public double getCenterY() {
		return y;
	}

	public double getMinX() {
		return x;
	}

	public double getMaxX() {
		return x + width;
	}

	public double getMinY() {
		return y;
	}

	public double getMaxY() {
		return y + height;
	}

	public boolean intersects(Rectangle2D rectangle) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Point2D point) {
		// TODO Auto-generated method stub
		return false;
	}

}
