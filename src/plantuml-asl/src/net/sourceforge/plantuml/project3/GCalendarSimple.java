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

public class GCalendarSimple implements GCalendar {

	private final DayAsDate start;

	public GCalendarSimple(DayAsDate start) {
		this.start = start;
	}

	public DayAsDate toDayAsDate(InstantDay day) {
		DayAsDate result = start;
		final int target = day.getNumDay();
		int work = 0;
		while (work < target) {
			result = result.next();
			work++;
		}
		return result;
	}

	public InstantDay fromDayAsDate(DayAsDate day) {
		if (day.compareTo(start) < 0) {
			throw new IllegalArgumentException();
		}
		InstantDay result = new InstantDay(0);
		while (toDayAsDate(result).equals(day) == false) {
			result = result.increment();
		}
		return result;
	}

	public DayAsDate getStartingDate() {
		return start;
	}

}
