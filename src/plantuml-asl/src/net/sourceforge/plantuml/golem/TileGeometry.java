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
package net.sourceforge.plantuml.golem;

import net.sourceforge.plantuml.StringUtils;

public enum TileGeometry {
	NORTH, SOUTH, EAST, WEST, CENTER;

	public TileGeometry opposite() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		case CENTER:
		default:
			throw new UnsupportedOperationException();
		}
	}

	public static TileGeometry fromString(String s) {
		final char c = StringUtils.goUpperCase(s.charAt(0));
		switch (c) {
		case 'N':
			return NORTH;
		case 'S':
			return SOUTH;
		case 'E':
			return EAST;
		case 'W':
			return WEST;
		default:
			throw new IllegalArgumentException();
		}
	}

}
