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

import java.util.Set;
import java.util.TreeSet;

public class Resource implements Subject /* , LoadPlanable */{

	private final String name;
	private ResourceDraw draw;
	// private final LoadPlanable loadPlanable;
	private Set<Instant> closed = new TreeSet<Instant>();

	public Resource(String name, LoadPlanable loadPlanable) {
		this.name = name;
		// this.loadPlanable = loadPlanable;
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

	public boolean isClosedAt(Instant instant) {
		 return this.closed.contains(instant);
	}

//	public int getLoadAt(Instant instant) {
//		if (this.closed.contains(instant)) {
//			return 0;
//		}
//		return loadPlanable.getLoadAt(instant);
//	}

	public void addCloseDay(Instant instant) {
		this.closed.add(instant);
	}
}
