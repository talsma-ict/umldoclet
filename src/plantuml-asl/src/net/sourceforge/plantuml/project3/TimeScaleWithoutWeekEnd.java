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

public class TimeScaleWithoutWeekEnd implements TimeScale {

	private final double scale = 16.0;
	private final GCalendar calendar;

	public TimeScaleWithoutWeekEnd(GCalendar calendar) {
		if (calendar == null) {
			throw new IllegalArgumentException();
		}
		this.calendar = calendar;
	}

	public double getStartingPosition(Instant instant) {
		double result = 0;
		InstantDay current = (InstantDay) instant;
		while (current.getNumDay() > 0) {
			current = current.decrement();
			result += getWidth(current);
		}
		return result;
	}

	public double getWidth(Instant instant) {
		final DayAsDate day = calendar.toDayAsDate((InstantDay) instant);
		final DayOfWeek dayOfWeek = day.getDayOfWeek();
		if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
			return 1;
		}
		return scale;
	}

	public double getEndingPosition(Instant instant) {
		throw new UnsupportedOperationException();
	}

}
