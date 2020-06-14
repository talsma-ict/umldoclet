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
package net.sourceforge.plantuml.code;

public class SpiralOnRectangle {

	private final Spiral spiral = new Spiral();
	private final int width;
	private final int height;
	private final PairInt delta;

	public SpiralOnRectangle(int width, int height) {
		this.width = width;
		this.height = height;
		this.delta = new PairInt(width / 2, height / 2);
	}

	private boolean inside(PairInt point) {
		final int x = point.getX();
		final int y = point.getY();
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public PairInt nextPoint() {
		do {
			final PairInt result = spiral.nextPoint().plus(delta);
			if (inside(result)) {
				return result;
			}
		} while (true);
	}
}
