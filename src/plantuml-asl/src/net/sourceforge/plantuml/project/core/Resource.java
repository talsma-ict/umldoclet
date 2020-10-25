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

public class Resource {

	private final String name;
	private ResourceDraw draw;
	private final Set<Day> closed = new TreeSet<Day>();
	private final Set<Day> forcedOn = new TreeSet<Day>();

	private final Collection<DayOfWeek> closedDayOfWeek = EnumSet.noneOf(DayOfWeek.class);

	public Resource(String name, LoadPlanable loadPlanable) {
		this.name = name;
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

	public boolean isClosedAt(Day instant) {
		if (this.forcedOn.contains(instant)) {
			return false;
		}
		if (closedDayOfWeek.size() > 0) {
			if (closedDayOfWeek.contains(instant.getDayOfWeek())) {
				return true;
			}
		}
		return this.closed.contains(instant);
	}

	public void addCloseDay(Day instant) {
		this.closed.add(instant);
	}

	public void addForceOnDay(Day instant) {
		this.forcedOn.add(instant);
	}

	public void addCloseDay(DayOfWeek dayOfWeek) {
		closedDayOfWeek.add(dayOfWeek);
	}
}
