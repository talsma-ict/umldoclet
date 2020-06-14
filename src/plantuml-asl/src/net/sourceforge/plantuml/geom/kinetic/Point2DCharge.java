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
package net.sourceforge.plantuml.geom.kinetic;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.Log;

public class Point2DCharge extends Point2D.Double {

	private double charge = 1.0;

	private MoveObserver moveObserver = null;

	public Point2DCharge(double x, double y) {
		super(x, y);
	}

	public Point2DCharge(Point2D pt, double ch) {
		super(pt.getX(), pt.getY());
		this.charge = ch;
	}

	public void apply(VectorForce value) {
		Log.println("Applying " + value);
		x += value.getX();
		y += value.getY();
		if (moveObserver != null) {
			moveObserver.pointMoved(this);
		}
	}

	@Override
	final public void setLocation(double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	final public void setLocation(Point2D p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return System.identityHashCode(this) + " " + String.format("[%8.2f %8.2f]", x, y);
	}

	public final double getCharge() {
		return charge;
	}

	public final void setCharge(double charge) {
		this.charge = charge;
	}

	private final int hash = System.identityHashCode(this);

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	public final void setMoveObserver(MoveObserver moveObserver) {
		this.moveObserver = moveObserver;
	}

}
