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
package net.sourceforge.plantuml.graph;

import java.awt.geom.Point2D;

public class ElectricCharge {

	private boolean moveable;
	private final Point2D position;
	private final double charge;

	public ElectricCharge(double x, double y, double charge) {
		this.position = new Point2D.Double(x, y);
		this.charge = charge;
	}

	public Point2D getPosition() {
		return position;
	}

	public double getCharge() {
		return charge;
	}

	public final boolean isMoveable() {
		return moveable;
	}

	public final void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public void move(double deltax, double deltay) {
		position.setLocation(position.getX() + deltax, position.getY() + deltay);
	}
}
