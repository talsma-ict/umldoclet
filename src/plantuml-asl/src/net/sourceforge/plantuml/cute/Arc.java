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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;

public class Arc {

	private final Segment segment;
	private final Tension tension;

	public Tension getTension() {
		return tension;
	}

	public Arc(final MyPoint2D a, final MyPoint2D b) {
		this(a, b, Tension.none());
	}

	private Arc(final MyPoint2D a, final MyPoint2D b, Tension tension) {
		this.segment = new Segment(a, b);
		this.tension = tension;
	}

	public MyPoint2D getA() {
		return (MyPoint2D) segment.getA();
	}

	public MyPoint2D getB() {
		return (MyPoint2D) segment.getB();
	}

	public Arc withNoTension() {
		return new Arc(getA(), getB(), Tension.none());
	}

	public Arc withTension(String tensionString) {
		if (tensionString == null) {
			return this;
		}
		final double newTension = Double.parseDouble(tensionString);
		return new Arc(getA(), getB(), new Tension(newTension));
	}

	public Arc rotateZoom(RotationZoom rotationZoom) {
		return new Arc(getA().rotateZoom(rotationZoom), getB().rotateZoom(rotationZoom),
				tension.rotateZoom(rotationZoom));
	}

//	public void appendTo(UPath path) {
//		if (tension.isNone()) {
//			path.lineTo(getB());
//		} else {
//			final double a = segment.getLength() / 2;
//			final double b = getTension().getValue();
//			final double radius = (a * a + b * b) / 2 / b;
//			final int sweep_flag = 1;
//			path.arcTo(getB(), radius, 0, sweep_flag);
//		}
//	}

	public Point2D getTensionPoint() {
		if (tension.isNone()) {
			throw new IllegalArgumentException();
		}
		return segment.getOrthoPoint(-tension.getValue());
	}

	// public void appendTo(UPath path) {
	// if (path.isEmpty()) {
	// path.moveTo(getA());
	// }
	// path.lineTo(getB());
	// }

	public double getLength() {
		return segment.getLength();
	}

}
