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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.StringUtils;

public class Ident implements Comparable<Ident> {

	private final String ident;

	private Ident(String ident) {
		if (ident == null) {
			throw new IllegalArgumentException();
		}
		this.ident = ident;
	}

	public static Ident of(String code) {
		return new Ident(code);
	}

	@Override
	public String toString() {
		return ident;
	}

	@Override
	public int hashCode() {
		return ident.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Ident other = (Ident) obj;
		return this.ident.equals(other.ident);
	}

	public int compareTo(Ident other) {
		return this.ident.compareTo(other.ident);
	}

	private Ident eventuallyRemoveStartingAndEndingDoubleQuote() {
		return Ident.of(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(ident));
	}

}
