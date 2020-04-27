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
package net.sourceforge.plantuml.geom;

import java.awt.geom.Point2D;

public class Point2DInt extends Point2D implements Pointable {

	private final int x;
	private final int y;

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public Point2DInt(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getXint() {
		return x;
	}

	public int getYint() {
		return y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		throw new UnsupportedOperationException();
	}

	public Point2DInt getPosition() {
		return this;
	}

	public Point2DInt translate(int deltaX, int deltaY) {
		return new Point2DInt(x + deltaX, y + deltaY);
	}

	public Point2DInt inflateX(int xpos, int inflation) {
		if (inflation % 2 != 0) {
			throw new IllegalArgumentException();
		}
		if (x < xpos) {
			return this;
		}
		if (x == xpos) {
			// throw new IllegalArgumentException();
			return translate(inflation / 2, 0);
		}
		return translate(inflation, 0);
	}

	public Point2DInt inflateX(InflateData inflateData) {
		return inflateX(inflateData.getPos(), inflateData.getInflation());
	}

	public Point2DInt inflateY(InflateData inflateData) {
		return inflateY(inflateData.getPos(), inflateData.getInflation());
	}

	public Point2DInt inflateY(int ypos, int inflation) {
		if (inflation % 2 != 0) {
			throw new IllegalArgumentException();
		}
		if (y < ypos) {
			return this;
		}
		if (y == ypos) {
			// throw new IllegalArgumentException();
			return translate(0, inflation / 2);
		}
		return translate(0, inflation);
	}

}
