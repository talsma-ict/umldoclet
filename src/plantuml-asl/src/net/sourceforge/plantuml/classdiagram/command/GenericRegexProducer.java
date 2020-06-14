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
package net.sourceforge.plantuml.classdiagram.command;


public class GenericRegexProducer {

	public final static String PATTERN = "[^\\<\\>/]" + getGenericRegex(4);

	// \<[^\<\>]([^\<\>]|\<\>)*\>
	static final private String part1 = "(?:[^\\<\\>/]|\\<";
	static final private String part2 = "\\>)*";

	static String getGenericRegex(int level) {
		if (level < 0) {
			throw new IllegalArgumentException();
		}
		if (level == 0) {
			return part1 + part2;
		}
		return part1 + getGenericRegex(level - 1) + part2;
	}

}
