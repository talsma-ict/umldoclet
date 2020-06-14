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

import java.util.Map;
import java.util.TreeMap;

public class TimeScaleBasic2 implements TimeScale {

	private final GCalendar calendar;
	private final GCalendar calendarAllOpen;
	private final TimeScaleBasic basic = new TimeScaleBasic();
	private final Map<Instant, Instant> cache = new TreeMap<Instant, Instant>();

	public TimeScaleBasic2(GCalendarSimple calendar) {
		this.calendar = calendar;
		this.calendarAllOpen = calendar;
	}

	private Instant changeInstantSlow(Instant instant) {
		final DayAsDate day = calendar.toDayAsDate((InstantDay) instant);
		return calendarAllOpen.fromDayAsDate(day);
	}

	private Instant changeInstant(Instant instant) {
		Instant result = cache.get(instant);
		if (result == null) {
			result = changeInstantSlow(instant);
			cache.put(instant, result);
		}
		return result;
	}

	public double getStartingPosition(Instant instant) {
		return basic.getStartingPosition(changeInstant(instant));
	}

	public double getEndingPosition(Instant instant) {
		return basic.getEndingPosition(changeInstant(instant));
	}

	public double getWidth(Instant instant) {
		return basic.getWidth(changeInstant(instant));
	}

}
