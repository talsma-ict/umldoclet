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
package net.sourceforge.plantuml.sequencediagram;

public enum GroupingType {
	START, ELSE, END;
	public static GroupingType getType(String s) {
		if (s.equalsIgnoreCase("opt")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("alt")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("loop")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("par")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("par2")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("break")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("group")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("critical")) {
			return GroupingType.START;
		} else if (s.equalsIgnoreCase("also")) {
			return GroupingType.ELSE;
		} else if (s.equalsIgnoreCase("else")) {
			return GroupingType.ELSE;
		} else if (s.equalsIgnoreCase("end")) {
			return GroupingType.END;
		}

		throw new IllegalArgumentException();
	}
}
