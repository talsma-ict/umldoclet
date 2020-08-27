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
package net.sourceforge.plantuml.project.core;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.project.LoadPlanable;
import net.sourceforge.plantuml.project.draw.ResourceDraw;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.project.time.GCalendar;
import net.sourceforge.plantuml.project.time.Wink;

public class Resource {

	private final String name;
	private ResourceDraw draw;
	private final Set<Wink> closed = new TreeSet<Wink>();
	private final Set<Wink> forcedOn = new TreeSet<Wink>();
	private final GCalendar calendar;

	private final Collection<DayOfWeek> closedDayOfWeek = EnumSet.noneOf(DayOfWeek.class);

	public Resource(String name, LoadPlanable loadPlanable, GCalendar calendar) {
		this.name = name;
		this.calendar = calendar;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Resource other = (Resource) obj;
		return this.name.equals(other.name);
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public ResourceDraw getResourceDraw() {
		return draw;
	}

	public void setTaskDraw(ResourceDraw draw) {
		this.draw = draw;
	}

	public boolean isClosedAt(Wink instant) {
		if (this.forcedOn.contains(instant)) {
			return false;
		}
		if (closedDayOfWeek.size() > 0 && calendar != null) {
			final Day d = calendar.toDayAsDate((Wink) instant);
			if (closedDayOfWeek.contains(d.getDayOfWeek())) {
				return true;
			}
		}
		return this.closed.contains(instant);
	}

	public void addCloseDay(Wink instant) {
		this.closed.add(instant);
	}

	public void addForceOnDay(Wink instant) {
		this.forcedOn.add(instant);
	}

	public void addCloseDay(DayOfWeek dayOfWeek) {
		closedDayOfWeek.add(dayOfWeek);
	}
}
