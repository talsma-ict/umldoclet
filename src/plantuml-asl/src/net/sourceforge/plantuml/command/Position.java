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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.cucadiagram.Rankdir;

public enum Position {
	RIGHT, LEFT, BOTTOM, TOP;

	public static Position fromString(String s) {
		return Position.valueOf(s.toUpperCase());
	}

	public Position withRankdir(Rankdir rankdir) {
		if (rankdir == null) {
			throw new IllegalArgumentException();
		}
		if (rankdir == Rankdir.TOP_TO_BOTTOM) {
			// Default
			return this;
		}
		if (this == RIGHT) {
			return BOTTOM;
		}
		if (this == LEFT) {
			return TOP;
		}
		if (this == BOTTOM) {
			return RIGHT;
		}
		if (this == TOP) {
			return LEFT;
		}
		throw new IllegalStateException();
	}

	public Direction reverseDirection() {
		if (this == LEFT) {
			return Direction.RIGHT;
		}
		if (this == RIGHT) {
			return Direction.LEFT;
		}
		throw new UnsupportedOperationException();
	}
}
