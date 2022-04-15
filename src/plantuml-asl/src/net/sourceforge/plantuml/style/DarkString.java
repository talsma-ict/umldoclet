/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.style;

public class DarkString {

	private final String value1;
	private final String value2;
	private final int priority;

	public DarkString(String value1, String value2, int priority) {
		this.value1 = value1;
		this.value2 = value2;
		this.priority = priority;
	}

	public DarkString mergeWith(DarkString other) {
		if (other == null)
			return this;

		if ((this.value2 == null && other.value2 == null) || this.value1 == null && other.value1 == null) {
			if (isBigger(this.priority, other.priority))
				return this;
			return other;
		}
		if (this.value2 == null && other.value1 == null)
			return new DarkString(this.value1, other.value2, this.priority);
		if (other.value2 == null && this.value1 == null)
			return new DarkString(other.value1, this.value2, other.priority);

		if (isBigger(this.priority, other.priority))
			return this;
		return other;

//		System.err.println("this =" + this);
//		System.err.println("other=" + other);
//		throw new UnsupportedOperationException();
	}

	private static boolean isBigger(int a, int b) {
//		if (a > StyleLoader.DELTA_PRIORITY_FOR_STEREOTYPE)
//			a = StyleLoader.DELTA_PRIORITY_FOR_STEREOTYPE;
//		if (b > StyleLoader.DELTA_PRIORITY_FOR_STEREOTYPE)
//			b = StyleLoader.DELTA_PRIORITY_FOR_STEREOTYPE;
		return a > b;
	}

	public DarkString addPriority(int delta) {
		return new DarkString(value1, value2, delta + priority);
	}

	@Override
	public String toString() {
		return value1 + "/" + value2 + " (" + priority + ")";
	}

	public final String getValue1() {
		return value1;
	}

	public final String getValue2() {
		return value2;
	}

	public final int getPriority() {
		return priority;
	}

}
