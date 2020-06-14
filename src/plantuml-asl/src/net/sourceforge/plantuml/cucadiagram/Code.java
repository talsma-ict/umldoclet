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

public class Code implements Comparable<Code> {

	private final String fullName;
	private final String separator;

	private Code(String fullName, String separator) {
		if (fullName == null) {
			throw new IllegalArgumentException();
		}
		this.fullName = fullName;
		this.separator = separator;
	}

	public Code removeMemberPart() {
		final int x = fullName.lastIndexOf("::");
		if (x == -1) {
			return null;
		}
		return new Code(fullName.substring(0, x), separator);
	}

	public String getPortMember() {
		final int x = fullName.lastIndexOf("::");
		if (x == -1) {
			return null;
		}
		return fullName.substring(x + 2);
	}

	public Code withSeparator(String separator) {
		if (separator == null) {
			throw new IllegalArgumentException();
		}
		if (this.separator != null && this.separator.equals(separator) == false) {
			throw new IllegalStateException();
		}
		return new Code(fullName, separator);
	}

	public static Code of(String code) {
		return of(code, null);
	}

	public static Code of(String code, String separator) {
		if (code == null) {
			return null;
		}
		return new Code(code, separator);
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
		final Code other = (Code) obj;
		return this.fullName.equals(other.fullName);
	}

	public Code addSuffix(String suffix) {
		return new Code(fullName + suffix, separator);
	}

	public int compareTo(Code other) {
		return this.fullName.compareTo(other.fullName);
	}

	public Code eventuallyRemoveStartingAndEndingDoubleQuote(String format) {
		return Code.of(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(fullName, format), separator);
	}

	public final String getSeparator() {
		return separator;
	}

}
