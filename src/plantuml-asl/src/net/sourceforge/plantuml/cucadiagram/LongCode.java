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

public class LongCode implements Comparable<LongCode> {

	private final String fullName;
	private final String separator;

	private LongCode(String fullName, String separator) {
		if (fullName == null) {
			throw new IllegalArgumentException();
		}
		this.fullName = fullName;
		this.separator = separator;
	}

	public String getNamespaceSeparator() {
		return separator;
	}

	public static LongCode of(String code, String separator) {
		if (code == null) {
			throw new IllegalStateException();
		}
		return new LongCode(code, separator);
	}

	public final String getFullName() {
		return fullName;
	}

	@Override
	public String toString() {
		return fullName + "(" + separator + ")";
	}

	@Override
	public int hashCode() {
		return fullName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final LongCode other = (LongCode) obj;
		return this.fullName.equals(other.fullName);
	}

	public int compareTo(LongCode other) {
		return this.fullName.compareTo(other.fullName);
	}

	private LongCode eventuallyRemoveStartingAndEndingDoubleQuote() {
		return LongCode.of(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(fullName), separator);
	}

}
