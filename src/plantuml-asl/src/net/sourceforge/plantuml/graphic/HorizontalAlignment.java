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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.StringUtils;

public enum HorizontalAlignment {

	LEFT, CENTER, RIGHT;

	public static HorizontalAlignment fromString(String s) {
		if (LEFT.name().equalsIgnoreCase(s)) {
			return LEFT;
		}
		if (CENTER.name().equalsIgnoreCase(s)) {
			return CENTER;
		}
		if (RIGHT.name().equalsIgnoreCase(s)) {
			return RIGHT;
		}
		return null;
	}

	public static HorizontalAlignment fromString(String s, HorizontalAlignment defaultValue) {
		if (defaultValue == null) {
			throw new IllegalArgumentException();
		}
		if (s == null) {
			return defaultValue;
		}
		s = StringUtils.goUpperCase(s);
		final HorizontalAlignment result = fromString(s);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	public String getGraphVizValue() {
		return toString().substring(0, 1).toLowerCase();
	}

}
