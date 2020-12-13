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
package net.sourceforge.plantuml.project.time;

import net.sourceforge.plantuml.project.Value;

public class Instant implements Comparable<Instant>, Value {

	private final long ms;

	public static Instant create(long ms) {
		return new Instant(ms);
	}

	public static Instant today() {
		return create(System.currentTimeMillis());
	}

	private Instant(long ms) {
		this.ms = ms;
	}

	public final long getMillis() {
		return ms;
	}

	@Override
	public String toString() {
		return "" + ms;
	}

	@Override
	public int hashCode() {
		return toLong().hashCode();
	}

	private Long toLong() {
		return new Long(ms);
	}

	@Override
	public boolean equals(Object obj) {
		final Instant other = (Instant) obj;
		return this.ms == other.ms;
	}

	public int compareTo(Instant other) {
		return toLong().compareTo(other.toLong());
	}

}
