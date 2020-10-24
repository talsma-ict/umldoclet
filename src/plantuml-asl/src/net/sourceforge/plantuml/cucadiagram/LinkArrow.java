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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.svek.GuideLine;

public enum LinkArrow {

	NONE_OR_SEVERAL, DIRECT_NORMAL, BACKWARD;

	public LinkArrow reverse() {
		if (this == DIRECT_NORMAL) {
			return BACKWARD;
		}
		if (this == BACKWARD) {
			return DIRECT_NORMAL;
		}
		return NONE_OR_SEVERAL;
	}

	public GuideLine mute(final GuideLine guide) {
		switch (this) {
		case DIRECT_NORMAL:
			return guide;
		case BACKWARD:
			return new GuideLine() {
				public Direction getArrowDirection() {
					return guide.getArrowDirection().getInv();
				}

				public double getArrowDirection2() {
					return Math.PI + guide.getArrowDirection2();
				}
			};

		}
		throw new UnsupportedOperationException();
	}

}
