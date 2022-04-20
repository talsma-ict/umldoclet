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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;

public enum NoteLinkStrategy {
	NORMAL, HALF_PRINTED_FULL, HALF_NOT_PRINTED;

	public Dimension2D computeDimension(double width, double height) {
		if (this == HALF_PRINTED_FULL) {
			return new Dimension2DDouble(width / 2, height);
		}
		if (this == HALF_NOT_PRINTED) {
			return new Dimension2DDouble(0, 0);
		}
		return new Dimension2DDouble(width, height);
	}
}
