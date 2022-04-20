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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Color;

public class ShadowManager {
	
	// http://www.w3schools.com/svg/svg_feoffset.asp

	private final int c1;
	private final int c2;

	public ShadowManager(int c1, int c2) {
		this.c1 = c1;
		this.c2 = c2;
	}

	public double[] getShadowDeltaPoints(double deltaShadow, double diff, double[] points) {
		assert points.length % 2 == 0;
		double cx = 0;
		double cy = 0;
		for (int i = 0; i < points.length; i += 2) {
			cx += points[i];
			cy += points[i + 1];
		}
		final int nbPoints = points.length / 2;

		cx = cx / nbPoints;
		cy = cy / nbPoints;

		final double[] result = new double[points.length];
		for (int i = 0; i < result.length; i += 2) {
			final double diffx = points[i] > cx ? -diff : diff;
			final double diffy = points[i + 1] > cy ? -diff : diff;
			result[i] = points[i] + diffx + deltaShadow;
			result[i + 1] = points[i + 1] + diffy + deltaShadow;
		}
		return result;
	}

	public Color getColor(double delta, double total) {
		final int c = (int) (c2 + 1.0 * delta / total * (c1 - c2));
		return new Color(c, c, c);
	}

}
