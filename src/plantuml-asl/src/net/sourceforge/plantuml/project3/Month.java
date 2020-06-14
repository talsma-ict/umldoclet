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
package net.sourceforge.plantuml.project3;

import net.sourceforge.plantuml.StringUtils;

public enum Month {

	JANUARY(31), FEBRUARY(28), MARCH(31), APRIL(30), MAY(31), JUNE(30), //
	JULY(31), AUGUST(31), SEPTEMBER(30), OCTOBER(31), NOVEMBER(30), DECEMBER(31);

	private final int daysPerMonth;

	private Month(int daysPerMonth) {
		this.daysPerMonth = daysPerMonth;
	}

	static public String getRegexString() {
		final StringBuilder sb = new StringBuilder();
		for (Month month : Month.values()) {
			if (sb.length() > 0) {
				sb.append("|");
			}
			sb.append(month.name().substring(0, 3) + "[a-z]*");
		}
		return sb.toString();
	}

	public static Month fromString(String value) {
		value = StringUtils.goUpperCase(value).substring(0, 3);
		for (Month m : Month.values()) {
			if (m.name().startsWith(value)) {
				return m;
			}
		}
		throw new IllegalArgumentException();
	}

	public final int getDaysPerMonth(int year) {
		if (this == FEBRUARY && year % 4 == 0) {
			return 29;
		}
		return daysPerMonth;
	}

	public Month next() {
		return Month.values()[(this.ordinal() + 1) % 12];
	}

	public int m() {
		return 3 + (ordinal() + 10) % 12;
	}
}
