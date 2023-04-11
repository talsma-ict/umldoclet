/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.posimo;

import java.util.Locale;

import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class Block implements Clusterable {

	private final int uid;
	private final double width;
	private final double height;
	private double x;
	private double y;
	private final Cluster parent;

	public Block(int uid, double width, double height, Cluster parent) {
		this.uid = uid;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "BLOCK " + uid;
	}

	public String toStringPosition() {
		return String.format(Locale.US, "x=%9.2f y=%9.2f w=%9.2f h=%9.2f", x, y, width, height);
	}

	public int getUid() {
		return uid;
	}

	public Cluster getParent() {
		return parent;
	}

	public XPoint2D getPosition() {
		return new XPoint2D(x, y);
	}

	public XDimension2D getSize() {
		return new XDimension2D(width, height);
	}

	public void setCenterX(double center) {
		this.x = center - width / 2;
	}

	public void setCenterY(double center) {
		this.y = center - height / 2;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public void moveSvek(double deltaX, double deltaY) {
		throw new UnsupportedOperationException();
	}

}
