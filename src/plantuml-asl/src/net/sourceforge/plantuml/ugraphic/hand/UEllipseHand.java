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
package net.sourceforge.plantuml.ugraphic.hand;

import java.awt.geom.Point2D;
import java.util.Random;

import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class UEllipseHand {

	private Shadowable poly;
	private final Random rnd;

	private double randomMe() {
		return rnd.nextDouble();
	}

	public UEllipseHand(UEllipse source, Random rnd) {
		this.rnd = rnd;
		if (source.getStart() != 0 || source.getExtend() != 0) {
			this.poly = source;
			return;
		}
		poly = new UPolygon();
		final double width = source.getWidth();
		final double height = source.getHeight();
		double angle = 0;
		if (width == height) {
			while (angle < Math.PI * 2) {
				angle += (10 + randomMe() * 10) * Math.PI / 180;
				final double variation = 1 + (randomMe() - 0.5) / 8;
				final double x = width / 2 + Math.cos(angle) * width * variation / 2;
				final double y = height / 2 + Math.sin(angle) * height * variation / 2;
				// final Point2D.Double p = new Point2D.Double(x, y);
				((UPolygon) poly).addPoint(x, y);
			}
		} else {
			while (angle < Math.PI * 2) {
				angle += Math.PI / 20;
				final Point2D pt = getPoint(width, height, angle);
				((UPolygon) poly).addPoint(pt.getX(), pt.getY());
			}

		}

		this.poly.setDeltaShadow(source.getDeltaShadow());
	}

	private Point2D getPoint(double width, double height, double angle) {
		final double x = width / 2 + Math.cos(angle) * width / 2;
		final double y = height / 2 + Math.sin(angle) * height / 2;
		final double variation = (randomMe() - 0.5) / 50;
		return new Point2D.Double(x + variation * width, y + variation * height);

	}

	public Shadowable getHanddrawn() {
		return this.poly;
	}

}
