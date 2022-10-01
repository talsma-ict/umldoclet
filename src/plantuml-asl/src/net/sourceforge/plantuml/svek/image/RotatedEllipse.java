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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.ugraphic.UEllipse;

public class RotatedEllipse {

	private final UEllipse ellipse;
	private final double beta;

	public RotatedEllipse(UEllipse ellipse, double beta) {
		this.ellipse = ellipse;
		this.beta = beta;
	}

	public double getA() {
		return ellipse.getWidth() / 2;
	}

	public double getB() {
		return ellipse.getHeight() / 2;
	}

	public double getBeta() {
		return beta;
	}

	public XPoint2D getPoint(double theta) {
		final double x = getA() * Math.cos(theta);
		final double y = getB() * Math.sin(theta);

		final double xp = x * Math.cos(beta) - y * Math.sin(beta);
		final double yp = x * Math.sin(beta) + y * Math.cos(beta);

		return new XPoint2D(xp, yp);
	}

	public double getOtherTheta(double theta1) {
		final double z = getPoint(theta1).getX();
		final double a = getA() * Math.cos(beta);
		final double b = getB() * Math.sin(beta);
		final double sum = 2 * a * z / (a * a + b * b);
		final double other = sum - Math.cos(theta1);
		return -Math.acos(other);
	}

	private double other(double[] all, double some) {
		final double diff0 = Math.abs(some - all[0]);
		final double diff1 = Math.abs(some - all[1]);

		if (diff0 > diff1) {
			return all[0];
		}
		return all[1];
	}

}
