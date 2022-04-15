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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Point2D;

public class Oscillator {

	private int n = 3;
	private int i = 0;
	private char seg = 'A';

	private int x = -1;
	private int y = -1;

	public Point2D.Double nextPosition() {
		assert n % 2 == 1;
		final int halfN = (n - 1) / 2;
		final Point2D.Double result = new Point2D.Double(x, y);
		i++;
		if (seg == 'A') {
			x++;
			if (x > halfN) {
				seg = 'B';
				x = halfN;
				y = -halfN + 1;
			}
		} else if (seg == 'B') {
			y++;
			if (y > halfN) {
				seg = 'C';
				x = halfN - 1;
				y = halfN;
			}
		} else if (seg == 'C') {
			x--;
			if (x < -halfN) {
				seg = 'D';
				x = -halfN;
				y = halfN - 1;
			}
		} else if (seg == 'D') {
			y--;
			if (y == -halfN) {
				n += 2;
				i = 0;
				x = -((n - 1) / 2);
				y = x;
				seg = 'A';
			}
		} else {
			throw new UnsupportedOperationException();
		}
		return result;
	}
}
